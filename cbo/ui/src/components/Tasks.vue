<script lang="ts">
import { defineComponent, ref, h } from "vue";
import { Task, TaskWithState } from "@/types";
import TaskTable from "@/components/TaskTable.vue";
import { getTasks } from "@/api";
import TaskEditDialog from "@/components/TaskEditDialog.vue";
import TaskStatusDisplay from "@/components/TaskStatusDisplay.vue";


const poll = <T>(
	makeRequest: () => Promise<T>,
	proceed: (t: T) => boolean,
	ms: number
) => {
	const next = () => {
		setTimeout(async () => {
			try {
				const resp = await makeRequest();
				if (proceed(resp)) {
					next();
				}
			} catch {
				next();
			}
		}, ms);
	};
	next();
};


export default defineComponent({
	components: { TaskTable },
	setup() {
		const tasks = ref<TaskWithState[]>([]);

		const foo = getTasks();
		console.log(foo);
		tasks.value = foo.map(task => ({ task, isNew: false }));
		// getTasks().then(resp => {
		// 	tasks.value = resp.map(task => ({ task, isNew: false }));
		// });

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

		const findUpdates = (newList: Task[]): { name: string, oldStatus: string, newStatus: string }[] =>
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
				return [{
					name: task.name,
					oldStatus,
					newStatus
				}];
			});

		const showUpdates = (newList: Task[]) => {
			findUpdates(newList).forEach(update => {
				const message = h("p", [
					`EHR changed status of task "${update.name}" from `,
					//todo: for some reason ts pops up error about incorrect import
					// @ts-ignore
					h(TaskStatusDisplay, {
						status: update.oldStatus,
						small: true
					}),
					" to ",
					// @ts-ignore
					h(TaskStatusDisplay,{
						status: update.newStatus,
						small: true
					})
				]);
			});
		};

		return {
			tasks
		};
	}
});
</script>

<template>
	<div class="tasks">
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
	background-color: $alice-blue;

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
		//flex: 1;
		height: calc(100% - 210px);
		width: 100%;
		background-color: $global-background;
		box-shadow: 0 2px 5px rgba(51, 51, 51, 0.25);
		border-radius: 5px;
		overflow: auto;
		padding: 0 20px;
	}
}
</style>
