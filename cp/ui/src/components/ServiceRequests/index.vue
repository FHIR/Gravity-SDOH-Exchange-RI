<script lang="ts">
import { defineComponent, ref, onUnmounted } from "vue";
import TaskTable from "@/components/TaskTable.vue";
import TaskResourcesDialog from "@/components/TaskResourcesDialog.vue";
import TaskEditDialog from "./TaskEditDialog.vue";
import useServiceRequests from "@/state/useServiceRequests";


export default defineComponent({
	components: { TaskEditDialog, TaskResourcesDialog, TaskTable },
	setup() {
		const alive = ref(true);
		onUnmounted(() => alive.value = false);

		const { serviceRequests, markVisited } = useServiceRequests();

		const taskToEditId = ref<string | null>(null);

		const closeDialog = () => {
			taskToEditId.value = null;
		};

		const editTask = (id: string) => {
			markVisited(id);
			taskToEditId.value = id;
		};

		const taskToViewResourcesId = ref<string | null>(null);

		const viewResources = (id: string) => {
			taskToViewResourcesId.value = id;
		};

		return {
			serviceRequests,
			taskToEditId,
			taskToViewResourcesId,
			editTask,
			viewResources,
			closeDialog,
		};
	}
});
</script>

<template>
	<div class="tasks">
		<TaskEditDialog
			:task-id="taskToEditId"
			@close="closeDialog"
		/>

		<TaskResourcesDialog
			:task-id="taskToViewResourcesId"
			@close="taskToViewResourcesId = null"
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
				:tasks="serviceRequests"
				@task-name-click="editTask"
				@view-resources="viewResources"
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
