<script lang="ts">
import { defineComponent, PropType, computed, ref, watch } from "vue";
import { Task, TaskStatus } from "@/types";
import TaskStatusDisplay from "@/components/TaskStatusDisplay.vue";
import TaskStatusSelect from "@/components/TaskStatusSelect.vue";
import { showDateTime, showOccurrence } from "@/utils";
import useOurTasks from "@/state/useOurTasks";


type TaskStuff = {
	id: string,
	requestName: string,
	requestor: string,
	request: string,
	forPatient: string,
	priority: string,
	occurrence: string,
	previousComments: string[],
	status: TaskStatus,
	statusDate: string,
	outcome: string,
	statusReason: string,
	previouslySetProcedures: string[],
	performer: string
}

const prepareTaskStuff = (task: Task): TaskStuff => ({
	id: task.id,
	requestName: task.name,
	requestor: task.requester.display,
	request: `${task.serviceRequest.code.display} (${task.serviceRequest.code.code})`,
	forPatient: task.patient.display,
	priority: task.priority,
	occurrence: showOccurrence(task.serviceRequest.occurrence),
	previousComments: task.comments.map(({ text }) => text),
	status: task.status,
	statusDate: showDateTime(task.lastModified),
	outcome: task.outcome || "",
	statusReason: task.statusReason || "",
	previouslySetProcedures: task.procedures.map(proc => proc.display),
	performer: task.performer?.display || "",
});

const CancellableStatuses: Set<TaskStatus> = new Set(["Received", "Accepted", "In Progress", "On Hold"]);

export default defineComponent({
	components: { TaskStatusDisplay, TaskStatusSelect },
	props: {
		taskId: {
			type: String as PropType<string | null>,
			default: null
		}
	},
	emits: ["close"],
	setup(props, ctx) {
		const { find, cancelTask } = useOurTasks();
		const task = computed(() => props.taskId ? find(props.taskId).value : null);

		const opened = computed(() => task.value !== null);

		const taskFields = computed<TaskStuff | null>(() => task.value ? prepareTaskStuff(task.value) : null);

		const canCancel = computed(() => taskFields.value && CancellableStatuses.has(taskFields.value.status));

		const statusModel = ref<TaskStatus>(taskFields.value?.status || "Received");
		watch(opened, () => {
			statusModel.value = taskFields.value?.status || "Received";
			formModel.value.statusReason = "";
		});

		const statusChanged = computed(() => statusModel.value !== taskFields.value?.status);

		const formModel = ref({
			statusReason: ""
		});

		const canSave = computed(() => statusChanged.value && formModel.value.statusReason);

		const saveInProgress = ref(false);

		const save = async () => {
			try {
				saveInProgress.value = true;
				await cancelTask(props.taskId!, formModel.value.statusReason);
			} finally {
				saveInProgress.value = false;
			}
		};

		const beforeClose = () => {
			ctx.emit("close");
		};

		const formRules = {
			statusReason: [{ required: true, message: "This field is required" }]
		};

		return {
			opened,
			taskFields,
			beforeClose,
			canCancel,
			statusModel,
			statusChanged,
			canSave,
			saveInProgress,
			formModel,
			formRules,
			save
		};
	}
});
</script>

<template>
	<div class="dialog">
		<el-dialog
			title="Task"
			:model-value="opened"
			:width="700"
			:show-close="true"
			:before-close="beforeClose"
		>
			<div
				v-if="taskFields"
				class="dialog-body"
			>
				<div class="readonly-part">
					<el-form
						label-position="left"
					>
						<el-form-item
							label="Request Name"
						>
							<span>
								{{ taskFields.requestName }}
							</span>
						</el-form-item>

						<el-form-item
							label="Requestor"
						>
							<span>
								{{ taskFields.requestor }}
							</span>
						</el-form-item>

						<el-form-item
							label="Request"
						>
							<span>
								{{ taskFields.request }}
							</span>
						</el-form-item>

						<el-form-item
							label="For"
						>
							<span>
								{{ taskFields.forPatient }}
							</span>
						</el-form-item>

						<el-form-item
							label="Priority"
						>
							<span>
								{{ taskFields.priority }}
							</span>
						</el-form-item>

						<el-form-item
							label="Occurrence"
						>
							<span>
								{{ taskFields.occurrence }}
							</span>
						</el-form-item>

						<el-form-item
							label="Comments"
						>
							<div
								v-for="(comment, ix) in taskFields.previousComments"
								:key="ix"
								class="comment"
							>
								{{ comment }}
							</div>
						</el-form-item>
					</el-form>
				</div>

				<div class="editable-part">
					<el-form
						label-position="left"
						:model="formModel"
						:rules="formRules"
					>
						<el-form-item
							v-if="canCancel"
							label="Status"
						>
							<TaskStatusSelect
								v-model="statusModel"
								:options="[taskFields.status, 'Cancelled']"
							/>
							<span
								v-if="!statusChanged"
								class="status-date"
							>
								{{ taskFields.statusDate }}
							</span>
						</el-form-item>
						<el-form-item
							v-else
							label="Status"
						>
							<TaskStatusDisplay
								:status="taskFields.status"
								small
							/>
							<span class="status-date">
								{{ taskFields.statusDate }}
							</span>
						</el-form-item>

						<el-form-item
							v-if="statusChanged"
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
							v-if="taskFields.statusReason"
							label="Reason"
						>
							<span>
								{{ taskFields.statusReason }}
							</span>
						</el-form-item>

						<el-form-item
							v-if="taskFields.outcome"
							label="Outcome"
						>
							<span>
								{{ taskFields.outcome }}
							</span>
						</el-form-item>

						<el-form-item
							v-if="taskFields.previouslySetProcedures.length > 0"
							label="Procedures"
						>
							<el-tag
								v-for="(proc, ix) in taskFields.previouslySetProcedures"
								:key="ix"
								size="mini"
								class="procedure-tag"
							>
								{{ proc }}
							</el-tag>
						</el-form-item>

						<el-form-item
							v-if="taskFields.performer"
							label="Performing CBO"
						>
							<span>
								{{ taskFields.performer }}
							</span>
						</el-form-item>
					</el-form>
				</div>
			</div>

			<div class="dialog-footer">
				<el-button
					round
					@click="beforeClose"
				>
					Close
				</el-button>

				<el-button
					:disabled="!canSave"
					round
					:loading="saveInProgress"
					@click="save"
				>
					Save
				</el-button>
			</div>
		</el-dialog>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.dialog {
	::v-deep(.el-dialog) {
		.el-dialog__body {
			padding: 0;
		}

		.el-dialog__title {
			color: $global-text-color;
			font-size: $global-large-font-size;
			font-weight: 500;
		}

		.el-dialog__close {
			font-weight: 1000;
			color: $global-text-color;
		}
	}

	::v-deep(.el-form) {
		.el-form-item {
			margin: 0;
			display: flex;
			align-items: center;
		}

		.el-form-item + .el-form-item {
			margin-top: 20px;
		}

		.el-form-item__label {
			width: 165px;
			padding-right: 10px;
			line-height: 15px;
			color: $grey;
		}

		.el-form-item__content {
			flex: 1;
			line-height: 15px;
			color: $global-text-color;
		}
	}

	.dialog-body {
		border-top: $global-border;
		border-bottom: $global-border;
		padding: 25px;
		max-height: 700px;
		overflow-y: auto;

		.readonly-part {
			padding-bottom: 30px;
			border-bottom: $global-border;

			.comment + .comment {
				margin-top: 5px;
			}
		}

		.editable-part {
			padding-top: 20px;

			.status-date {
				margin-left: 10px;
			}

			.procedure-tag + .procedure-tag {
				margin-left: 5px;
			}
		}
	}

	.dialog-footer {
		padding: 25px;
		display: flex;
		justify-content: flex-end;

		.el-button {
			min-width: 155px;
			font-size: $global-font-size;
			font-weight: 400;
			color: $global-text-color;
			border: $global-border;
			padding: 0;
			height: 25px;
			min-height: 25px;

			&.is-disabled {
				color: $global-text-muted-color;
			}
		}

		.el-button + .el-button {
			margin-left: 20px;
		}
	}
}
</style>
