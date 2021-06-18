<script lang="ts">
import { defineComponent, PropType, ref, reactive, computed, toRefs } from "vue";
import { TableData } from "@/components/patients/goals/Goals.vue";
import { Coding, ServiceRequestCondition, UpdateGoalPayload } from "@/types";
import { RuleItem } from "async-validator";
import { getCategories, getRequests, getServiceRequestConditions } from "@/api";
import { GoalsModule } from "@/store/modules/goals";
import _ from "@/vendors/lodash";

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
		}
	},
	emits: ["close"],
	setup(props, { emit }) {
		const categoryOptions = ref<Coding[]>([]);
		const codeOptions = ref<Coding[]>([]);
		const problemOptions = ref<ServiceRequestCondition[]>([]);
		const { goal } = toRefs(props);
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

		//todo: seems like element-ui added few additional keys to async-validator RuleItem
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

		const onDialogOpen = async () => {
			Object.assign(formModel, {
				category: goal.value.category.code,
				code: goal.value.code.code,
				name: goal.value.name,
				problems: [ ...goal.value.problems ],
				startDate: goal.value.startDate,
				addedBy: goal.value.addedBy
			});

			categoryOptions.value = await getCategories();
			codeOptions.value = await getRequests(goal.value.category.code);
			problemOptions.value = await getServiceRequestConditions();
		};

		const onDialogClose = () => {
			formEl.value?.resetFields();
			emit("close");
		};

		const hasChanges = computed<boolean>(() =>
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
		const saveInProgress = ref<boolean>(false);
		const onFormSave = async () => {
			const payload: UpdateGoalPayload = {
				id: goal.value.id,
				...formModel
			};
			saveInProgress.value = true;
			try {
				await GoalsModule.updateGoal(payload);
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
			hasChanges,
			saveInProgress,
			onFormSave
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
			<el-button
				round
				size="mini"
				@click="$emit('close')"
			>
				Close
			</el-button>

			<el-button
				plain
				round
				type="primary"
				size="mini"
				:loading="saveInProgress"
				:disabled="!hasChanges"
				@click="onFormSave"
			>
				Save Changes
			</el-button>
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
</style>
