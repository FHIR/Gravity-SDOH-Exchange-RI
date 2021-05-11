<script lang="ts">
import { defineComponent, computed, onMounted, ref, onUnmounted } from "vue";
import { TasksModule } from "@/store/modules/tasks";
import { Comment, Task, Occurrence, ServiceRequestGoal, ServiceRequestCondition } from "@/types";
import NewRequestDialog from "@/components/patients/NewRequestDialog.vue";
import EditRequestDialog from "@/components/patients/EditRequestDialog.vue";

export type TableData = {
	name: string,
	status: string,
	category: string,
	problems: ServiceRequestCondition[],
	goals: ServiceRequestGoal[],
	performer: string | null | undefined,
	consent: string
	outcomes: string | null,
	comments: Comment[],
	lastModified: string | null,
	request: string,
	priority: string | null,
	occurrence: Occurrence,
	procedures: string[]
}

export default defineComponent({
	name: "RequestTable",
	components: {
		NewRequestDialog,
		EditRequestDialog
	},
	setup() {
		const isLoading = ref<boolean>(false);
		const newRequestDialogVisible = ref<boolean>(false);
		const editRequestDialogVisible = ref<boolean>(false);

		const tasks = computed<Task[] | null>(() => TasksModule.tasks);
		const tableData = computed<TableData[]>(() => {
			const res: TableData[] = [];

			tasks.value && tasks.value.forEach((task: Task) => {
				res.push({
					name: task.name,
					status: task.status,
					category: task.serviceRequest.category,
					problems: task.serviceRequest.conditions,
					goals: task.serviceRequest.goals,
					performer: task.organization?.name,
					consent: task.serviceRequest.consent.display,
					outcomes: task.outcome,
					comments: task.comments,
					lastModified: task.lastModified,
					request: task.serviceRequest.request,
					priority: task.priority,
					occurrence: task.serviceRequest.occurrence,
					procedures: []
				});
			});

			return res;
		});

		onMounted(async () => {
			isLoading.value = true;
			try {
				await TasksModule.getTasks();
				pollData();
			} finally {
				isLoading.value = false;
			}
		});
		const pollId = ref<number>();
		const pollData = async () => {
			try {
				await TasksModule.getTasks();
			} finally {
				// todo: should we continue polling if request failed? adjust polling time later to be in sync with BE
				pollId.value = window.setTimeout(pollData, 15000);
			}
		};
		onUnmounted(() => {
			clearTimeout(pollId.value);
		});
		const editRequest = ref<TableData>();
		const onRequestClick = (row: TableData) => {
			editRequestDialogVisible.value = true;
			editRequest.value = row;
		};

		return {
			tableData,
			isLoading,
			newRequestDialogVisible,
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
				Request Table
			</h4>
			<el-button
				plain
				round
				type="primary"
				size="mini"
				@click="newRequestDialogVisible = true"
			>
				Add New Request
			</el-button>
		</div>
		<div
			v-loading="isLoading"
			class="table-wrapper"
		>
			<el-table
				v-if="!isLoading && tableData.length > 0"
				:data="tableData"
			>
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
								<span class="date">{{ scope.row.lastModified }}</span>
							</div>
						</div>
					</template>
				</el-table-column>
				<el-table-column
					prop="category"
					label="Category"
				/>
				<el-table-column
					label="Problem(s)"
				>
					<template #default="scope">
						<div
							v-for="(item, index) in scope.row.problems"
							:key="index"
							class="truncate"
						>
							{{ item.display }}
						</div>
					</template>
				</el-table-column>
				<el-table-column
					label="Goal(s)"
				>
					<template #default="scope">
						<div
							v-for="(item, index) in scope.row.goals"
							:key="index"
							class="truncate"
						>
							{{ item.display }}
						</div>
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
						{{ scope.row.comments.length > 0 ? `${scope.row.comments.length} comment${scope.row.comments.length > 1 ? 's' : ''}` : "--" }}
					</template>
				</el-table-column>
			</el-table>
			<div
				v-if="!isLoading && !tableData.length"
				class="no-data"
			>
				<h2>No Referral Requests Yet</h2>
				<el-button
					plain
					round
					type="primary"
					size="mini"
					@click="newRequestDialogVisible = true"
				>
					Add New Request
				</el-button>
			</div>
		</div>

		<NewRequestDialog
			:visible="newRequestDialogVisible"
			@close="newRequestDialogVisible = false"
		/>
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
	display: flex;
	justify-content: space-between;
	align-items: center;
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

.no-data {
	height: 350px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;

	h2 {
		color: $whisper;
		font-size: $global-xxxlarge-font-size;
		font-weight: $global-font-weight-normal;
		margin-bottom: 50px;
	}
}

.truncate {
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	padding: 5px 0;
}
</style>
