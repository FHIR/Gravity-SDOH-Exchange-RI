<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import EditRequestDialog from "@/components/patients/EditRequestDialog.vue";
import { TableData } from "@/components/patients/ActionSteps.vue";

export default defineComponent({
	name: "RequestTable",
	components: {
		EditRequestDialog
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
		<div class="title">
			<h4>
				{{ title }}
			</h4>
		</div>
		<div
			class="table-wrapper"
		>
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
							<span
								class="icon"
								:class="scope.row.status.toLocaleLowerCase()"
							></span>
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
					prop="outcomes"
					label="Outcomes"
				/>
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
@import "~@/assets/scss/abstracts/mixins";

.title {
	margin: 20px 0;

	h4 {
		font-weight: $global-font-weight-medium;
		margin: 0;
	}
}

.table-wrapper {
	background-color: $global-background;
	border-radius: 5px;
	box-shadow: 0 2px 5px rgba(51, 51, 51, 0.25);
	padding: 10px 20px;
	min-height: 130px;
}

.underline {
	text-decoration: underline;
}

.el-table {
	&::before {
		background-color: $global-background;
	}

	::v-deep(.el-table__body) {
		border-spacing: 0 10px;

		th,
		td {
			padding: 7px 0;
		}
	}

	::v-deep(.el-table__row) {
		td {
			border-top: 1px solid $global-base-border-color;
			border-bottom: 1px solid $global-base-border-color;

			&:first-child {
				border-left: 1px solid $global-base-border-color;
				border-radius: 5px 0 0 5px;
			}

			&:last-child {
				border-right: 1px solid $global-base-border-color;
				border-radius: 0 5px 5px 0;
			}
		}
	}

	::v-deep(th.is-leaf) {
		border-bottom: 0;
	}

	::v-deep(.el-table__header-wrapper) {
		.cell {
			color: $grey;
			font-weight: $global-font-weight-normal;
		}
	}

	::v-deep(.cell) {
		white-space: nowrap;
		padding: 0 15px;
		font-size: $global-medium-font-size;
		line-height: 19px;
	}

	.status-cell {
		display: flex;
		align-items: center;
		height: 35px;
		line-height: 1;

		.info {
			display: flex;
			flex-direction: column;
		}

		.icon {
			margin-right: 5px;

			&.completed {
				@include icon("~@/assets/images/status-completed.svg", 20px);
			}

			&.accepted {
				@include icon("~@/assets/images/status-accepted.svg", 20px);
			}

			&.cancelled {
				@include icon("~@/assets/images/status-cancelled.svg", 20px);
			}

			&.failed {
				@include icon("~@/assets/images/status-failed.svg", 20px);
			}

			&.inprogress {
				@include icon("~@/assets/images/status-in-progress.svg", 20px);
			}

			&.onhold {
				@include icon("~@/assets/images/status-on-hold.svg", 20px);
			}

			&.received {
				@include icon("~@/assets/images/status-received.svg", 20px);
			}

			&.rejected {
				@include icon("~@/assets/images/status-rejected.svg", 20px);
			}

			&.requested {
				@include icon("~@/assets/images/status-requested.svg", 20px);
			}
		}

		.date {
			font-size: $global-small-font-size;
			color: $grey;
		}
	}
}
</style>
