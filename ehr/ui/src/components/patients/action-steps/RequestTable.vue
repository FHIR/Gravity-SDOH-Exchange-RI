<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import EditRequestDialog from "@/components/patients/action-steps/EditRequestDialog.vue";
import { TableData } from "@/components/patients/action-steps/ActionSteps.vue";
import TaskStatusIcon from "@/components/patients/TaskStatusIcon.vue";

export default defineComponent({
	name: "RequestTable",
	components: {
		EditRequestDialog,
		TaskStatusIcon
	},
	props: {
		data: {
			type: Array as PropType<TableData[]>,
			required: true
		},
		title: {
			type: String,
			default: "Active Requests"
		}
	},
	setup() {
		const editRequestDialogVisible = ref<boolean>(false);
		const editRequest = ref<TableData>();
		const onRequestClick = (row: TableData) => {
			editRequestDialogVisible.value = true;
			editRequest.value = row;
		};

		return {
			editRequestDialogVisible,
			onRequestClick,
			editRequest
		};
	}
});
</script>

<template>
	<div>
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
					label="Request/Task"
				>
					<template #default="scope">
						<el-button
							type="text"
							@click="onRequestClick(scope.row)"
						>
							{{ scope.row.name }}
						</el-button>
					</template>
				</el-table-column>
				<el-table-column
					label="Status"
				>
					<template #default="scope">
						<div class="status-cell">
							<TaskStatusIcon :status="scope.row.status" />
							<div class="info">
								<span class="status">{{ scope.row.status }}</span>
								<span class="date">{{ $filters.formatDateTime(scope.row.lastModified) }}</span>
							</div>
						</div>
					</template>
				</el-table-column>
				<el-table-column
					label="Category"
				>
					<template #default="scope">
						{{ scope.row.category.display }}
					</template>
				</el-table-column>
				<el-table-column
					label="Problem(s)"
				>
					<template #default="scope">
						<a :class="{underline: scope.row.problems.length}">
							{{ scope.row.problems.length > 1 ? `${scope.row.problems.length} Problems` : scope.row.problems.length === 1 ? scope.row.problems[0].display : "--" }}
						</a>
					</template>
				</el-table-column>
				<el-table-column
					label="Goal(s)"
				>
					<template #default="scope">
						<a :class="{underline: scope.row.goals.length}">
							{{ scope.row.goals.length > 1 ? `${scope.row.goals.length} Goals` : scope.row.goals.length === 1 ? scope.row.goals[0].display : "--" }}
						</a>
					</template>
				</el-table-column>
				<el-table-column
					prop="performer"
					label="Performer"
				/>
				<el-table-column
					prop="consent"
					label="Consent"
				/>
				<el-table-column
					label="Outcomes"
				>
					<template #default="scope">
						{{ scope.row.statusReason ? scope.row.statusReason : scope.row.outcomes }}
					</template>
				</el-table-column>
				<el-table-column
					label="Comment(s)"
				>
					<template #default="scope">
						{{ scope.row.comments.length > 1 ? `${scope.row.comments.length} comments` : scope.row.comments.length === 1 ? scope.row.comments[0].text : "--" }}
					</template>
				</el-table-column>
			</el-table>
		</div>

		<EditRequestDialog
			:visible="editRequestDialogVisible"
			:task="editRequest"
			@close="editRequestDialogVisible = false"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

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

.underline {
	text-decoration: underline;
}
</style>
