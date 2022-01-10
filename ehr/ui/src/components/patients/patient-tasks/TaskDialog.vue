<script lang="ts">
import { computed, defineComponent, reactive, ref } from "vue";
import TaskStatusIcon from "@/components/patients/TaskStatusIcon.vue";
import { TaskStatus, PatientTask } from "@/types";
import { PatientTasksModule } from "@/store/modules/patientTasks";

export type FormModel = {
	status: string,
	comment: string,
	statusReason: string
};

export default defineComponent({
	components: { TaskStatusIcon },
	props: {
		visible: {
			type: Boolean,
			default: false
		},
		taskId: {
			type: String,
			default: undefined
		},
		taskName: {
			type: String,
			default: undefined
		}
	},
	emits: ["close", "trigger-open-assessment", "trigger-open-action-step"],
	setup(props, { emit }) {
		const saveInProgress = ref<boolean>(false);
		const isLoading = ref<boolean>(false);
		const formModel = reactive<FormModel>({
			status: "",
			comment: "",
			statusReason: ""
		});
		const task = ref<PatientTask | null>(null);

		//todo: use element-ui form type
		const formEl = ref<HTMLFormElement>();

		const onDialogOpen = () => {
			isLoading.value = true;
			PatientTasksModule.getPatientTask(props.taskId).then(data => {
				task.value = data;
				initFormModel(data);
			}).finally(() => isLoading.value = false);
		};

		const onDialogClose = () => {
			formEl.value?.resetFields();
			emit("close");
		};

		const getStatusOptions = (status: TaskStatus): { name: string, value: string }[] => {
			if (status === "Completed" || status === "Cancelled") {
				return [];
			}

			return [{
				name: "Cancelled",
				value: "Cancelled"
			}];
		};

		const showStatusReasonInput = computed(() => formModel.status === "Cancelled" && task.value?.status !== "Cancelled");

		const initFormModel = (task: PatientTask) => {
			formModel.status = task.status ? task.status : "";
			formModel.comment = "";
			formModel.statusReason = "";
		};

		const onFormSave = async () => {
			const payload = {
				id: task.value!.id,
				comment: formModel.comment,
				status: formModel.status === task.value!.status ? null : formModel.status as TaskStatus,
				statusReason: showStatusReasonInput.value ? formModel.statusReason : undefined
			};
			saveInProgress.value = true;
			try {
				await PatientTasksModule.updatePatientTask(payload);
				emit("close");
			} finally {
				saveInProgress.value = false;
			}
		};

		const statusChanged = computed<boolean>(() =>
			formModel.status !== task.value?.status
		);

		const isValid = computed(() =>
			(!showStatusReasonInput.value || formModel.statusReason)
		);

		const hasChanges = computed(() => (statusChanged.value || formModel.comment !== "") && isValid.value);


		const formRules = computed(() => ({
			statusReason: [{ required: true, message: "This field is required" }]
		}));

		return {
			saveInProgress,
			onDialogClose,
			formModel,
			formEl,
			hasChanges,
			onFormSave,
			getStatusOptions,
			formRules,
			onDialogOpen,
			showStatusReasonInput,
			isLoading,
			task
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		:title="task ? task.name : ''"
		:width="700"
		destroy-on-close
		custom-class="patient-task-dialog"
		@close="onDialogClose"
		@opened="onDialogOpen"
	>
		<div
			v-loading="isLoading"
			class="form-wrapper"
		>
			<el-form
				v-if="task"
				ref="formEl"
				:model="formModel"
				:rules="formRules"
				label-width="155px"
				label-position="left"
				size="mini"
				class="problem-form"
			>
				<el-form-item label="Task Name">
					{{ task.name }}
				</el-form-item>
				<el-form-item label="Type">
					{{ task.type === "COMPLETE_SR_QUESTIONNAIRE" ? "Complete questionnaire regarding social risks" : "Provide feedback on service delivered" }}
				</el-form-item>
				<el-form-item
					v-if="task.referralTask"
					label="Referral Task"
				>
					{{ task.referralTask.display }}
					<span
						v-if="task.referralTask && task.referralTask?.display"
						class="icon-link"
						@click="$emit('trigger-open-action-step', task.referralTask.id)"
					>
					</span>
				</el-form-item>
				<el-form-item label="Code">
					{{ task.code ? `${task.code.display}` : "N/A" }}
				</el-form-item>
				<el-form-item
					v-if="task.type === 'COMPLETE_SR_QUESTIONNAIRE'"
					label="Questionnaire Type"
				>
					{{ task.type === "COMPLETE_SR_QUESTIONNAIRE" ? "Risk Questionnaire" : "N/A" }}
				</el-form-item>
				<el-form-item
					v-if="task.type === 'COMPLETE_SR_QUESTIONNAIRE'"
					label="Questionnaire Format"
				>
					{{ task.type === "COMPLETE_SR_QUESTIONNAIRE" ? "FHIR Questionnaire" : "N/A" }}
				</el-form-item>
				<el-form-item
					v-if="task.type === 'COMPLETE_SR_QUESTIONNAIRE'"
					label="FHIR Questionnaire"
				>
					{{ task.assessment ? task.assessment.display : "N/A" }}
					<span
						v-if="task.assessment && task.assessment.id"
						class="icon-link"
						@click="$emit('trigger-open-assessment', task.assessment.id)"
					>
					</span>
				</el-form-item>
				<el-divider />
				<el-form-item
					label="Status"
					prop="status"
				>
					<el-select
						v-model="formModel.status"
						placeholder="Select Status"
					>
						<template #prefix>
							<TaskStatusIcon
								:status="formModel.status"
								:small="true"
							/>
						</template>

						<el-option
							v-for="item in getStatusOptions(task.status)"
							:key="item.value"
							:label="item.name"
							:value="item.value"
						>
							<TaskStatusIcon
								:status="item.value"
								:small="true"
							/>
							{{ item.name }}
						</el-option>
					</el-select>
					<span class="date">{{ $filters.formatDateTime(task.lastModified) }}</span>
				</el-form-item>
				<el-form-item label="Priority">
					{{ task.priority }}
				</el-form-item>
				<el-form-item
					v-if="showStatusReasonInput"
					label="Reason"
					prop="statusReason"
				>
					<el-input
						v-model="formModel.statusReason"
						type="textarea"
						placeholder="Enter reason here"
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
				<el-divider v-if="task.statusReason" />
				<el-form-item
					v-if="task.statusReason"
					label="Reason"
				>
					{{ task.statusReason }}
				</el-form-item>
				<el-form-item
					v-if="task.outcome"
					label="Outcomes"
				>
					{{ task.outcomes }}
				</el-form-item>
				<el-form-item
					v-if="task.comments && task.comments.length > 0"
					label="Comment(s)"
				>
					<div
						v-for="(item, index) in task.comments"
						:key="index"
						class="wrapper"
					>
						{{ item.text }}
					</div>
				</el-form-item>
			</el-form>

			<el-divider v-if=" task && task.answers" />

			<el-form
				v-if="task && task.answers"
				label-width="90px"
				label-position="left"
				size="mini"
			>
				<h4>
					Question-Answer Pairs
				</h4>

				<div
					v-for="(value, key, index) in task.answers"
					:key="index"
					class="question"
				>
					<el-form-item
						:label="`Question ${index+1}`"
					>
						{{ key }}
					</el-form-item>
					<el-form-item
						label="Response"
					>
						{{ value }}
					</el-form-item>
				</div>
			</el-form>
		</div>
		<template #footer>
			<el-button
				plain
				round
				type="primary"
				size="mini"
				@click="$emit('close')"
			>
				Close
			</el-button>
			<el-button
				:loading="saveInProgress"
				:disabled="!hasChanges"
				plain
				round
				type="primary"
				size="mini"
				@click="onFormSave"
			>
				Save Changes
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.edit-request-form {
	.el-divider {
		margin: 20px 0;
	}
}

.form-wrapper {
	min-height: 300px;
}

.wrapper {
	line-height: 15px;
	margin-top: 5px;
	margin-bottom: 10px;

	&:last-child {
		margin-bottom: 0;
	}
}

.icon-link {
	position: relative;
	left: 7px;
	cursor: pointer;

	@include icon("~@/assets/images/link.svg", 14px, 14px);
}

.status-cell {
	display: flex;
	align-items: center;

	.status {
		margin-right: 10px;
	}
}

.date {
	margin-left: 10px;
}
</style>
