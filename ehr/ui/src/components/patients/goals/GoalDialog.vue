<script lang="ts">
import { defineComponent, PropType, ref, reactive, computed, toRefs } from "vue";
import { TableData } from "@/components/patients/goals/Goals.vue";
import { Coding, ServiceRequestCondition, UpdateGoalPayload,GoalAsCompletedPayload } from "@/types";
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
	endDate: string,
	addedBy: string,
	comment: string
};

const DEFAULT_REQUIRED_RULE = {
	required: true,
	message: "This field is required"
};

const CONFIRM_MESSAGES = {
	"edit": "",
	"view": "",
	"remove": "Please confirm that you want to remove this goal.",
	"mark-as-completed": "Please enter goal completion date:"
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
			type: Object as PropType<TableData | undefined>,
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
		const phase = ref<GoalAction>("edit");
		const confirmMessage = computed<string>(() => CONFIRM_MESSAGES[phase.value]);
		const showConfirm = computed<boolean>(() => phase.value === "mark-as-completed" || phase.value == "remove");
		const categoryOptions = ref<Coding[]>([]);
		const codeOptions = ref<Coding[]>([]);
		const problemOptions = ref<ServiceRequestCondition[]>([]);

		const formModel = reactive<FormModel>({
			category: "",
			code: "",
			name: "",
			problems: [],
			startDate: "",
			endDate: "",
			addedBy: "",
			comment: ""
		});
		const formEl = ref<HTMLFormElement>();
		const formRules: { [field: string]: RuleItem & { trigger?: string } } = {
			name: DEFAULT_REQUIRED_RULE,
			category: DEFAULT_REQUIRED_RULE,
			code: DEFAULT_REQUIRED_RULE
		};
		const hasFormChanges = computed<boolean>(() =>
			(
				formModel.category !== goal.value?.category.code ||
				formModel.code !== goal.value?.snomedCode.code ||
				formModel.name !== goal.value?.name ||
				!_.isEqual(formModel.problems, goal.value?.problems)||
				formModel.startDate !== goal.value?.startDate ||
				formModel.addedBy !== goal.value?.addedBy ||
				!!formModel.comment
			)
		);
		const isFormDisabled = computed<boolean>(() => phase.value === "mark-as-completed" || phase.value === "remove");

		const onDialogOpen = async () => {
			Object.assign(formModel, {
				category: goal.value?.category.code,
				code: goal.value?.snomedCode.code,
				name: goal.value?.name,
				problems: [ ...goal.value!.problems ],
				startDate: goal.value?.startDate,
				endDate: goal.value?.endDate,
				addedBy: goal.value?.addedBy
			});
			phase.value = openPhase.value;

			if (phase.value !== "view") {
				categoryOptions.value = await getCategories();
				codeOptions.value = await getRequests(goal.value!.category.code);
				problemOptions.value = await getServiceRequestConditions();
			}
		};
		const onDialogClose = () => {
			formEl.value?.resetFields();
			emit("close");
		};

		const saveInProgress = ref<boolean>(false);
		const saveGoal = async () => {
			const payload: UpdateGoalPayload = {
				id: goal.value!.id,
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
			await formEl.value!.validate();
			await saveGoal();
			emit("close");
		};

		const onActionClick = async (action: string) => {
			hasFormChanges.value && await formEl.value!.validate() && await saveGoal();

			if (action === "mark-as-completed") {
				phase.value = "mark-as-completed";
			} else if (action === "remove") {
				phase.value = "remove";
			}
		};

		const completionDate = ref<string>("");
		const onCompletionConfirmClick = async () => {
			saveInProgress.value = true;
			try {
				if (phase.value === "remove") {
					await GoalsModule.removeGoal(goal.value!.id);
				} else if (phase.value === "mark-as-completed") {
					const payload: GoalAsCompletedPayload = {
						id: goal.value!.id,
						endDate: moment(completionDate.value || new Date()).format("YYYY-MM-DD")
					};
					await GoalsModule.markGoalAsCompleted(payload);
				}
				emit("close");
			} finally {
				saveInProgress.value = false;
			}
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
			isFormDisabled,
			confirmMessage,
			showConfirm
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
		@open="onDialogOpen"
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
				label="Goal Name"
				prop="name"
			>
				<span v-if="phase === 'view'">
					{{ formModel.name }}
				</span>
				<el-input
					v-else
					v-model="formModel.name"
					placeholder="Add Goal Name"
				/>
			</el-form-item>

			<el-form-item
				label="Category"
				prop="category"
			>
				<span v-if="phase === 'view'">
					{{ formModel.category }}
				</span>
				<el-select
					v-else
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
				<span v-if="phase === 'view'">
					{{ formModel.code }}
				</span>
				<el-select
					v-else
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
				label="Problem(s)"
				prop="problems"
			>
				<div v-if="phase === 'view'">
					<div
						v-for="(item, index) in formModel.problems"
						:key="index"
					>
						<span class="problem">{{ item }}</span>
					</div>
				</div>
				<el-select
					v-else
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
				<span v-if="phase === 'view'">
					{{ $filters.formatDateTime(formModel.startDate) }}
				</span>
				<el-date-picker
					v-else
					v-model="formModel.startDate"
					placeholder="Select Date"
				/>
			</el-form-item>

			<el-form-item
				v-if="phase === 'view'"
				label="End Date"
				prop="endDate"
			>
				{{ $filters.formatDateTime(formModel.endDate) }}
			</el-form-item>

			<el-form-item
				label="Added By"
				prop="addedBy"
			>
				<span v-if="phase === 'view'">
					{{ formModel.addedBy.display }}
				</span>
				<el-input
					v-else
					v-model="formModel.addedBy.display"
					placeholder="Add Author"
				/>
			</el-form-item>

			<el-form-item
				v-if="phase !== 'view'"
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

				<el-form-item
					label="Comment(s)"
					prop="comment"
				>
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
			<div
				v-if="showConfirm"
				class="confirm-message"
			>
				<div class="completion-date">
					<span>{{ confirmMessage }}</span>
					<el-date-picker
						v-if="phase === 'mark-as-completed'"
						v-model="completionDate"
						placeholder="Select Date"
						size="mini"
					/>
				</div>

				<el-button
					v-if="showConfirm"
					plain
					round
					type="primary"
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
					:loading="saveInProgress"
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
					v-if="!showConfirm"
					label="Save Changes"
					:items="[{ id: 'mark-as-completed', label: 'Mark as Completed', iconSrc: require('@/assets/images/goal-mark-as-completed.svg') },
						{ id: 'remove', label: 'Remove', iconSrc: require('@/assets/images/goal-remove.svg') }]"
					:disabled="!hasFormChanges"
					@click="onSaveChangesClick"
					@item-click="onActionClick"
				/>
			</div>

			<div v-if="phase === 'view'">
				<el-button
					plain
					round
					type="primary"
					size="mini"
					@click="$emit('close')"
				>
					Close
				</el-button>
			</div>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

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

	//todo: create small `tag` component for displaying goals/problems/procedures with blue bg
	.problem {
		background-color: $alice-blue;
		border-radius: 5px;
		padding: 0 5px;
		font-size: $global-small-font-size;

		@include dont-break-out();
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
