<script lang="ts">
import { defineComponent, PropType, ref, reactive, computed, watch } from "vue";
import { TableData } from "@/components/patients/action-steps/ActionSteps.vue";
import { Occurrence, TaskStatus, updateTaskPayload } from "@/types";
import moment from "moment";
import { TasksModule } from "@/store/modules/tasks";
import TaskStatusIcon from "@/components/patients/TaskStatusIcon.vue";

export type FormModel = {
	status: string,
	comment: string,
	statusReason: string
};


export default defineComponent({
	name: "EditRequestDialog",
	components: {
		TaskStatusIcon
	},
	props: {
		visible: {
			type: Boolean,
			default: false
		},
		task: {
			type: Object as PropType<TableData>,
			default: undefined
		}
	},
	emits: ["close"],
	setup(props, { emit }) {
		const saveInProgress = ref<boolean>(false);
		const formModel = reactive<FormModel>({
			status: "",
			comment: "",
			statusReason: ""
		});
		//todo: use element-ui form type
		const formEl = ref<HTMLFormElement>();

		const onDialogOpen = () => {
			Object.assign(formModel, { status: props.task.status });
		};
		const onDialogClose = () => {
			formEl.value?.resetFields();
			emit("close");
		};

		const showOccurrence = (occurrence: Occurrence) => {
			if (occurrence.start !== null) {
				return `From ${moment(occurrence.start).format("MMM DD, YYYY")} to ${moment(occurrence.end).format("MMM DD, YYYY")}`;
			}
			return `Until ${moment(occurrence.end).format("MMM DD, YYYY")}`;
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

		const showStatusReasonInput = computed(() => formModel.status === "Cancelled" && props.task?.status !== "Cancelled");

		const init = (task: TableData) => {
			formModel.status = "";
			formModel.comment = "";
			formModel.statusReason= "";
		};

		watch(() => props.task, (newTask, prevTask) => {
			if (newTask && newTask.id !== prevTask?.id) {
				init(newTask);
			}
		}, { immediate: true });

		const onFormSave = async () => {
			const payload: updateTaskPayload = {
				id: props.task.id,
				comment: formModel.comment,
				status: formModel.status === props.task.status ? null : formModel.status as TaskStatus,
				statusReason: showStatusReasonInput.value ? formModel.statusReason : undefined
			};
			saveInProgress.value = true;
			try {
				await TasksModule.updateTask(payload);
				emit("close");
			} finally {
				saveInProgress.value = false;
			}
		};

		const statusChanged = computed<boolean>(() =>
			formModel.status !== props.task?.status
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
			formModel,
			onDialogOpen,
			onDialogClose,
			showOccurrence,
			getStatusOptions,
			onFormSave,
			hasChanges,
			formEl,
			showStatusReasonInput,
			formRules
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="Service Request/Task"
		:width="700"
		append-to-body
		destroy-on-close
		custom-class="edit-request-dialog"
		@close="onDialogClose"
		@opened="onDialogOpen"
	>
		<el-form
			ref="formEl"
			:model="formModel"
			:rules="formRules"
			label-width="155px"
			label-position="left"
			size="mini"
			class="edit-request-form"
		>
			<el-form-item label="Request Name">
				{{ task.name }}
			</el-form-item>
			<el-form-item label="Category/Domain">
				{{ task.category.display }}
			</el-form-item>
			<el-form-item label="Request">
				{{ `${task.request.display} (${task.request.code})` }}
			</el-form-item>
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

			<el-divider />

			<el-form-item label="Priority">
				{{ task.priority }}
			</el-form-item>
			<el-form-item label="Occurrence">
				{{ showOccurrence(task.occurrence) }}
			</el-form-item>
			<el-form-item label="Problem(s)">
				<div
					v-for="(item, index) in task.problems"
					:key="index"
					class="wrapper"
				>
					<span class="item">{{ item.display }}</span>
				</div>
			</el-form-item>
			<el-form-item
				label="Goal(s)"
			>
				<div
					v-for="(item, index) in task.goals"
					:key="index"
					class="wrapper"
				>
					<span class="item">{{ item.display }}</span>
				</div>
			</el-form-item>

			<el-divider />

			<el-form-item label="Performer(s)">
				{{ task.performer }}
			</el-form-item>
			<el-form-item label="Consent">
				{{ task.consent }}
			</el-form-item>

			<div
				v-if="task.outcomes || task.statusReason || task.procedures.length > 0 || task.comments.length > 0"
				class="outcome-section"
			>
				<el-divider />

				<el-form-item
					v-if="task.outcomes || task.statusReason"
					label="Outcome"
				>
					{{ task.statusReason ? task.statusReason : task.outcomes }}
				</el-form-item>
				<el-form-item
					v-if="task.procedures.length > 0"
					label="Procedure(s)"
				>
					<div
						v-for="(item, index) in task.procedures"
						:key="index"
						class="wrapper"
					>
						<span class="item">{{ item.display }}</span>
					</div>
				</el-form-item>
				<el-form-item
					v-if="task.comments.length > 0"
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
			</div>
		</el-form>
		<template #footer>
			<el-button
				round
				size="mini"
				@click="$emit('close')"
			>
				Cancel
			</el-button>
			<el-button
				plain
				round
				type="primary"
				size="mini"
				:loading="saveInProgress"
				:disabled="!hasChanges"
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

.wrapper {
	line-height: 15px;
	margin-top: 5px;
	margin-bottom: 10px;

	&:last-child {
		margin-bottom: 0;
	}
}

.item {
	background-color: $alice-blue;
	border-radius: 5px;
	padding: 0 5px;
	font-size: $global-small-font-size;

	@include dont-break-out();
}

.date {
	margin-left: 10px;
}
</style>
