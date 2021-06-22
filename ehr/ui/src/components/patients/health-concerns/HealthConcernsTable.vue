<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import EditConcernDialog from "@/components/patients/health-concerns/EditConcernDialog.vue";
import { TableData } from "@/components/patients/health-concerns/HealthConcerns.vue";
import ActionButton from "@/components/patients/ActionButton.vue";
import NewConcernDialog from "@/components/patients/health-concerns/NewConcernDialog.vue";
import { ACTION_BUTTONS } from "@/utils/constants";

export default defineComponent({
	name: "HealthConcernsTable",
	components: {
		NewConcernDialog,
		EditConcernDialog,
		ActionButton
	},
	props: {
		data: {
			type: Array as PropType<TableData[]>,
			required: true
		},
		title: {
			type: String,
			default: "Active Health Concerns"
		},
		type: {
			type: String,
			required: true
		}
	},
	setup() {
		const editConcernDialogVisible = ref<boolean>(false);
		const newConcernDialogVisible = ref<boolean>(false);
		const editConcern = ref<TableData>();
		const clickedAction = ref<string>("");

		const concernOrActionClick = (row: TableData, val: string) => {
			editConcernDialogVisible.value = true;
			editConcern.value = row;
			clickedAction.value = val;
		};

		return {
			editConcernDialogVisible,
			newConcernDialogVisible,
			concernOrActionClick,
			editConcern,
			clickedAction,
			ACTION_BUTTONS
		};
	}
});
</script>

<template>
	<div>
		<div
			class="table-wrapper"
		>
			<div class="header">
				<div class="title">
					<h3>
						{{ title }}
					</h3>
				</div>
				<el-button
					v-if="type === 'ActiveConcerns'"
					plain
					round
					type="primary"
					size="mini"
					@click="newConcernDialogVisible = true"
				>
					Add Health Concern
				</el-button>
			</div>
			<el-table :data="data">
				<el-table-column
					label="Health Concern"
				>
					<template #default="scope">
						<el-button
							type="text"
							@click="concernOrActionClick(scope.row, ACTION_BUTTONS.default)"
						>
							{{ scope.row.name }}
						</el-button>
					</template>
				</el-table-column>
				<el-table-column
					label="Category"
				>
					<template #default="scope">
						{{ scope.row.category }}
					</template>
				</el-table-column>
				<el-table-column
					label="Based On"
				>
					<template #default="scope">
						{{ scope.row.basedOn }}
					</template>
				</el-table-column>
				<el-table-column
					label="Assessment Date"
				>
					<template #default="scope">
						{{ $filters.formatDateTime(scope.row.assessmentDate) }}
					</template>
				</el-table-column>
				<el-table-column
					v-if="type === 'ActiveConcerns'"
					label="Actions"
					width="350"
				>
					<template #default="scope">
						<ActionButton
							icon-class="icon-promote"
							label="Promote to Problem"
							@clicked="concernOrActionClick(scope.row, ACTION_BUTTONS.promoteToProblem)"
						/>
						<ActionButton
							icon-class="icon-resolved"
							label="Mark as Resolved"
							@clicked="concernOrActionClick(scope.row, ACTION_BUTTONS.markAsResolved)"
						/>
						<ActionButton
							icon-class="icon-remove"
							label="Remove"
							@clicked="concernOrActionClick(scope.row, ACTION_BUTTONS.remove)"
						/>
					</template>
				</el-table-column>
				<el-table-column
					v-if="type === 'PromotedOrResolvedConcerns'"
					label="Status"
				>
					<template #default="scope">
						{{ scope.row.status }}
					</template>
				</el-table-column>
				<el-table-column
					v-if="type === 'PromotedOrResolvedConcerns'"
					label="Promotion/Resolution Date"
				>
					May 5, 2021, 10:00 AM
				</el-table-column>
			</el-table>
		</div>

		<EditConcernDialog
			:visible="editConcernDialogVisible"
			:concern="editConcern"
			:clicked-action="clickedAction"
			@change-action="clickedAction = $event"
			@close="editConcernDialogVisible = false;"
		/>

		<NewConcernDialog
			:visible="newConcernDialogVisible"
			@close="newConcernDialogVisible = false"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.header {
	display: flex;
	justify-content: space-between;

	.el-button {
		width: 155px;
		height: 25px;
		margin-top: 10px;
	}
}

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
}

::v-deep(.el-table--enable-row-hover) .el-table__body tr:hover {
	box-shadow: 0 2px 5px 0 rgba(51, 51, 51, 0.25);
	border-radius: 5px;
}
</style>
