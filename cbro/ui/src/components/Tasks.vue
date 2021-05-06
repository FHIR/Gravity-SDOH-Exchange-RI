<script lang="ts">
import { defineComponent, ref, computed } from "vue";
import { Task } from "@/types";
import TaskTable from "@/components/TaskTable.vue";
import { getTasks } from "@/api";


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
	components: { TaskTable },
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

		poll(
			getTasks,
			newResp => {
				updateTasks(newResp);
				return true;
			},
			10000
		);

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
			<TaskTable :tasks="tasks" />
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
