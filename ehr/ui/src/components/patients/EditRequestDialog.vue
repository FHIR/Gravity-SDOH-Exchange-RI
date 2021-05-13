<script lang="ts">
import { defineComponent, PropType, ref, reactive } from "vue";
import { TableData } from "@/components/patients/ActionSteps.vue";
import { Occurrence } from "@/types";
import moment from "moment";

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
	setup(props) {
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

		return {
			saveInProgress,
			formModel,
			onDialogOpen,
			showOccurrence
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
				/>
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
				{{ task.procedures }}
			</el-form-item>
			<el-form-item label="Comment">
				{{ task.comments.reduce((acc, comment) => acc += `\t${comment.text}`, "") }}
			</el-form-item>
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
			>
				Save Changes
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.edit-request-form {
	.el-divider {
		margin: 20px 0;
	}
}

.item {
	background-color: $alice-blue;
	border-radius: 5px;
	padding: 0 7px 0 5px;
}
</style>
