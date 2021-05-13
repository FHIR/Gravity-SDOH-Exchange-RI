<script lang="ts">
import { computed, defineComponent, onMounted, onUnmounted, ref } from "vue";
import RequestTable from "@/components/patients/RequestTable.vue";
import NewRequestDialog from "@/components/patients/NewRequestDialog.vue";
import { Comment, Occurrence, Task, ServiceRequestCondition, ServiceRequestGoal } from "@/types";
import { TasksModule } from "@/store/modules/tasks";

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
	name: "ActionSteps",
	components: {
		RequestTable,
		NewRequestDialog
	},
	setup() {
		const activeGroup = ref<string>("referrals");
		const newRequestDialogVisible = ref<boolean>(false);
		const isRequestLoading = ref<boolean>(false);
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
					request: task.serviceRequest.code,
					priority: task.priority,
					occurrence: task.serviceRequest.occurrence,
					//todo: no api
					procedures: []
				});
			});

			return res;
		});
		const activeRequests = computed<TableData[]>(() => tableData.value.filter(t => t.status !== "COMPLETED"));
		const completedRequests = computed<TableData[]>(() => tableData.value.filter(t => t.status === "COMPLETED"));

		onMounted(async () => {
			isRequestLoading.value = true;
			try {
				await TasksModule.getTasks();
				pollData();
			} finally {
				isRequestLoading.value = false;
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

		return {
			activeGroup,
			newRequestDialogVisible,
			activeRequests,
			completedRequests,
			isRequestLoading
		};
	}
});
</script>

<template>
	<div class="action-steps">
		<div class="action-steps-switcher">
			<el-radio-group
				v-model="activeGroup"
				size="mini"
			>
				<el-radio-button label="interventions">
					Interventions
				</el-radio-button>
				<el-radio-button label="referrals">
					Referrals
				</el-radio-button>
			</el-radio-group>
			<el-button
				v-if="activeGroup === 'referrals' && (activeRequests.length || completedRequests.length)"
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
			v-if="activeGroup === 'interventions'"
			class="interventions"
		>
			Interventions
		</div>
		<div
			v-else
			v-loading="isRequestLoading"
			class="referrals"
		>
			<RequestTable
				v-if="activeRequests.length"
				:data="activeRequests"
			/>
			<RequestTable
				v-if="completedRequests.length"
				:data="completedRequests"
				title="Completed Requests"
			/>
			<div
				v-if="!isRequestLoading && !(completedRequests.length || activeRequests.length)"
				class="no-request-data"
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
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.action-steps-switcher {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20px;
}

.referrals {
	min-height: 130px;
}

.no-request-data {
	height: 350px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	background-color: $global-background;

	h2 {
		color: $whisper;
		font-size: $global-xxxlarge-font-size;
		font-weight: $global-font-weight-normal;
		margin-bottom: 50px;
	}
}
</style>
