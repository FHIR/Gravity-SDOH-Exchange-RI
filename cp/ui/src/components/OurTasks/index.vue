<script lang="ts">
import { defineComponent, ref } from "vue";
import TaskTable from "@/components/TaskTable.vue";
import TaskResourcesDialog from "@/components/TaskResourcesDialog.vue";
import ViewTaskDialog from "./ViewTaskDialog.vue";
import useOurTasks from "@/state/useOurTasks";


export default defineComponent({
	components: { TaskResourcesDialog, TaskTable, ViewTaskDialog },
	setup() {
		const { ourTasks, markVisited } = useOurTasks();

		const taskToViewId = ref<string | null>(null);

		const closeDialog = () => {
			taskToViewId.value = null;
		};

		const viewTask = (id: string) => {
			markVisited(id);
			taskToViewId.value = id;
		};

		const taskToViewResourcesId = ref<string | null>(null);
		const viewTaskResources = (taskId: string) => {
			taskToViewResourcesId.value = taskId;
		};

		return {
			ourTasks,
			taskToViewId,
			taskToViewResourcesId,
			viewTask,
			viewTaskResources,
			closeDialog,
		};
	}
});
</script>

<template>
	<div class="tasks">
		<TaskResourcesDialog
			:task-id="taskToViewResourcesId"
			@close="taskToViewResourcesId = null"
		/>

		<ViewTaskDialog
			:task-id="taskToViewId"
			@close="taskToViewId = null"
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
				:tasks="ourTasks"
				variant="our-tasks"
				@task-name-click="viewTask"
				@view-resources="viewTaskResources"
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
