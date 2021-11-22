<script lang="ts">
import { defineComponent, PropType, ref, computed, watch, reactive, toRefs } from "vue";
import { Task, TaskStatus, UpdatedStatus, UpdateTaskPayload, Procedure, Cbo } from "@/types";
import TaskStatusSelect from "@/components/TaskStatusSelect.vue";
import TaskStatusDisplay from "@/components/TaskStatusDisplay.vue";
import { getProceduresForCategory, getCBOList } from "@/api";
import { showDateTime, showOccurrence } from "@/utils";
import useServiceRequests from "@/state/useServiceRequests";


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
	performingCBO: string
}

const initialTaskStuff: TaskStuff = {
	id: "",
	requestName: "",
	requestor: "",
	request: "",
	forPatient: "",
	priority: "",
	occurrence: "",
	previousComments: [],
	status: "Received",
	statusDate: "",
	outcome: "",
	statusReason: "",
	previouslySetProcedures: [],
	performingCBO: ""
};

const Flow: { [status in TaskStatus]?: TaskStatus[] } = {
	"Received":    ["Accepted", "Rejected"],
	"Accepted":    ["Cancelled"],
	"In Progress": ["Cancelled"],
	"On Hold":     ["Cancelled"]
};

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
	performingCBO: task.performer?.display || ""
});


export default defineComponent({
	components: { TaskStatusSelect, TaskStatusDisplay },
	props: {
		taskId: {
			type: String as PropType<string | null>,
			default: null
		}
	},
	emits: ["close"],
	setup(props, ctx) {
		const { find, update } = useServiceRequests();
		const task = computed(() => props.taskId ? find(props.taskId).value : null);

		const opened = computed(() => task.value !== null);

		const taskFields = ref<TaskStuff>(initialTaskStuff);

		const status = ref<TaskStatus>("Received");
		const comment = ref<string>("");
		const formStuff = reactive({
			outcome: "",
			statusReason: "",
			procedures: [] as string[],
			cboPerformer: "",
			priorityForCBO: ""
		});
		const { outcome, statusReason, procedures, cboPerformer, priorityForCBO } = toRefs(formStuff);

		const acceptedStatuses = computed(() => [taskFields.value.status].concat(Flow[taskFields.value.status] || []));

		const isFinalized = computed(() => ["Rejected", "Cancelled", "Completed"].includes(taskFields.value.status));

		const showOutcomeInput = computed(() => status.value === "Completed");
		const showStatusReasonInput = computed(() => status.value === "Rejected" || status.value === "Cancelled");
		const showProceduresSelect = computed(() => status.value === "Completed" || status.value === "Cancelled");

		const proceduresRequired = computed(() => status.value === "Completed");

		const statusChanged = computed(() =>
			status.value !== taskFields.value.status
		);

		const isValid = computed(() =>
			(!showOutcomeInput.value || outcome.value) &&
			(!showStatusReasonInput.value || statusReason.value) &&
			(!showFieldsForAcceptedStatus.value || priorityForCBO.value) &&
			(!showFieldsForAcceptedStatus.value || cboPerformer.value) &&
			(!proceduresRequired.value || procedures.value.length > 0)
		);

		const canSave = computed(() => statusChanged.value && isValid.value);

		const availableProcedures = ref<Procedure[]>([]);
		const loadProcedures = async (categoryCode: string) => {
			availableProcedures.value = await getProceduresForCategory(categoryCode);
		};

		const availableCBOList = ref<Cbo[]>([]);
		const loadCBO = async () => {
			availableCBOList.value = await getCBOList();
		};

		const init = (task: Task) => {
			loadProcedures(task.serviceRequest.category.code);
			loadCBO();
			const fields = prepareTaskStuff(task);
			taskFields.value = fields;
			status.value = fields.status;
			comment.value = "";
			outcome.value = "";
			statusReason.value = "";
			procedures.value = [];
			cboPerformer.value = "";
			priorityForCBO.value = "";
		};

		watch(task, (newTask, prevTask) => {
			if (newTask && newTask.id !== prevTask?.id) {
				init(newTask);
			}
		}, { immediate: true });

		const showFieldsForAcceptedStatus = ref<boolean>(false);
		watch(() => status.value, newStatus => {
			showFieldsForAcceptedStatus.value = newStatus === "Accepted" && task.value?.status === "Received";
		}, { immediate: true });

		const beforeClose = () => {
			ctx.emit("close");
		};

		const saveInProgress = ref(false);

		const save = async () => {
			const payload: UpdateTaskPayload = {
				status: status.value as UpdatedStatus,
				comment: comment.value || undefined,
				outcome: showOutcomeInput.value ? outcome.value : undefined,
				statusReason: showStatusReasonInput.value ? statusReason.value : undefined,
				procedureCodes: procedures.value.length > 0 ? procedures.value : undefined,
				cboPerformer: cboPerformer.value || undefined,
				priorityForCBO: priorityForCBO.value || undefined
			};
			saveInProgress.value = true;
			try {
				await update(taskFields.value.id, payload);
				ctx.emit("close");
			} finally {
				saveInProgress.value = false;
				showFieldsForAcceptedStatus.value = false;
			}
		};

		const formRef = ref<any>(null);
		const formRules = computed(() => ({
			statusReason: [{ required: true, message: "This field is required" }],
			outcome: [{ required: true, message: "This field is required" }],
			cboPerformer: [{ required: true, message: "This field is required" }],
			priority: {
				required: true,
				trigger: "change",
				message: ""
			},
			procedures: [
				{
					required: proceduresRequired.value,
					message: "This field is required",
					trigger: "change"
				}
			]
		}));

		watch(status, () => {
			formRef.value?.clearValidate();
		});

		return {
			formRef,
			formRules,
			opened,
			taskFields,
			status,
			comment,
			outcome,
			statusReason,
			procedures,
			cboPerformer,
			priorityForCBO,
			showFieldsForAcceptedStatus,
			formStuff,
			acceptedStatuses,
			availableProcedures,
			availableCBOList,
			showOutcomeInput,
			showStatusReasonInput,
			showProceduresSelect,
			statusChanged,
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
			title="Service Request"
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

						<el-form-item
							v-if="taskFields.performingCBO"
							label="Perorming CBO"
						>
							<span>
								{{ taskFields.performingCBO }}
							</span>
						</el-form-item>
					</el-form>
				</div>

				<div class="editable-part">
					<el-form
						ref="formRef"
						:rules="formRules"
						:model="formStuff"
						label-position="left"
					>
						<template v-if="isFinalized">
							<el-form-item
								label="Status"
							>
								<TaskStatusDisplay
									:status="status"
									small
								/>
								<span class="status-date">
									{{ taskFields.statusDate }}
								</span>
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
						</template>

						<template v-if="!isFinalized">
							<el-form-item
								label="Status"
							>
								<TaskStatusSelect
									v-model="status"
									:options="acceptedStatuses"
									class="status-select"
								/>
								<span
									v-if="!statusChanged"
									class="status-date"
								>
									{{ taskFields.statusDate }}
								</span>
							</el-form-item>

							<el-form-item
								v-if="showOutcomeInput"
								label="Outcome"
								prop="outcome"
							>
								<el-input
									v-model="formStuff.outcome"
									type="textarea"
									placeholder="Enter outcome here"
								/>
							</el-form-item>

							<el-form-item
								v-if="showStatusReasonInput"
								label="Reason"
								prop="statusReason"
							>
								<el-input
									v-model="formStuff.statusReason"
									type="textarea"
									placeholder="Enter reason here"
								/>
							</el-form-item>

							<el-form-item
								v-if="showProceduresSelect"
								label="Procedures"
								prop="procedures"
							>
								<el-select
									v-model="formStuff.procedures"
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
								label="Comment"
							>
								<el-input
									v-model="comment"
									type="textarea"
									placeholder="Enter your comment here"
								/>
							</el-form-item>
							<el-form-item
								v-if="showFieldsForAcceptedStatus"
								label="Performing CBO"
								prop="cboPerformer"
							>
								<el-select
									v-model="formStuff.cboPerformer"
									placeholder="Select CBO"
								>
									<el-option
										v-for="item in availableCBOList"
										:key="item.id"
										:label="item.name"
										:value="item.id"
									/>
								</el-select>
							</el-form-item>
							<el-form-item
								v-if="showFieldsForAcceptedStatus"
								label="Priority for CBO"
								prop="priority"
							>
								<el-radio-group v-model="formStuff.priorityForCBO">
									<el-radio label="Routine" />
									<el-radio label="Urgent" />
									<el-radio label="ASAP" />
								</el-radio-group>
							</el-form-item>
						</template>
					</el-form>
				</div>
			</div>

			<div class="dialog-footer">
				<el-button
					round
					@click="beforeClose"
				>
					{{ isFinalized ? "Close" : "Cancel" }}
				</el-button>
				<el-button
					v-if="!isFinalized"
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

	::v-deep(.el-select) {
		line-height: 25px;

		.el-input__inner {
			font-size: $global-font-size;
			font-weight: 400;
			color: #333;
			height: 25px;
		}

		.el-select-dropdown__item .selected {
			padding: 0 10px;
			font-size: $global-small-font-size;
			font-weight: 400;
			color: #333;
		}

		.el-input {
			line-height: 25px;
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

		.el-tag {
			background-color: $alice-blue;
			color: $global-text-color;
			font-size: $global-font-size;
			font-weight: 400;

			.el-tag__close {
				background: none;
				color: $global-text-color;
				font-weight: 1000;

				&:hover {
					background: none;
				}
			}
		}
	}
}
</style>
