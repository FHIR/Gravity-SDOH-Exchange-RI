<script lang="ts">
import { defineComponent, computed, onMounted, ref } from "vue";
import { TasksModule } from "@/store/modules/tasks";
import { TaskResponse } from "@/types";

export type TableData = {
	request: string,
	status: string,
	category: string,
	problems: string,
	goals: string,
	performer: string | null | undefined,
	consent: string | boolean,
	outcomes: string | null,
	comment: string
}

export default defineComponent({
	name: "RequestTable",
	setup() {
		const isLoading = ref<boolean>(false);

		const tasks = computed<TaskResponse[] | null>(() => TasksModule.tasks);
		const tableData = computed<TableData[]>(() => {
			const res: TableData[] = [];

			tasks.value && tasks.value.forEach((task: TaskResponse) => {
				res.push({
					request: task.serviceRequest.request,
					status: task.status,
					category: task.serviceRequest.category,
					//todo: no api for that
					problems: "",
					//todo: no api for that
					goals: "",
					performer: task.organization?.name,
					//todo: no api for that
					consent: "yes",
					outcomes: task.outcome,
					//todo: is it comments tho?
					comment: task.serviceRequest.details
				});
			});

			return res;
		});

		onMounted(async() => {
			isLoading.value = true;
			try {
				await TasksModule.getTasks();
			} finally {
				isLoading.value = false;
			}
		});

		return {
			tableData,
			isLoading
		};
	}
});
</script>

<template>
	<div>
		<h4 class="title">
			Request Table
		</h4>
		<el-table
			v-loading="isLoading"
			:data="tableData"
		>
			<el-table-column
				prop="request"
				label="Request/Task"
			/>
			<el-table-column
				prop="status"
				label="Status"
			/>
			<el-table-column
				prop="category"
				label="Category"
			/>
			<el-table-column
				prop="problems"
				label="Problem(s)"
			/>
			<el-table-column
				prop="goals"
				label="Goal(s)"
			/>
			<el-table-column
				prop="performer"
				label="Performer"
			/>
			<el-table-column
				prop="consent"
				label="Consent"
			/>
			<el-table-column
				prop="outcomes"
				label="Outcomes"
			/>
			<el-table-column
				prop="comment"
				label="Comment"
			/>
		</el-table>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.title {
	font-weight: $global-font-weight-medium;
}

.el-table {
	border-radius: 5px;
	box-shadow: 0 2px 5px rgba(51, 51, 51, 0.25);

	::v-deep(.cell) {
		white-space: nowrap;
	}
}
</style>
