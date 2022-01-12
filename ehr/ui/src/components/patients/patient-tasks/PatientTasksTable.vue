<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { TableData } from "@/components/patients/patient-tasks/PatientTasks.vue";
import TaskStatusIcon from "@/components/patients/TaskStatusIcon.vue";
import TaskDialog from "@/components/patients/patient-tasks/TaskDialog.vue";

export default defineComponent({
	components: {
		TaskStatusIcon,
		TaskDialog
	},
	props: {
		data: {
			type: Array as PropType<TableData[]>,
			required: true
		},
		status: {
			type: String as PropType<"active" | "completed">,
			default: "active"
		}
	},
	emits: ["add-task", "trigger-open-assessment", "trigger-open-action-step"],
	setup(props, { emit }) {
		const title = ref<string>(props.status === "active" ? "Active Tasks" : "Completed Tasks");
		const taskDialogVisible = ref<boolean>(false);
		const activeTaskId = ref<string | null>(null);
		const activeTaskName = ref<string | null>(null);

		const onTaskClick = (row: TableData) => {
			activeTaskId.value = row.id;
			activeTaskName.value = row.name;
			taskDialogVisible.value = true;
		};

		const handleOpenAssessment = (id: string) => {
			taskDialogVisible.value = false;
			emit("trigger-open-assessment", id);
		};
		// Fix for table in Safari browser.
		const tableEl = ref<HTMLFormElement>();
		tableEl.value?.doLayout();

		const handleOpenActionStep = (id: string) => {
			taskDialogVisible.value = false;
			emit("trigger-open-action-step", id);
		};

		return {
			title,
			onTaskClick,
			taskDialogVisible,
			activeTaskId,
			handleOpenAssessment,
			activeTaskName,
			handleOpenActionStep,
			tableEl
		};
	}
});
</script>

<template>
	<div class="table-wrapper">
		<div class="title">
			<h3>
				{{ title }}
			</h3>
			<el-button
				v-if="status === 'active'"
				plain
				round
				type="primary"
				size="mini"
				@click="$emit('add-task')"
			>
				Add Patient Task
			</el-button>
		</div>
		<el-table
			ref="tableEl"
			:data="data"
		>
			<el-table-column label="Task Name">
				<template #default="scope">
					<el-button
						type="text"
						@click="onTaskClick(scope.row)"
					>
						{{ scope.row.name }}
					</el-button>
				</template>
			</el-table-column>

			<el-table-column
				prop="type"
				label="Type"
			>
				<template #default="scope">
					<el-popover
						effect="light"
						trigger="hover"
						placement="bottom"
					>
						<template #default>
							{{ scope.row.type }}
						</template>
						<template #reference>
							<div class="cell-text">
								{{ scope.row.type }}
							</div>
						</template>
					</el-popover>
				</template>
			</el-table-column>

			<el-table-column label="Status">
				<template #default="scope">
					<div class="status-cell">
						<TaskStatusIcon :status="scope.row.status" />
						<div class="info">
							<span class="status">{{ scope.row.status }}</span>
							<span class="date">{{ $filters.formatDateTime(scope.row.lastModified) }}</span>
						</div>
					</div>
				</template>
			</el-table-column>

			<el-table-column label="Code">
				<template #default="scope">
					<el-popover
						effect="light"
						trigger="hover"
						placement="bottom"
					>
						<template #default>
							{{ scope.row.code?.display || "N/A" }}
						</template>
						<template #reference>
							<div class="cell-text">
								{{ scope.row.code?.display || "N/A" }}
							</div>
						</template>
					</el-popover>
				</template>
			</el-table-column>

			<el-table-column label="Referral">
				<template #default="scope">
					<el-popover
						:disabled="!scope.row.referralTask"
						effect="light"
						trigger="hover"
						placement="bottom"
					>
						<template #default>
							{{ scope.row.referralTask?.display || "N/A" }}
						</template>
						<template #reference>
							<div class="cell-wrapper">
								<div class="cell-text">
									{{ scope.row.referralTask?.display || "N/A" }}
								</div>
								<span
									v-if="scope.row.referralTask && scope.row.referralTask?.display"
									class="icon-link"
									@click="$emit('trigger-open-action-step', scope.row.referralTask.id)"
								>
								</span>
							</div>
						</template>
					</el-popover>
				</template>
			</el-table-column>

			<el-table-column label="Document">
				<template #default="scope">
					<el-popover
						:disabled="!scope.row.assessment"
						effect="light"
						trigger="hover"
						placement="bottom"
					>
						<template #default>
							{{ scope.row.assessment?.display || "N/A" }}
						</template>
						<template #reference>
							<div class="cell-wrapper">
								<div class="cell-text">
									{{ scope.row.assessment?.display || "N/A" }}
								</div>
								<span
									v-if="scope.row.assessment && scope.row.assessment?.display"
									class="icon-link"
									@click="$emit('trigger-open-assessment', scope.row.assessment?.id)"
								>
								</span>
							</div>
						</template>
					</el-popover>
				</template>
			</el-table-column>

			<el-table-column label="Outcomes/Reason">
				<template #default="scope">
					<el-popover
						:disabled="!scope.row.statusReason && !scope.row.outcomes && !scope.row.assessmentResponse?.display"
						effect="light"
						trigger="hover"
						placement="bottom"
					>
						<template #default>
							{{ scope.row.statusReason || scope.row.outcomes || scope.row.assessmentResponse?.display || "N/A" }}
						</template>
						<template #reference>
							<div class="cell-wrapper">
								<div class="cell-text">
									{{ scope.row.statusReason || scope.row.outcomes || scope.row.assessmentResponse?.display || "N/A" }}
								</div>
								<span
									v-if="scope.row.type === 'Complete questionnaire regarding social risks' && scope.row.assessmentResponse && scope.row.assessmentResponse?.display"
									class="icon-link"
									@click="$emit('trigger-open-assessment', scope.row.assessmentResponse.id)"
								>
								</span>
							</div>
						</template>
					</el-popover>
				</template>
			</el-table-column>
		</el-table>
		<TaskDialog
			:visible="taskDialogVisible"
			:task-id="activeTaskId"
			:task-name="activeTaskName"
			@close="taskDialogVisible = false"
			@trigger-open-assessment="handleOpenAssessment"
			@trigger-open-action-step="handleOpenActionStep"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.title {
	margin-top: 10px;
	display: flex;
	justify-content: space-between;

	h3 {
		font-weight: $global-font-weight-medium;
		margin: 0 0 0 20px;
	}
}

.table-wrapper {
	background-color: $global-background;
	border-radius: 5px;
	border: 1px solid $global-base-border-color;
	padding: 10px 20px;
	min-height: 130px;

	+ .table-wrapper {
		margin-top: 30px;
	}
}

.icon-link {
	position: relative;
	left: 7px;
	cursor: pointer;

	@include icon("~@/assets/images/link.svg", 14px, 14px);
}

.cell-wrapper {
	display: flex;
	min-width: 60%;

	.icon-link {
		min-height: 14px;
		min-width: 14px;
	}
}

.cell-text {
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
}
</style>
