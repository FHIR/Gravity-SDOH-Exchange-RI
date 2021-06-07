<script lang="ts">
import { defineComponent, PropType } from "vue";
import { TableData } from "@/components/patients/problems/Problems.vue";

export default defineComponent({
	name: "ProblemsTable",
	props: {
		data: {
			type: Array as PropType<TableData[]>,
			required: true
		},
		title: {
			type: String,
			required: true
		}
	},
	setup() {}
});
</script>

<template>
	<div
		class="table-wrapper"
	>
		<div class="title">
			<h3>
				{{ title }}
			</h3>
		</div>
		<el-table :data="data">
			<el-table-column
				label="Problem"
			>
				<template #default="scope">
					<el-button
						type="text"
					>
						{{ scope.row.name }}
					</el-button>
				</template>
			</el-table-column>
			<el-table-column
				label="Based on"
			>
				<template #default="scope">
					<span>{{ scope.row.basedOn }}</span>
				</template>
			</el-table-column>
			<el-table-column
				label="Start Date"
			>
				<template #default="scope">
					<span>{{ $filters.formatDateTime(scope.row.startDate) }}</span>
				</template>
			</el-table-column>
			<el-table-column
				label="Goal(s)"
			>
				<template #default="scope">
					<span>{{ scope.row.goals }}</span>
				</template>
			</el-table-column>
			<el-table-column
				label="Action Steps"
			>
				<template #default="scope">
					<span>{{ scope.row.actionSteps }}</span>
				</template>
			</el-table-column>
			<el-table-column
				label="Actions"
				width="350"
			>
				<template #default="scope">
					<button class="action-button">
						<span class="button-content">
							<span class="icon add-goal"></span>
							<span class="button-text">Add Goal</span>
						</span>
					</button>
					<button class="action-button">
						<span class="button-content">
							<span class="icon add-action-step"></span>
							<span class="button-text">Add Action Step</span>
						</span>
					</button>
					<button class="action-button">
						<span class="button-content">
							<span class="icon mark-as-closed"></span>
							<span class="button-text">Mark As Closed</span>
						</span>
					</button>
				</template>
			</el-table-column>
		</el-table>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.title {
	margin: 10px 20px 0;

	h3 {
		font-weight: $global-font-weight-medium;
		margin: 0;
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

.action-button {
	background-color: $global-background;
	padding: 3px 12px;
	min-height: 25px;
	min-width: 44px;
	border-radius: 19px;
	border: 0.3px solid $global-base-border-color;
	vertical-align: center;
	cursor: pointer;

	.button-content {
		display: flex;
	}

	.button-text {
		font-size: $global-font-size;
		line-height: 16px;
		margin-left: 5px;
		display: none;
	}

	&:hover,
	&:focus {
		background-color: $global-background;
		color: $global-text-color;
		border-color: $global-primary-color;

		.button-text {
			display: inline-block;
		}
	}

	+ .action-button {
		margin-left: 20px;
	}
}

.add-goal {
	@include icon("~@/assets/images/add-goal.svg", 16px);
}

.add-action-step {
	@include icon("~@/assets/images/add-action-step.svg", 16px);
}

.mark-as-closed {
	@include icon("~@/assets/images/mark-as-closed.svg", 16px);
}
</style>
