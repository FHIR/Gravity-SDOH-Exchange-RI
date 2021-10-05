<script lang="ts">
import { defineComponent, ref, h } from "vue";
import { Task, TaskWithState } from "@/types";
import TaskTable from "@/components/TaskTable.vue";
import { getTasks } from "@/api";
import Filters from "@/components/Filters.vue";
import TableCard from "@/components/TableCard.vue";
import TaskEditDialog from "@/components/TaskEditDialog.vue";

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
	components: {
		TableCard,
		Filters,
		TaskTable,
		TaskEditDialog
	},
	props: {
		requestType: {
			type: String,
			required: true
		}
	},
	setup(props) {
		const tasks = ref<TaskWithState[]>([]);
		const activeRequests = ref<Task[]>([]);
		const inactiveRequests = ref<Task[]>([]);

		getTasks().then((resp: Task[]) => {
			activeRequests.value = resp.filter((task: Task) => task.requestType === "active");
			inactiveRequests.value = resp.filter((task: Task) => task.requestType === "inactive");

			props.requestType === "active" ?
				tasks.value = activeRequests.value.map((task: Task) => ({ task, isNew: false })) :
				tasks.value = inactiveRequests.value.map((task: Task) => ({ task, isNew: false }));
		});

		const taskInEdit = ref<Task | null>(null);

		const editTask = (taskToEdit: TaskWithState) => {
			// markTaskAsNotNew(taskToEdit.task.id);
			taskInEdit.value = taskToEdit.task;
		};

		const closeDialog = () => {
			taskInEdit.value = null;
		};

		return {
			tasks,
			editTask,
			closeDialog,
			taskInEdit
		};
	}
});
</script>

<template>
	<div class="tasks">
		<Filters />
		<TableCard>
		<TaskEditDialog
			:task="taskInEdit"
			@close="closeDialog"
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
		</TableCard>
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
		margin-bottom: 30px;
	}

	.table-card {
		flex-grow: 1;
	}
}
</style>
