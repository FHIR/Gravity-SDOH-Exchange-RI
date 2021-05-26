<script lang="ts">
import { defineComponent, PropType, computed } from "vue";
import { Task, TaskStatus, TaskWithState } from "@/types";
import TaskStatusDisplay from "@/components/TaskStatusDisplay.vue";
import { showDate } from "@/utils";


type TaskDisplayFields = {
	id: string,
	taskName: string,
	isNew: boolean,
	requestDate: string,
	priority: string,
	status: TaskStatus,
	category: string,
	requestor: string,
	patient: string,
	consent: string,
	performingCBO: string,
	payer: string,
	comment: string,
	outcome: string
}


const displayTask = ({ task, isNew }: TaskWithState): TaskDisplayFields => ({
	id: task.id,
	taskName: task.name,
	isNew,
	requestDate: showDate(task.createdAt),
	priority: task.priority,
	status: task.status,
	category: task.serviceRequest.category.display,
	requestor: task.requester.display,
	patient: task.patient.display,
	consent: task.consent,
	performingCBO: "",
	payer: "",
	comment: task.comments[0]?.text || "",
	outcome: task.outcome || ""
});


const orderOnTasks = (left: TaskWithState, right: TaskWithState): number => {
	if (left.isNew !== right.isNew) {
		return left.isNew ? -1 : 1;
	}
	if (left.task.createdAt !== right.task.createdAt) {
		return new Date(left.task.createdAt) >= new Date(right.task.createdAt) ? -1 : 1;
	}
	return 0;
};


export default defineComponent({
	components: { TaskStatusDisplay },
	props: {
		tasks: {
			type: Array as PropType<TaskWithState[]>,
			required: true
		}
	},
	emits: ["task-name-click", "view-resources"],
	setup(props, ctx) {
		const tasksInOrder = computed(() => [...props.tasks].sort(orderOnTasks));
		const tableData = computed(() => tasksInOrder.value.map(displayTask));

		const taskNameClick = (taskId: string) => {
			const task: TaskWithState = props.tasks.find(taskState => taskState.task.id === taskId)!;
			ctx.emit("task-name-click", task);
		};

		const taskViewResourcesClick = (taskId: string) => {
			ctx.emit("view-resources", taskId);
		};

		return {
			tableData,
			taskNameClick,
			taskViewResourcesClick
		};
	}
});
</script>

<template>
	<div class="task-table">
		<el-table
			:data="tableData"
			:row-class-name="({ row }) => row.isNew ? 'new-task' : ''"
		>
			<el-table-column
				label="Task Name"
				width="290"
			>
				<template #default="{ row }">
					<div
						class="task-name-cell"
						@click="taskNameClick(row.id)"
					>
						<span class="name">
							{{ row.taskName }}
						</span>
						<span
							v-if="row.isNew"
							class="new-mark"
						>
							new
						</span>
					</div>
				</template>
			</el-table-column>

			<el-table-column
				prop="requestDate"
				label="Request Date"
				:width="120"
			/>

			<el-table-column
				prop="priority"
				label="Priority"
				:width="90"
			/>

			<el-table-column
				label="Status"
				:width="130"
			>
				<template #default="{ row }">
					<!-- scope.row is empty on first render for some reason -->
					<TaskStatusDisplay
						v-if="row.status"
						:status="row.status"
					/>
				</template>
			</el-table-column>

			<el-table-column
				prop="category"
				label="Category"
			/>

			<el-table-column
				prop="requestor"
				label="Requestor"
				class-name="column-interactive"
			/>

			<el-table-column
				prop="patient"
				label="Patient"
				class-name="column-interactive"
			/>

			<el-table-column
				prop="consent"
				label="Consent"
				:width="80"
			/>

			<el-table-column
				prop="performingCBO"
				label="Performing CBO"
				class-name="column-interactive"
			/>

			<el-table-column
				prop="payer"
				label="Payer"
			/>

			<el-table-column
				prop="comment"
				label="Comment"
			/>

			<el-table-column
				prop="outcome"
				label="Outcome"
			/>

			<el-table-column
				label="Resource"
				:width="90"
			>
				<template #default="{ row }">
					<el-button
						type="text"
						@click="taskViewResourcesClick(row.id)"
					>
						view
					</el-button>
				</template>
			</el-table-column>
		</el-table>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.task-table {
	height: 100%;

	::v-deep(.el-table) {
		height: 100%;
		display: flex;
		flex-direction: column;
		font-size: $global-medium-font-size;
		font-weight: 400;
		color: $global-text-color;

		.el-table__header-wrapper {
			flex-shrink: 0;
		}

		.el-table__body-wrapper {
			flex: 1;
			overflow: overlay;
		}

		&::before {
			display: none;
		}

		.cell {
			overflow-x: hidden;
			white-space: nowrap;
		}

		.el-table__header {
			th {
				padding: 0;
				height: 65px;
				border: none;
				font-weight: 400;
				color: $grey;
			}
		}

		.el-table__body {
			border-spacing: 0 10px;
			margin: -9px 0 -9px 0;

			tr.new-task {
				font-weight: 500;
			}

			td {
				padding: 0;
				height: 50px;
				border-top: $global-border;
				border-bottom: $global-border;

				&:first-child {
					border-left: $global-border;
					border-top-left-radius: 5px;
					border-bottom-left-radius: 5px;
				}

				&:last-child {
					border-right: $global-border;
					border-top-right-radius: 5px;
					border-bottom-right-radius: 5px;
				}
			}

			.el-button--text {
				color: $global-primary-color;
				text-decoration: underline;
			}

			.cell:empty::after {
				content: "--";
			}

			.column-interactive .cell:not(:empty) {
				color: $global-primary-color;
				text-decoration: underline;
				cursor: pointer;
			}
		}

		.task-name-cell {
			width: 100%;
			display: flex;
			justify-content: space-between;
			cursor: pointer;

			.name {
				flex-shrink: 1;
				overflow: hidden;
				text-overflow: ellipsis;
				color: $global-primary-color;
				text-decoration: underline;
			}

			.new-mark {
				line-height: 11px;
				display: inline-block;
				padding: 4px;
				background-color: #e04558;
				font-size: $global-font-size;
				font-weight: 400;
				color: $white;
				border-radius: 1px;
				user-select: none;
			}
		}
	}
}
</style>
