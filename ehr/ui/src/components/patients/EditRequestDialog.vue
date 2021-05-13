<script lang="ts">
import { defineComponent, PropType, ref, reactive } from "vue";
import { TableData } from "@/components/patients/ActionSteps.vue";
import { Occurrence, TaskStatus, updateTaskPayload } from "@/types";
import moment from "moment";
import { TasksModule } from "@/store/modules/tasks";

export type FormModel = {
	status: string,
	comment: string
};


export default defineComponent({
	name: "EditRequestDialog",
	props: {
		visible: {
			type: Boolean,
			default: false
		},
		task: {
			type: Object as PropType<TableData>,
			default: undefined
		}
	},
	emits: ["close"],
	setup(props, { emit }) {
		const saveInProgress = ref<boolean>(false);
		const formModel = reactive<FormModel>({
			status: "",
			comment: ""
		});

		const onDialogOpen = () => {
			Object.assign(formModel, { status: props.task.status });
		};

		const showOccurrence = (occurrence: Occurrence) => {
			if (occurrence.start !== null) {
				return `From ${moment(occurrence.start).format("MMM DD, YYYY")} to ${moment(occurrence.end).format("MMM DD, YYYY")}`;
			}
			return `Until ${moment(occurrence.end).format("MMM DD, YYYY")}`;
		};

		const getStatusOptions = (status: TaskStatus): { name: string, value: string }[] => {
			if (status === "Completed" || status === "Cancelled") {
				return [];
			}

			return [{
				name: "Cancelled",
				value: "Cancelled"
			}];
		};

		const onFormSave = async () => {
			const payload: updateTaskPayload = {
				id: props.task.id,
				comment: formModel.comment,
				status: formModel.status === props.task.status ? null : formModel.status as TaskStatus
			};
			saveInProgress.value = true;
			try {
				await TasksModule.updateTask(payload);
				emit("close");
			} finally {
				saveInProgress.value = false;
			}
		};

		return {
			saveInProgress,
			formModel,
			onDialogOpen,
			showOccurrence,
			getStatusOptions,
			onFormSave
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="Service Request/Task"
		:width="700"
		append-to-body
		destroy-on-close
		custom-class="edit-request-dialog"
		@close="$emit('close')"
		@open="onDialogOpen"
	>
		<el-form
			ref="formEl"
			:model="formModel"
			label-width="155px"
			label-position="left"
			size="mini"
			class="edit-request-form"
		>
			<el-form-item label="Request Name">
				{{ task.name }}
			</el-form-item>
			<el-form-item label="Category/Domain">
				{{ task.category.display }}
			</el-form-item>
			<el-form-item label="Request">
				{{ `${task.request.display} (${task.request.code})` }}
			</el-form-item>
			<el-form-item label="Status">
				<el-select
					v-model="formModel.status"
					placeholder="Select Status"
				>
					<template #prefix>
						<span
							class="icon"
							:class="formModel.status.toLocaleLowerCase()"
						></span>
					</template>

					<el-option
						v-for="item in getStatusOptions(task.status)"
						:key="item.value"
						:label="item.name"
						:value="item.value"
					>
						<span
							class="icon"
							:class="item.value.toLocaleLowerCase()"
						></span>
						{{ item.name }}
					</el-option>
				</el-select>
				<span class="date">{{ $filters.formatDateTime(task.lastModified) }}</span>
			</el-form-item>
			<el-form-item label="Comment">
				<el-input
					v-model="formModel.comment"
					type="textarea"
					rows="2"
					placeholder="Enter your comment here..."
				/>
			</el-form-item>

			<el-divider />

			<el-form-item label="Priority">
				{{ task.priority }}
			</el-form-item>
			<el-form-item label="Occurrence">
				{{ showOccurrence(task.occurrence) }}
			</el-form-item>
			<el-form-item label="Problem(s)">
				<div
					v-for="(item, index) in task.problems"
					:key="index"
					class="wrapper"
				>
					<span class="item">{{ item.display }}</span>
				</div>
			</el-form-item>
			<el-form-item
				label="Goal(s)"
			>
				<div
					v-for="(item, index) in task.goals"
					:key="index"
					class="wrapper"
				>
					<span class="item">{{ item.display }}</span>
				</div>
			</el-form-item>

			<el-divider />

			<el-form-item label="Performer(s)">
				{{ task.performer }}
			</el-form-item>
			<el-form-item label="Consent">
				{{ task.consent }}
			</el-form-item>

			<div
				v-if="task.outcomes || task.procedures.length > 0 || task.comments.length > 0"
				class="outcome-section"
			>
				<el-divider />

				<el-form-item
					v-if="task.outcomes"
					label="Outcome"
				>
					{{ task.outcomes }}
				</el-form-item>
				<el-form-item
					v-if="task.procedures.length > 0"
					label="Procedure(s)"
				>
					<div
						v-for="(item, index) in task.procedures"
						:key="index"
						class="wrapper"
					>
						<span class="item">{{ item.display }}</span>
					</div>
				</el-form-item>
				<el-form-item
					v-if="task.comments.length > 0"
					label="Comment(s)"
				>
					<div
						v-for="(item, index) in task.comments"
						:key="index"
						class="wrapper"
					>
						{{ item.text }}
					</div>
				</el-form-item>
			</div>
		</el-form>
		<template #footer>
			<el-button
				round
				size="mini"
				@click="$emit('close')"
			>
				Cancel
			</el-button>
			<el-button
				plain
				round
				type="primary"
				size="mini"
				:loading="saveInProgress"
				@click="onFormSave"
			>
				Save Changes
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.edit-request-form {
	.el-divider {
		margin: 20px 0;
	}
}

.wrapper {
	line-height: 15px;
	margin-top: 5px;
	margin-bottom: 10px;

	&:last-child {
		margin-bottom: 0;
	}
}

.item {
	background-color: $alice-blue;
	border-radius: 5px;
	padding: 0 5px;

	@include dont-break-out();
}

.date {
	margin-left: 10px;
}

//todo: extract to separate icon component, reuse in table also
.icon {
	margin-right: 5px;

	&.completed {
		@include icon("~@/assets/images/status-completed.svg", 14px);
	}

	&.accepted {
		@include icon("~@/assets/images/status-accepted.svg", 14px);
	}

	&.cancelled {
		@include icon("~@/assets/images/status-cancelled.svg", 14px);
	}

	&.failed {
		@include icon("~@/assets/images/status-failed.svg", 14px);
	}

	&.inprogress {
		@include icon("~@/assets/images/status-in-progress.svg", 14px);
	}

	&.onhold {
		@include icon("~@/assets/images/status-on-hold.svg", 14px);
	}

	&.received {
		@include icon("~@/assets/images/status-received.svg", 14px);
	}

	&.rejected {
		@include icon("~@/assets/images/status-rejected.svg", 14px);
	}

	&.requested {
		@include icon("~@/assets/images/status-requested.svg", 14px);
	}
}
</style>
