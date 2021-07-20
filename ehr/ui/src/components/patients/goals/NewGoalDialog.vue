<script lang="ts">
import { defineComponent, ref, reactive, computed, PropType } from "vue";
import { Coding, Problem } from "@/types";
import { RuleItem } from "async-validator";
import { getCategories, getGoalCodes } from "@/api";
import moment from "moment";
import { GoalsModule } from "@/store/modules/goals";
import { ProblemsModule } from "@/store/modules/problems";
import { ACHIEVEMENT_STATUSES } from "@/components/patients/goals/Goals.vue";
import { showDefaultNotification } from "@/utils/utils";

const DEFAULT_REQUIRED_RULE = {
	required: true,
	message: "This field is required"
};

export type FormModel = {
	achievementStatus: string,
	name: string,
	category: string,
	snomedCode: string,
	problemIds: string[],
	startDate: string,
	comment: string
};

const DEFAULT_FORM_MODEL = {
	achievementStatus: "",
	name: "",
	category: "",
	snomedCode: "",
	problemIds: [],
	startDate: "",
	comment: ""
};

export default defineComponent({
	name: "NewGoalDialog",
	props: {
		visible: {
			type: Boolean,
			default: false
		},
		newGoalsProblems: {
			type: Array as PropType<string[]>,
			default: () => []
		}
	},
	emits: ["close"],
	setup(props, { emit }) {
		const formEl = ref<HTMLFormElement>();
		const formModel = reactive<FormModel>(DEFAULT_FORM_MODEL);
		const categoryOptions = ref<Coding[]>([]);
		const codeOptions = ref<Coding[]>([]);
		const problems = computed<Problem[]>(() => ProblemsModule.activeProblems);
		const saveInProgress = ref<boolean>(false);
		const formRules: { [field: string]: RuleItem & { trigger?: string } } = {
			name: DEFAULT_REQUIRED_RULE,
			category: DEFAULT_REQUIRED_RULE,
			snomedCode: DEFAULT_REQUIRED_RULE
		};

		//
		// Clear code fields on every category change because they are connected.
		//
		const onCategoryChange = async (code: string) => {
			formModel.snomedCode = "";

			const codes = await getGoalCodes(code);
			codeOptions.value = codes[0].codings;
		};

		const onDialogClose = () => {
			formEl.value?.resetFields();
			emit("close");
		};

		//
		// On dialog open fetch all options for dropdowns, set start date.
		//
		const onDialogOpen = async () => {
			formModel.startDate = new Date().toDateString();
			categoryOptions.value = await getCategories();
			await ProblemsModule.getActiveProblems();

			formModel.problemIds = props.newGoalsProblems;
		};


		//
		// On save button click handler. Validate form, if everything is ok save it and close dialog.
		//
		const onFormSave = () => {
			formEl.value?.validate(async (valid: boolean) => {
				if (valid) {
					saveInProgress.value = true;
					const payload = { ...formModel };

					payload.startDate = moment(formModel.startDate).format("YYYY-MM-DDTHH:mm");

					try {
						await GoalsModule.createGoal(payload);
						showDefaultNotification("Goal was added.");
						emit("close");
					} finally {
						saveInProgress.value = false;
					}
				}
			});
		};

		return {
			formModel,
			formRules,
			onDialogClose,
			onDialogOpen,
			onFormSave,
			saveInProgress,
			formEl,
			categoryOptions,
			onCategoryChange,
			codeOptions,
			problems,
			ACHIEVEMENT_STATUSES
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="Add Goal"
		:width="700"
		destroy-on-close
		custom-class="goal-dialog"
		@close="onDialogClose"
		@open="onDialogOpen"
	>
		<el-form
			ref="formEl"
			:model="formModel"
			:rules="formRules"
			label-width="155px"
			label-position="left"
			size="mini"
			class="new-goal-form"
		>
			<el-form-item
				label="Name"
				prop="name"
			>
				<el-input
					v-model="formModel.name"
					placeholder="Enter Goal Name"
				/>
			</el-form-item>
			<el-form-item
				label="Category"
				prop="category"
			>
				<el-select
					v-model="formModel.category"
					placeholder="Select Category"
					@change="onCategoryChange($event)"
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
				prop="snomedCode"
			>
				<el-select
					v-model="formModel.snomedCode"
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
				label="Achievement Status"
				prop="achievementStatus"
			>
				<el-select
					v-model="formModel.achievementStatus"
					placeholder="Select Status"
					class="achievement-status"
				>
					<el-option
						v-for="item in ACHIEVEMENT_STATUSES"
						:key="item.code"
						:label="item.display"
						:value="item.code"
					/>
				</el-select>
			</el-form-item>
			<el-form-item
				prop="problemIds"
				label="Problem(s)"
			>
				<el-select
					v-model="formModel.problemIds"
					multiple
					placeholder="Select Problem(s)"
				>
					<el-option
						v-for="item in problems"
						:key="item.id"
						:label="item.name"
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
					type="date"
				/>
			</el-form-item>

			<el-form-item
				prop="comments"
				label="Comment"
			>
				<el-input
					v-model="formModel.comment"
					type="textarea"
					placeholder="Enter your comment here"
				/>
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
				@click="onFormSave"
			>
				Add goal
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.new-goal-form {
	.el-select:not(.achievement-status) {
		width: 100%;
	}
}
</style>
