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
	comment: string,
	lastModified: string | null
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
					comment: task.serviceRequest.details,
					lastModified: task.lastModified
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
		<div class="table-wrapper">
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
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.title {
	font-weight: $global-font-weight-medium;
}

.table-wrapper {
	background-color: $global-background;
	border-radius: 5px;
	box-shadow: 0 2px 5px rgba(51, 51, 51, 0.25);
	padding: 10px 20px;
}

.el-table {
	&::before {
		background-color: $global-background;
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

	::v-deep(.el-table__body) {
		border-spacing: 0 10px;

		th,
		td {
			padding: 7px 0;
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
			color: $grey
		}
	}
}
</style>
