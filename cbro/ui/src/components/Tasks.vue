<script lang="ts">
import { defineComponent, ref, computed } from "vue";
import { Task } from "@/types";
import TaskTable from "@/components/TaskTable.vue";
import { getTasks } from "@/api";
import Dialog from "./Dialog.vue";
import { ElNotification } from "element-plus";


type TaskState = {
	task: Task,
	isNew: boolean
}


const poll = <T>(
	makeRequest: () => Promise<T>,
	proceed: (t: T) => boolean,
	ms: number
) => {
	const next = () => {
		setTimeout(async () => {
			const resp = await makeRequest();
			if (proceed(resp)) {
				next();
			}
		}, ms);
	};
	next();
};


export default defineComponent({
	props: {},
	components: { TaskTable, Dialog },
	setup() {
		const tasks = ref<TaskState[]>([]);

		getTasks().then(resp => {
			tasks.value = resp.map(task => ({ task, isNew: false }));
		});

		const isTaskNew = (task: Task) => {
			const existingTask = tasks.value.find(ts => ts.task.id === task.id);
			return existingTask === undefined || existingTask.isNew;
		};

		const updateTasks = (newList: Task[]) => {
			tasks.value = newList.map(task => ({
				task,
				isNew: isTaskNew(task)
			}));
		};

		const findUpdates = (newList: Task[]): string[] =>
			newList.flatMap(task => {
				const existingTask = tasks.value.find(ts => ts.task.id === task.id);
				if (!existingTask) {
					return [];
				}
				const oldStatus = existingTask.task.status;
				const newStatus = task.status;
				if (oldStatus === newStatus) {
					return [];
				}
				return [`EHR changed status of task "${task.name}" from ${oldStatus} to ${newStatus}.`];
			});

		const showUpdates = (newList: Task[]) => {
			findUpdates(newList).forEach(update => {
				ElNotification({
					title: "Update",
					type: "info",
					message: update
				});
			});
		};

		const markTaskAsNotNew = (taskId: string) => {
			tasks.value = tasks.value.map(taskState => taskState.task.id === taskId ? { ...taskState, isNew: false } : taskState);
		};

		const updateTaskFromDialog = (task: Task) => {
			tasks.value = tasks.value.map(taskState => taskState.task.id === task.id ? { ...taskState, task } : taskState);
		};

		poll(
			getTasks,
			newResp => {
				showUpdates(newResp);
				updateTasks(newResp);
				return true;
			},
			5000
		);

		const taskInEdit = ref<Task | null>(null);

		const editTask = (taskToEdit: TaskState) => {
			markTaskAsNotNew(taskToEdit.task.id);
			taskInEdit.value = taskToEdit.task;
		};

		return {
			tasks,
			taskInEdit,
			editTask,
			updateTaskFromDialog
		};
	}
});
</script>

<template>
	<div class="tasks">
		<Dialog
			:task="taskInEdit"
			@close="taskInEdit = null"
			@task-updated="updateTaskFromDialog"
		/>

		<div class="filters">
			<label>Search:</label>
			<el-input
				placeholder="Search..."
				size="mini"
			/>

			<label>Filter by:</label>
			<el-input
				size="mini"
			/>
		</div>

		<div class="table-card">
			<TaskTable
				:tasks="tasks"
				@task-name-click="editTask"
			/>
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.tasks {
	height: 100%;
	display: flex;
	flex-direction: column;

	.filters {
		flex-shrink: 0;
		width: 100%;
		padding: 20px;
		background-color: $global-background;
		box-shadow: 0 2px 5px rgba(51, 51, 51, 0.25);
		border-radius: 5px;
		display: flex;
		align-items: center;

		label {
			margin-right: 10px;
			font-size: $global-font-size;
			font-weight: 400;
		}

		::v-deep(.el-input) {
			width: 350px;
			margin-right: 50px;
		}
	}

	.table-card {
		margin-top: 30px;
		flex: 1;
		width: 100%;
		background-color: $global-background;
		box-shadow: 0 2px 5px rgba(51, 51, 51, 0.25);
		border-radius: 5px;
		overflow: auto;
		padding: 0 20px;
	}
}
</style>
