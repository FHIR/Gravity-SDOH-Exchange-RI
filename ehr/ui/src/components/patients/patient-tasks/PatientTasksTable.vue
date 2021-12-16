<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { TableData } from "@/components/patients/patient-tasks/PatientTasks.vue";

export default defineComponent({
	props: {
		data: {
			type: Array as PropType<TableData[]>,
			required: true
		},
		status: {
			type: String as PropType<"active" | "completed">,
			default: "active"
		}
	},
	emits: ["add-task"],
	setup(props) {
		const title = ref<string>(props.status === "active" ? "Active Tasks" : "Completed Tasks");

		const onTaskClick = (row: TableData) => {
			console.log(row);
		};

		return {
			title,
			onTaskClick
		};
	}
});
</script>

<template>
	<div class="table-wrapper">
		<div class="title">
			<h3>
				{{ title }}
			</h3>
			<el-button
				v-if="status === 'active'"
				plain
				round
				type="primary"
				size="mini"
				@click="$emit('add-task')"
			>
				Add Patient Task
			</el-button>
		</div>
		<el-table :data="data">
			<el-table-column label="Task Name">
				<template #default="scope">
					<el-button
						type="text"
						@click="onTaskClick(scope.row)"
					>
						{{ scope.row.name }}
					</el-button>
				</template>
			</el-table-column>

			<el-table-column
				prop="type"
				label="Type"
			/>

			<el-table-column
				prop="code"
				label="Code"
			/>

			<el-table-column
				prop="status"
				label="Status"
			/>

			<el-table-column
				prop="Date"
				label="Date"
			/>

			<el-table-column
				prop="basedOn"
				label="Based On"
			/>

			<el-table-column label="Outcomes">
				<template #default="scope">
					{{ scope.row.statusReason ? scope.row.statusReason : scope.row.outcomes }}
				</template>
			</el-table-column>
		</el-table>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.title {
	margin-top: 10px;
	display: flex;
	justify-content: space-between;

	h3 {
		font-weight: $global-font-weight-medium;
		margin: 0 0 0 20px;
	}
}

.table-wrapper {
	background-color: $global-background;
	border-radius: 5px;
	border: 1px solid $global-base-border-color;
	padding: 10px 20px;
	min-height: 130px;

	+ .table-wrapper {
		margin-top: 30px;
	}
}
</style>
