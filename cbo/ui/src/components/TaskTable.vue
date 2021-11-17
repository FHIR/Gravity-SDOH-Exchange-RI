<script lang="ts">
import { defineComponent, PropType, computed } from "vue";
import { TaskWithState } from "@/types";
import TaskStatusDisplay from "@/components/TaskStatusDisplay.vue";
import { showDate } from "@/utils";
import TableWrapper from "@/components/TableWrapper.vue";
import { TasksModule } from "@/store/modules/tasks";


type TaskDisplayFields = {
	id: string,
	taskName: string,
	isNew: boolean,
	requestDate: string,
	priority: string,
	status: any,
	category: string,
	requestor: string,
	patient: string,
	consent: string,
	performingCBO: string,
	payer: string,
	comment: string,
	outcome: string,
	requestType: string,
	lastSyncDate: string
}


const displayTask = ({ task, isNew }: TaskWithState): TaskDisplayFields => ({
	id: task.id,
	taskName: task.name,
	isNew,
	requestDate: showDate(task.createdAt),
	priority: task.priority,
	status: task.status,
	requestType: task.requestType,
	category: task.serviceRequest.category.display,
	requestor: task.requester.display,
	patient: task.patient.display,
	consent: task.consent,
	performingCBO: "",
	payer: "",
	comment: task.comments[0]?.text || "",
	outcome: task.outcome || "",
	lastSyncDate: TasksModule.lastSyncDate
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
	components: {
		TableWrapper,
		TaskStatusDisplay
	},
	props: {
		tasks: {
			type: Array as PropType<TaskWithState[]>,
			required: true
		},
		loading: {
			type: Boolean,
			default: false
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
			const task: TaskWithState = props.tasks.find(taskState => taskState.task.id === taskId)!;

			ctx.emit("view-resources", task);
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
	<TableWrapper>
		<el-table
			v-loading="loading"
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
				width="120"
			/>

			<el-table-column
				prop="priority"
				label="Priority"
				width="90"
			/>

			<el-table-column
				label="Status"
				width="130"
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
				prop="requestor"
				label="Requestor"
				width="140"
			>
				<template #default="{ row }">
					<span class="clickable-text"> {{ row.requestor }} </span>
				</template>
			</el-table-column>

			<el-table-column
				prop="patient"
				label="Patient"
				width="140"
			>
				<template #default="{ row }">
					<span class="clickable-text">{{ row.patient }}</span>
				</template>
			</el-table-column>

			<el-table-column
				prop="consent"
				label="Consent"
				width="80"
			/>

			<el-table-column
				prop="comment"
				label="Comment"
				width="100"
			/>

			<el-table-column
				prop="outcome"
				label="Outcome/Reason"
				width="160"
			/>

			<el-table-column
				label="Resources"
				width="120"
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

			<el-table-column
				label="Synchronization Status"
				class-name="sync-cell"
			>
				<template #default="{ row }">
					<div class="sync-wrapper">
						<div class="sync-icon"></div>
						Synced <span class="sync-date">{{ $filters.formatDateTime(row.lastSyncDate) }}</span>
					</div>
				</template>
			</el-table-column>
		</el-table>
	</TableWrapper>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

::v-deep(.el-button--text) {
	font-weight: $global-font-weight-normal;
	text-decoration: underline;
	font-size: $global-medium-font-size;
	color: $global-primary-color;
}

.new-task {
	font-weight: $global-font-weight-medium;
}

.column-interactive .cell:not(:empty) {
	color: $global-primary-color;
	text-decoration: underline;
	cursor: pointer;
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

.sync-wrapper {
	display: flex;
	align-items: center;

	.sync-icon {
		background-image: url("~@/assets/images/sync-icon.svg");
		width: 16px;
		height: 16px;
		margin-right: 5px;
	}

	.sync-date {
		margin-left: 40px;
	}
}
</style>
