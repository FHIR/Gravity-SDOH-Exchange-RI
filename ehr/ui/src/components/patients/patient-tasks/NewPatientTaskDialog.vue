<script lang="ts">
import { defineComponent, ref, computed } from "vue";
import { RuleItem } from "async-validator";

type FormModel = {
	name: string,
	type: string,
	code: string,
	status: string,
	priority: string,
	comment: string
};

const DEFAULT_REQUIRED_RULE = {
	required: true,
	message: "This field is required"
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
		const typeOptions = ref<{ label: string, value: string }[]>([{
			label: "Complete questionnaire regarding social risks",
			value: "assessment"
		}, {
			label: "Provide feedback on service delivered",
			value: "referral"
		}]);
		const codeOptions = ref<[]>([]);
		const statusOptions = ref<{ label: string, value: string }[]>([{
			label: "Ready",
			value: "ready"
		}]);
		const formModel = ref<FormModel>({
			name: "",
			type: "",
			code: "",
			status: "Ready",
			priority: "Routine",
			comment: ""
		});
		const formEl = ref<HTMLFormElement>();
		const formRules: { [field: string]: RuleItem & { trigger?: string } } = {
			name: DEFAULT_REQUIRED_RULE,
			type: DEFAULT_REQUIRED_RULE,
			code: DEFAULT_REQUIRED_RULE,
			priority: DEFAULT_REQUIRED_RULE
		};
		const formHasChanges = computed<boolean>(() =>
			(
				formModel.value.name !== "" ||
				formModel.value.type !== "" ||
				formModel.value.code !== "" ||
				formModel.value.status !== "Ready" ||
				formModel.value.priority !== "Routine" ||
				formModel.value.comment !== ""
			)
		);

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
			formHasChanges
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
			label-width="155px"
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
				<el-select
					v-model="formModel.code"
					placeholder="Select code"
					:disabled="true"
				/>
			</el-form-item>

			<el-divider />

			<el-form-item
				label="Status"
				prop="status"
			>
				<el-select
					v-model="formModel.status"
					placeholder="Select status"
					class="half"
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
				</el-radio-group>
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
