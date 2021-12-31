<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import EditConcernDialog from "@/components/patients/health-concerns/EditConcernDialog.vue";
import { TableData } from "@/components/patients/health-concerns/HealthConcerns.vue";
import ActionButton from "@/components/patients/ActionButton.vue";

export type ConcernAction = "view" | "mark-as-resolved" | "promote-to-problem" | "remove";

export default defineComponent({
	name: "HealthConcernsTable",
	components: {
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
	emits: ["add-concern", "trigger-open-assessment"],
	setup(props, { emit }) {
		const editConcernDialogVisible = ref<boolean>(false);
		const newConcernDialogVisible = ref<boolean>(false);
		const editConcern = ref<TableData>();
		const dialogOpenPhase = ref<ConcernAction>("view");

		const concernOrActionClick = (row: TableData, phase: ConcernAction) => {
			dialogOpenPhase.value = phase;
			editConcernDialogVisible.value = true;
			editConcern.value = row;
		};

		const handleOpenAssessment = (id: string) => {
			editConcernDialogVisible.value = false;
			emit("trigger-open-assessment", id);
		};

		return {
			editConcernDialogVisible,
			newConcernDialogVisible,
			concernOrActionClick,
			editConcern,
			dialogOpenPhase,
			handleOpenAssessment
		};
	}
});
</script>

<template>
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
				v-if="type === 'active'"
				plain
				round
				type="primary"
				size="mini"
				@click="$emit('add-concern')"
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
						@click="concernOrActionClick(scope.row, 'view')"
					>
						{{ scope.row.name }}
					</el-button>
				</template>
			</el-table-column>
			<el-table-column
				label="Category"
			>
				<template #default="scope">
					{{ scope.row.category ? scope.row.category.display : "N/A" }}
				</template>
			</el-table-column>
			<el-table-column
				label="Based On"
			>
				<template #default="scope">
					{{ scope.row.basedOn.display ? scope.row.basedOn.display : scope.row.basedOn }}
					<span
						v-if="scope.row.basedOn.id"
						class="icon-link"
						@click="$emit('trigger-open-assessment', scope.row.basedOn.id)"
					>
					</span>
				</template>
			</el-table-column>
			<el-table-column
				label="Assessment Date"
			>
				<template #default="scope">
					{{ scope.row.assessmentDate ? $filters.formatDateTime(scope.row.assessmentDate) : "N/A" }}
				</template>
			</el-table-column>
			<el-table-column
				v-if="type === 'resolved'"
				label="Resolution Date"
				width="350"
			>
				<template #default="scope">
					{{ scope.row.resolutionDate ? $filters.formatDateTime(scope.row.resolutionDate) : "N/A" }}
				</template>
			</el-table-column>
			<el-table-column
				v-if="type === 'active'"
				label="Actions"
				width="350"
			>
				<template #default="scope">
					<ActionButton
						icon-class="icon-promote"
						label="Promote to Problem"
						@clicked="concernOrActionClick(scope.row, 'promote-to-problem')"
					/>
					<ActionButton
						icon-class="icon-resolved"
						label="Mark as Resolved"
						@clicked="concernOrActionClick(scope.row, 'mark-as-resolved')"
					/>
					<ActionButton
						icon-class="icon-remove"
						label="Remove"
						@clicked="concernOrActionClick(scope.row, 'remove')"
					/>
				</template>
			</el-table-column>
		</el-table>

		<EditConcernDialog
			:visible="editConcernDialogVisible"
			:concern="editConcern"
			:open-phase="dialogOpenPhase"
			:type="type"
			@close="editConcernDialogVisible = false;"
			@trigger-open-assessment="handleOpenAssessment"
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

	+ .table-wrapper {
		margin-top: 30px;
	}
}

.icon-link {
	position: relative;
	left: 7px;
	cursor: pointer;

	@include icon("~@/assets/images/link.svg", 14px, 14px);
}
</style>
