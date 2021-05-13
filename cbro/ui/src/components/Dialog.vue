<script lang="ts">
import { defineComponent, PropType, ref, computed, watch } from "vue";
import { Task, TaskPriority, TaskStatus, Occurrence, UpdatedStatus, UpdateTaskPayload, Procedure } from "@/types";
import TaskStatusSelect from "@/components/TaskStatusSelect.vue";
import { updateTask, getProceduresForCategory } from "@/api";


type TaskStuff = {
	id: string,
	requestName: string,
	requestor: string,
	request: string,
	forPatient: string,
	priority: string,
	occurrence: string,
	ehrComment: string,

	status: TaskStatus,
	statusDate: string,
	comment: string,
	performer: string,
	cboPriority: TaskPriority,
	outcome: string,
	procedures: string,
	previouslySetProcedures: string[]
}

const initTaskStuff: TaskStuff = {
	id: "",
	requestName: "",
	requestor: "",
	request: "",
	forPatient: "",
	priority: "",
	occurrence: "",
	ehrComment: "",
	status: "Received",
	statusDate: "",
	comment: "",
	performer: "",
	cboPriority: "ROUTINE",
	outcome: "",
	procedures: "",
	previouslySetProcedures: []
};

const Flow: { [status in TaskStatus]?: UpdatedStatus[] } = {
	"Received": ["Accepted", "Rejected"],
	"Accepted": ["In Progress", "On Hold"],
	"In Progress": ["On Hold", "Completed"],
	"On Hold": ["In Progress"]
};

const showOccurrence = (ocr: Occurrence) => ocr.start ? `From ${ocr.start} to ${ocr.end}` : `Until ${ocr.end}`;

const prepareTaskStuff = (task: Task): TaskStuff => ({
	id: task.id,
	requestName: task.name,
	requestor: task.requester.display,
	request: `${task.serviceRequest.code.display} (${task.serviceRequest.code.code})`,
	forPatient: task.patient.display,
	priority: task.priority,
	occurrence: showOccurrence(task.serviceRequest.occurrence),
	ehrComment: task.comments[0]?.text || "",

	status: task.status,
	statusDate: task.lastModified,
	comment: "",
	performer: "",
	cboPriority: "ROUTINE",
	outcome: task.outcome || "",
	procedures: "",
	previouslySetProcedures: task.procedures.map(proc => proc.display)
});


export default defineComponent({
	emits: ["close", "task-updated"],
	components: { TaskStatusSelect },
	props: {
		task: {
			type: Object as PropType<Task | null>,
			default: null
		}
	},
	setup(props, ctx) {
		const opened = computed(() => props.task !== null);

		const taskFields = ref<TaskStuff>(initTaskStuff);

		const status = ref<TaskStatus>("Received");
		const comment = ref<string>("");
		const performer = ref<string>("");
		const cboPriority = ref<TaskPriority>("ROUTINE");
		const outcome = ref<string>("");
		const procedures = ref<string[]>([]);

		const acceptedStatuses = computed(() => [taskFields.value.status].concat(Flow[taskFields.value.status] || []));

		const showOuctome = computed(() => status.value === "Completed" || status.value === "Rejected");
		const showProcedures = computed(() => status.value === "Completed");
		const isFinalized = computed(() => taskFields.value.status === "Completed");

		const hasChanges = computed(() =>
			status.value !== taskFields.value.status ||
			comment.value !== taskFields.value.comment ||
			outcome.value !== taskFields.value.outcome ||
			procedures.value.length > 0
		);

		const canSave = computed(() => status.value === "Completed" ? (procedures.value.length > 0 && outcome.value.length > 0) : hasChanges.value);

		const availableProcedures = ref<Procedure[]>([]);
		const loadProcedures = async (categoryCode: string) => {
			availableProcedures.value = await getProceduresForCategory(categoryCode);
		};

		const init = (task: Task) => {
			console.log("init");
			loadProcedures(task.serviceRequest.category.code);
			const fields = prepareTaskStuff(task);
			taskFields.value = fields;
			status.value = fields.status;
			comment.value = fields.comment;
			performer.value = fields.performer;
			cboPriority.value = fields.cboPriority;
			outcome.value = fields.outcome;
			procedures.value = [];
		};

		watch(() => props.task, (newTask, prevTask) => {
			if (newTask && newTask.id !== prevTask?.id) {
				init(newTask);
			}
		}, { immediate: true });

		const beforeClose = () => {
			ctx.emit("close");
		};

		const saveInProgress = ref(false);

		const save = async () => {
			const payload: UpdateTaskPayload = {
				status: status.value as UpdatedStatus,
				comment: comment.value || undefined,
				outcome: outcome.value || undefined,
				procedureCodes: procedures.value
			};
			saveInProgress.value = true;
			try {
				const resp = await updateTask(taskFields.value.id, payload);
				ctx.emit("task-updated", resp);
				init(resp);
			} finally {
				saveInProgress.value = false;
			}
		};

		return {
			opened,
			taskFields,
			status,
			comment,
			performer,
			cboPriority,
			outcome,
			procedures,
			acceptedStatuses,
			availableProcedures,
			showOuctome,
			showProcedures,
			isFinalized,
			canSave,
			beforeClose,
			saveInProgress,
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
			<div class="dialog-body">
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
							label="Comment"
						>
							<span>
								{{ taskFields.ehrComment }}
							</span>
						</el-form-item>
					</el-form>
				</div>

				<div class="editable-part">
					<el-form
						label-position="left"
					>
						<el-form-item
							label="Status"
						>
							<TaskStatusSelect
								v-model="status"
								:options="acceptedStatuses"
								class="status-select"
							/>
							<span class="status-date">
								{{ taskFields.statusDate }}
							</span>
						</el-form-item>

						<el-form-item
							v-if="!isFinalized"
							label="Comment"
						>
							<el-input
								v-model="comment"
								type="textarea"
								placeholder="Enter your comment here"
							/>
						</el-form-item>

						<!-- <el-form-item
							label="Performer"
						>
							<el-select
								v-model="performer"
								size="mini"
								placeholder="Select CBO"
								:popper-append-to-body="false"
							/>
						</el-form-item>

						<el-form-item
							label="Priority for CBO"
						>
							<el-radio-group v-model="cboPriority">
								<el-radio label="ROUTINE">Routine</el-radio>
								<el-radio label="URGENT">Urgent</el-radio>
								<el-radio label="ASAP">ASAP</el-radio>
							</el-radio-group>
						</el-form-item> -->

						<el-form-item
							v-if="showOuctome"
							label="Outcomes"
						>
							<el-input
								v-model="outcome"
								type="textarea"
								:disabled="isFinalized"
								placeholder="Placeholder placeholder"
							/>
						</el-form-item>

						<el-form-item
							v-if="showProcedures && !isFinalized"
							label="Procedures"
						>
							<el-select
								v-model="procedures"
								multiple
								size="mini"
								placeholder="Select procedure code"
								:popper-append-to-body="false"
								class="procedures-select"
							>
								<el-option
									v-for="proc in availableProcedures"
									:key="proc.code"
									:value="proc.code"
									:label="`${proc.display} (${proc.code})`"
								/>
							</el-select>
						</el-form-item>

						<el-form-item
							v-if="isFinalized"
							label="Procedures"
						>
							<el-select
								v-model="taskFields.previouslySetProcedures"
								multiple
								size="mini"
								:disabled="true"
								placeholder="Select procedure code"
								:popper-append-to-body="false"
								class="procedures-select"
							/>
						</el-form-item>
					</el-form>
				</div>
			</div>

			<div class="dialog-footer">
				<el-button
					round
					@click="beforeClose"
				>
					Cancel
				</el-button>
				<el-button
					round
					:disabled="!canSave"
					:loading="saveInProgress"
					@click="save"
				>
					Save Changes
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
		}

		.editable-part {
			padding-top: 20px;

			.status-select {
				width: 200px;
			}

			.status-date {
				margin-left: 10px;
			}

			.procedures-select {
				::v-deep(.el-select-dropdown) {
					width: 485px;
				}
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

	::v-deep(.el-radio) {
		.el-radio__label {
			color: $global-text-color;
			font-weight: 400;
		}

		&.is-checked .el-radio__inner {
			background-color: $global-primary-color;
			border-color: $global-primary-color;
		}
	}

	::v-deep(.el-textarea),
	::v-deep(.el-input) {
		input, textarea {
			font-size: $global-font-size;
			font-weight: 400;
			color: $global-text-color;

			&::placeholder {
				font-size: $global-font-size;
				font-weight: 400;
				color: $grey;
			}
		}
	}

	::v-deep(.el-select) {
		width: 100%;

		.el-select-dropdown__item {
			padding: 0 10px;
			font-size: $global-small-font-size;
			height: 25px;
			line-height: 25px;
		}
	}
}
</style>
