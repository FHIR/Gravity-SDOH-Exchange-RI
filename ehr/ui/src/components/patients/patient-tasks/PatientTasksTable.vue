<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { TableData } from "@/components/patients/patient-tasks/PatientTasks.vue";
import TaskStatusIcon from "@/components/patients/TaskStatusIcon.vue";
import TaskDialog from "@/components/patients/patient-tasks/TaskDialog.vue";

export default defineComponent({
	components: {
		TaskStatusIcon,
		TaskDialog
	},
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
	emits: ["add-task", "trigger-open-assessment"],
	setup(props, { emit }) {
		const title = ref<string>(props.status === "active" ? "Active Tasks" : "Completed Tasks");
		const taskDialogVisible = ref<boolean>(false);
		const activeTaskId = ref<string | null>(null);
		const activeTaskName = ref<string | null>(null);

		const onTaskClick = (row: TableData) => {
			activeTaskId.value = row.id;
			activeTaskName.value = row.name;
			taskDialogVisible.value = true;
		};

		const handleOpenAssessment = (id: string) => {
			taskDialogVisible.value = false;
			emit("trigger-open-assessment", id);
		};

		return {
			title,
			onTaskClick,
			taskDialogVisible,
			activeTaskId,
			handleOpenAssessment,
			activeTaskName
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

			<el-table-column label="Status">
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

			<el-table-column label="Code">
				<template #default="scope">
					{{ scope.row.code?.display || "N/A" }}
				</template>
			</el-table-column>

			<el-table-column label="Referral">
				<template #default="scope">
					{{ scope.row.referralTask?.display || "N/A" }}
				</template>
			</el-table-column>

			<el-table-column label="Assessment">
				<template #default="scope">
					{{ scope.row.assessment?.display || "N/A" }}
				</template>
			</el-table-column>

			<el-table-column label="Outcomes/Reason">
				<template #default="scope">
					{{ scope.row.statusReason || scope.row.outcomes || "N/A" }}
				</template>
			</el-table-column>
		</el-table>
		<TaskDialog
			:visible="taskDialogVisible"
			:task-id="activeTaskId"
			:task-name="activeTaskName"
			@close="taskDialogVisible = false"
			@trigger-open-assessment="handleOpenAssessment"
		/>
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
