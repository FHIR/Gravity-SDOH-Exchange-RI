<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { TableData } from "@/components/patients/goals/Goals.vue";

export default defineComponent({
	name: "GoalsTable",
	props: {
		data: {
			type: Array as PropType<TableData[]>,
			required: true
		},
		status: {
			type: String,
			default: "active"
		}
	},
	setup(props) {
		const title = ref<string>(props.status === "active" ? "Active Goals" : "Completed Goals");

		return {
			title
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
			>
				Add Goal
			</el-button>
		</div>
		<el-table :data="data">
			<el-table-column label="Goal">
				<template #default="scope">
					<el-button type="text">
						{{ scope.row.name }}
					</el-button>
				</template>
			</el-table-column>

			<el-table-column label="Problem(s)">
				<template #default="scope">
					<el-button type="text">
						{{ scope.row.problems }}
					</el-button>
				</template>
			</el-table-column>

			<el-table-column
				label="Added By"
				prop="addedBy"
			/>

			<el-table-column label="Start Date">
				<template #default="scope">
					{{ $filters.formatDateTime(scope.row.startDate) }}
				</template>
			</el-table-column>

			<el-table-column label="Targets">
				<template #default="scope">
					<el-button type="text">
						{{ scope.row.targets.length }}
					</el-button>
				</template>
			</el-table-column>

			<el-table-column
				v-if="status === 'active'"
				label="Actions"
			>
				actions
			</el-table-column>

			<el-table-column
				v-if="status === 'completed'"
				label="End Date"
			>
				<template #default="scope">
					{{ $filters.formatDateTime(scope.row.endDate) }}
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
}
</style>
