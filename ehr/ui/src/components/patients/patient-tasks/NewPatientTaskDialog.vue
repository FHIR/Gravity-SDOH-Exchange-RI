<script lang="ts">
import { defineComponent, ref, computed, watch } from "vue";
import { RuleItem } from "async-validator";

type FormModel = {
	name: string,
	type: TaskType | "",
	code: string,
	status: string,
	priority: string,
	occurrence: string,
	comment: string,
	// additional type related fields
	questionnaireType: string,
	questionnaireFormat: string,
	questionnaireId: string
};

const DEFAULT_REQUIRED_RULE = {
	required: true,
	message: "This field is required"
};

type TaskType = "MAKE_CONTACT" | "COMPLETE_SR_QUESTIONNAIRE" | "PROVIDE_FEEDBACK";

const TYPE_CODE_MAP: Record<TaskType, { code: string, display: string }> = {
	MAKE_CONTACT: {
		code: "make-contact",
		display: "Make Contact"
	},
	COMPLETE_SR_QUESTIONNAIRE: {
		code: "complete-questionnaire",
		display: "Complete Questionnaire"
	},
	PROVIDE_FEEDBACK: {
		code: "adhoc",
		display: "Adhoc"
	}
};

export default defineComponent({
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	emits: ["close"],
	setup(_, { emit }) {
		const saveInProgress = ref<boolean>(false);
		const typeOptions = ref<{ label: string, value: TaskType }[]>([{
			label: "Complete questionnaire regarding social risks",
			value: "COMPLETE_SR_QUESTIONNAIRE"
		}, {
			label: "Provide feedback on service delivered",
			value: "PROVIDE_FEEDBACK"
		}]);
		const statusOptions = ref<{ label: string, value: string }[]>([{
			label: "Ready",
			value: "ready"
		}]);
		// todo: probably will be BE call for list of questionnaires
		const questionnaireOptions = ref<{ label: string, value: string }[]>([{
			label: "Hunger Vital Signs",
			value: "27867"
		}]);
		const formModel = ref<FormModel>({
			name: "",
			type: "",
			code: "",
			status: "Ready",
			priority: "Routine",
			occurrence: "",
			comment: "",
			questionnaireType: "",
			questionnaireFormat: "FHIR_QUESTIONNAIRE",
			questionnaireId: ""
		});
		const formEl = ref<HTMLFormElement>();
		const formRules: { [field: string]: RuleItem & { trigger?: string } } = {
			name: DEFAULT_REQUIRED_RULE,
			type: DEFAULT_REQUIRED_RULE,
			priority: DEFAULT_REQUIRED_RULE,
			occurrence: DEFAULT_REQUIRED_RULE,
			questionnaireFormat: DEFAULT_REQUIRED_RULE,
			questionnaireId: DEFAULT_REQUIRED_RULE
		};
		const formHasChanges = computed<boolean>(() =>
			(
				formModel.value.name !== "" ||
				formModel.value.type !== "" ||
				formModel.value.code !== "" ||
				formModel.value.status !== "Ready" ||
				formModel.value.priority !== "Routine" ||
				formModel.value.occurrence !== "Routine" ||
				formModel.value.comment !== ""
			)
		);

		watch(() => formModel.value.type, val => {
			if (val === "COMPLETE_SR_QUESTIONNAIRE") {
				formModel.value.code = TYPE_CODE_MAP[val].code;
			}
		});
		watch(() => formModel.value.questionnaireId, () => {
			// todo: based on chosen questionnaire set correct type
			formModel.value.questionnaireType = "RISK_QUESTIONNAIRE";
		});

		const onDialogClose = () => {
			formEl.value?.resetFields();
			emit("close");
		};
		const onDialogOpen = () => {
			// todo: fetch needed info here
		};
		const onFormSave = async () => {
			await formEl.value?.validate();
			// todo: create new patient task
			emit("close");
		};

		return {
			onDialogClose,
			onDialogOpen,
			formEl,
			saveInProgress,
			onFormSave,
			formModel,
			formRules,
			typeOptions,
			statusOptions,
			formHasChanges,
			questionnaireOptions
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="Add Patient Task"
		:width="700"
		destroy-on-close
		custom-class="patient-task-dialog"
		@close="onDialogClose"
		@open="onDialogOpen"
	>
		<el-form
			ref="formEl"
			:model="formModel"
			:rules="formRules"
			label-width="160px"
			label-position="left"
			size="mini"
			class="new-patient-task-form"
		>
			<el-form-item
				label="Task Name"
				prop="name"
			>
				<el-input
					v-model="formModel.name"
					placeholder="Add name"
				/>
			</el-form-item>
			<el-form-item
				label="Type"
				prop="type"
			>
				<el-select
					v-model="formModel.type"
					placeholder="Select type"
				>
					<el-option
						v-for="item in typeOptions"
						:key="item.value"
						:label="item.label"
						:value="item.value"
					/>
				</el-select>
			</el-form-item>
			<el-form-item
				label="Code"
				prop="code"
			>
				<el-input
					v-model="formModel.code"
					placeholder="Select code"
					:disabled="true"
				/>
			</el-form-item>

			<template v-if="formModel.type === 'COMPLETE_SR_QUESTIONNAIRE'">
				<el-form-item
					label="Questionnaire Type"
					prop="questionnaireType"
				>
					<el-input
						v-model="formModel.questionnaireType"
						:disabled="true"
					/>
				</el-form-item>
				<el-form-item
					label="Questionnaire Format"
					prop="questionnaireFormat"
				>
					<el-radio-group v-model="formModel.questionnaireFormat">
						<el-radio label="FHIR_QUESTIONNAIRE">
							FHIR Questionnaire
						</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item
					label="FHIR Questionnaire"
					prop="questionnaireId"
				>
					<el-select
						v-model="formModel.questionnaireId"
						placeholder="Select questionnaire"
					>
						<el-option
							v-for="item in questionnaireOptions"
							:key="item.value"
							:label="item.label"
							:value="item.value"
						/>
					</el-select>
				</el-form-item>
			</template>

			<el-divider />

			<el-form-item
				label="Status"
				prop="status"
			>
				<el-select
					v-model="formModel.status"
					placeholder="Select status"
					class="half"
					:disabled="true"
				>
					<el-option
						v-for="item in statusOptions"
						:key="item.value"
						:label="item.label"
						:value="item.value"
					/>
				</el-select>
			</el-form-item>
			<el-form-item
				label="Priority"
				prop="priority"
			>
				<el-radio-group v-model="formModel.priority">
					<el-radio label="Routine" />
					<el-radio label="Urgent" />
					<el-radio label="ASAP" />
				</el-radio-group>
			</el-form-item>
			<el-form-item
				label="Occurrence"
				prop="occurrence"
			>
				<el-date-picker
					v-model="formModel.occurrence"
					type="daterange"
					range-separator="To"
					start-placeholder="Select date"
					end-placeholder="Select date"
				/>
			</el-form-item>
			<el-form-item
				label="Comment"
				prop="comment"
			>
				<el-input
					v-model="formModel.comment"
					type="textarea"
					rows="2"
					placeholder="Enter your comment here..."
				/>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button
				round
				size="mini"
				@click="onDialogClose"
			>
				Cancel
			</el-button>
			<el-button
				plain
				round
				type="primary"
				size="mini"
				:loading="saveInProgress"
				:disabled="!formHasChanges"
				@click="onFormSave"
			>
				Create
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.new-patient-task-form {
	.el-select {
		width: 100%;

		&.half {
			width: 50%;
			margin-right: 15px;
		}
	}

	.el-divider {
		margin: 20px 0;
	}
}

</style>
