<script lang="ts">
import { defineComponent, ref, h } from "vue";
import { Task, TaskWithState } from "@/types";
import TaskTable from "@/components/TaskTable.vue";
import { getTasks } from "@/api";

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
