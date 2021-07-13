<script lang="ts">
import { defineComponent, ref, reactive } from "vue";
import { Coding } from "@/types";
import { RuleItem } from "async-validator";
import { getCategories, getConditionCodes } from "@/api";
import { ProblemsModule } from "@/store/modules/problems";
import moment from "moment";
import { DEFAULT_BASED_ON_TEXT, DEFAULT_REQUIRED_FORM_RULE } from "@/utils/constants";

export type FormModel = {
	name: string,
	category: string,
	icdCode: string,
	snomedCode: string,
	basedOnText: string,
	startDate: string
};

export default defineComponent({
	name: "NewProblemDialog",
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	emits: ["close"],
	setup(props, { emit }) {
		const formEl = ref<HTMLFormElement>();
		const formModel = reactive<FormModel>({
			name: "",
			category: "",
			icdCode: "",
			snomedCode: "",
			basedOnText: DEFAULT_BASED_ON_TEXT,
			startDate: new Date().toDateString()
		});
		const categoryOptions = ref<Coding[]>([]);
		const icdCodesOptions = ref<Coding[]>([]);
		const snomedCodesOptions = ref<Coding[]>([]);

		//
		// Clear code fields on every category change because they are connected.
		//
		const onCategoryChange = async (category: string) => {
			formModel.icdCode = "";
			formModel.snomedCode = "";

			const conditionCodes = await getConditionCodes(category);
			icdCodesOptions.value = conditionCodes.find(item => item.display === "ICD-10-CM")?.codings || [];
			snomedCodesOptions.value = conditionCodes.find(item => item.display === "SNOMED CT")?.codings || [];
		};

		const onDialogClose = () => {
			formEl.value?.resetFields();
			emit("close");
		};

		//
		// On dialog open fetch all options for dropdowns and reset previous edits.
		//
		const onDialogOpen = async () => {
			categoryOptions.value = await getCategories();
			formModel.startDate = new Date().toDateString();
			formModel.basedOnText = DEFAULT_BASED_ON_TEXT;
		};

		const formRules: { [field: string]: RuleItem & { trigger?: string } } = {
			name: DEFAULT_REQUIRED_FORM_RULE,
			category: DEFAULT_REQUIRED_FORM_RULE,
			icdCode: DEFAULT_REQUIRED_FORM_RULE,
			snomedCode: DEFAULT_REQUIRED_FORM_RULE,
			basedOnText: DEFAULT_REQUIRED_FORM_RULE
		};

		const saveInProgress = ref<boolean>(false);

		//
		// On save button click handler. Validate form, if everything is ok save it and close dialog.
		//
		const onFormSave = () => {
			formEl.value?.validate(async (valid: boolean) => {
				if (valid) {
					saveInProgress.value = true;
					const payload = { ...formModel };
					payload.startDate = formModel.startDate ? moment(formModel.startDate).format("YYYY-MM-DD") : "";

					try {
						await ProblemsModule.createProblem(payload);
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
			icdCodesOptions,
			snomedCodesOptions
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="New Problem"
		:width="700"
		append-to-body
		destroy-on-close
		custom-class="problem-dialog"
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
			class="new-problem-form"
		>
			<el-form-item
				label="Problem"
				prop="name"
			>
				<el-input
					v-model="formModel.name"
					placeholder="Enter problem name"
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
				label="ICD-10 Code"
				prop="icdCode"
			>
				<el-select
					v-model="formModel.icdCode"
					placeholder="Select Code"
				>
					<el-option
						v-for="item in icdCodesOptions"
						:key="item.code"
						:label="`${item.display} (${item.code})`"
						:value="item.code"
					/>
				</el-select>
			</el-form-item>
			<el-form-item
				label="SNOMED-CT Code"
				prop="snomedCode"
			>
				<el-select
					v-model="formModel.snomedCode"
					placeholder="Select Code"
				>
					<el-option
						v-for="item in snomedCodesOptions"
						:key="item.code"
						:label="`${item.display} (${item.code})`"
						:value="item.code"
					/>
				</el-select>
			</el-form-item>
			<el-form-item
				label="Based on"
				prop="basedOnText"
			>
				<el-input
					v-model="formModel.basedOnText"
				/>
			</el-form-item>

			<el-form-item
				label="Start Date"
			>
				<el-date-picker
					v-model="formModel.startDate"
					type="date"
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
				Add problem
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.new-problem-form {
	.el-select {
		width: 100%;
	}

	::v-deep(.el-date-editor.el-input) {
		width: 125px;
	}
}
</style>
