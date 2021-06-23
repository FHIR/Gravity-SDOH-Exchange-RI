<script lang="ts">
import { defineComponent, PropType, ref, reactive, computed, toRefs } from "vue";
import { TableData } from "@/components/patients/goals/Goals.vue";
import { Coding, ServiceRequestCondition, UpdateGoalPayload } from "@/types";
import { RuleItem } from "async-validator";
import { getCategories, getRequests, getServiceRequestConditions } from "@/api";
import { GoalsModule } from "@/store/modules/goals";
import _ from "@/vendors/lodash";
import moment from "moment";
import { GoalAction } from "@/components/patients/goals/GoalsTable.vue";
import DropButton from "@/components/DropButton.vue";

export type FormModel = {
	category: string,
	code: string,
	name: string,
	problems: string[],
	startDate: string,
	addedBy: string,
	comment: string
};

export default defineComponent({
	name: "GoalDialog",
	components: {
		DropButton
	},
	props: {
		visible: {
			type: Boolean,
			default: false
		},
		goal: {
			type: Object as PropType<TableData>,
			default: undefined
		},
		openPhase: {
			type: String as PropType<GoalAction>,
			default: "edit"
		}
	},
	emits: ["close"],
	setup(props, { emit }) {
		const { goal, openPhase } = toRefs(props);
		const categoryOptions = ref<Coding[]>([]);
		const codeOptions = ref<Coding[]>([]);
		const problemOptions = ref<ServiceRequestCondition[]>([]);

		const formModel = reactive<FormModel>({
			category: "",
			code: "",
			name: "",
			problems: [],
			startDate: "",
			addedBy: "",
			comment: ""
		});
		const formEl = ref<HTMLFormElement>();
		const formRules: { [field: string]: RuleItem & { trigger?: string } } = {
			category: {
				required: true,
				message: "This field is required"
			},
			code: {
				required: true,
				message: "This field is required"
			}
		};
		const hasFormChanges = computed<boolean>(() =>
			(
				formModel.category !== goal.value?.category.code ||
				formModel.code !== goal.value?.code.code ||
				formModel.name !== goal.value?.name ||
				!_.isEqual(formModel.problems, goal.value?.problems)||
				formModel.startDate !== goal.value?.startDate ||
				formModel.addedBy !== goal.value?.addedBy ||
				!!formModel.comment
			)
		);
		const isFormDisabled = computed<boolean>(() => phase.value === "mark-as-completed");

		const onDialogOpen = async () => {
			Object.assign(formModel, {
				category: goal.value.category.code,
				code: goal.value.code.code,
				name: goal.value.name,
				problems: [ ...goal.value.problems ],
				startDate: goal.value.startDate,
				addedBy: goal.value.addedBy
			});
			phase.value = openPhase.value;

			categoryOptions.value = await getCategories();
			codeOptions.value = await getRequests(goal.value.category.code);
			problemOptions.value = await getServiceRequestConditions();
		};
		const onDialogClose = () => {
			formEl.value?.resetFields();
			emit("close");
		};

		const saveInProgress = ref<boolean>(false);
		const saveGoal = async () => {
			const payload: UpdateGoalPayload = {
				id: goal.value.id,
				...formModel
			};
			saveInProgress.value = true;
			try {
				await GoalsModule.updateGoal(payload);
			} finally {
				saveInProgress.value = false;
			}
		};
		const onSaveChangesClick = async () => {
			await saveGoal();
			emit("close");
		};

		const phase = ref<GoalAction>("edit");
		const onActionClick = async (action: string) => {
			hasFormChanges.value && await saveGoal();

			if (action === "mark-as-completed") {
				phase.value = "mark-as-completed";
			}
		};

		const completionDate = ref<string>("");
		const markGoalAsCompleted = async () => {
			const payload: UpdateGoalPayload = {
				id: goal.value.id,
				status: "completed",
				endDate: moment(completionDate.value || new Date()).format("YYYY-MM-DD[T]HH:mm:ss")
			};
			saveInProgress.value = true;
			try {
				await GoalsModule.updateGoal(payload);
			} finally {
				saveInProgress.value = false;
			}
		};
		const onCompletionConfirmClick = async () => {
			await markGoalAsCompleted();
			emit("close");
		};

		return {
			formModel,
			onDialogOpen,
			onDialogClose,
			formEl,
			formRules,
			categoryOptions,
			codeOptions,
			problemOptions,
			hasFormChanges,
			saveInProgress,
			onSaveChangesClick,
			onActionClick,
			phase,
			completionDate,
			onCompletionConfirmClick,
			isFormDisabled
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="Goal Details"
		:width="700"
		append-to-body
		destroy-on-close
		custom-class="goal-dialog"
		@close="onDialogClose"
		@opened="onDialogOpen"
	>
		<el-form
			ref="formEl"
			:model="formModel"
			:rules="formRules"
			:disabled="isFormDisabled"
			label-width="155px"
			label-position="left"
			size="mini"
			class="goal-form"
		>
			<el-form-item
				label="Category"
				prop="category"
			>
				<el-select
					v-model="formModel.category"
					placeholder="Select Category"
				>
					<el-option
						v-for="item in categoryOptions"
						:key="item.code"
						:label="item.display"
						:value="item.code"
					/>
				</el-select>
			</el-form-item>

			<el-form-item
				label="Code"
				prop="code"
			>
				<el-select
					v-model="formModel.code"
					filterable
					placeholder="Select Code"
				>
					<el-option
						v-for="item in codeOptions"
						:key="item.code"
						:label="`${item.display} (${item.code})`"
						:value="item.code"
					/>
				</el-select>
			</el-form-item>

			<el-form-item
				label="Goal Name"
				prop="name"
			>
				<el-input
					v-model="formModel.name"
					placeholder="Add Goal Name"
				/>
			</el-form-item>

			<el-form-item
				label="Problem(s)"
				prop="problems"
			>
				<el-select
					v-model="formModel.problems"
					multiple
					placeholder="Select Problem(s)"
				>
					<el-option
						v-for="item in problemOptions"
						:key="item.id"
						:label="item.display"
						:value="item.id"
					/>
				</el-select>
			</el-form-item>

			<el-form-item
				label="Start Date"
				prop="startDate"
			>
				<el-date-picker
					v-model="formModel.startDate"
					placeholder="Select Date"
				/>
			</el-form-item>

			<el-form-item
				label="Added By"
				prop="addedBy"
			>
				<el-input
					v-model="formModel.addedBy"
					placeholder="Add Author"
				/>
			</el-form-item>

			<el-form-item
				label="Comment"
				prop="comment"
			>
				<el-input
					v-model="formModel.comment"
					type="textarea"
					rows="2"
					placeholder="Enter your comment here..."
				/>
			</el-form-item>

			<div
				v-if="goal.comments.length > 0"
				class="outcome-section"
			>
				<el-divider />

				<el-form-item label="Comment(s)">
					<div
						v-for="(item, index) in goal.comments"
						:key="index"
						class="comment"
					>
						{{ item.text }}
					</div>
				</el-form-item>
			</div>
		</el-form>
		<template #footer>
			<div v-if="phase === 'mark-as-completed'">
				<div class="completion-date">
					<span>Please enter goal completion date:</span>
					<el-date-picker
						v-model="completionDate"
						placeholder="Select Date"
						size="mini"
					/>
				</div>

				<el-button
					round
					size="mini"
					@click="phase = 'edit'"
				>
					Cancel
				</el-button>

				<el-button
					plain
					round
					type="primary"
					size="mini"
					@click="onCompletionConfirmClick"
				>
					Confirm
				</el-button>
			</div>

			<div v-if="phase === 'edit'">
				<el-button
					round
					size="mini"
					@click="$emit('close')"
				>
					Cancel
				</el-button>

				<DropButton
					label="Save Changes"
					:items="[{ id: 'mark-as-completed', label: 'Mark as Completed', iconSrc: require('@/assets/images/goal-mark-as-completed.svg') }]"
					:disabled="!hasFormChanges"
					@click="onSaveChangesClick"
					@item-click="onActionClick"
				/>
			</div>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.goal-form {
	.el-select {
		width: 100%;
	}

	.el-divider {
		margin: 20px 0;
	}

	.comment {
		line-height: 15px;
		margin-top: 5px;
		margin-bottom: 10px;

		&:last-child {
			margin-bottom: 0;
		}
	}
}

.completion-date {
	text-align: left;
	margin-bottom: 20px;

	span {
		color: $global-text-color;
		font-size: $global-font-size;
		font-weight: $global-font-weight-medium;
		margin-right: 10px;
	}
}
</style>
