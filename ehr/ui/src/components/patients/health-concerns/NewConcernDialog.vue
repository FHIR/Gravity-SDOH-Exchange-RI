<script lang="ts">
import { defineComponent, reactive, ref } from "vue";
import { getCategories, getConditionCodes } from "@/api";
import { Coding } from "@/types";
import { RuleItem } from "async-validator";
import { ConcernsModule } from "@/store/modules/concerns";
import { DEFAULT_BASED_ON_TEXT, DEFAULT_REQUIRED_FORM_RULE } from "@/utils/constants";

export type FormModel = {
	name: string,
	category: string,
	icdCode: string,
	snomedCode: string,
	basedOnText: string
};

export default defineComponent({
	name: "NewConcernDialog",
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	emits: ["close"],
	setup(props, { emit }) {
		const categoryOptions = ref<Coding[]>([]);
		const icdCodes = ref<Coding[]>([]);
		const snomedCodes = ref<Coding[]>([]);

		const formModel = reactive<FormModel>({
			name: "",
			category: "",
			icdCode: "",
			snomedCode: "",
			basedOnText: DEFAULT_BASED_ON_TEXT
		});
		const assessmentDate = ref<string>("");

		const formEl = ref<HTMLFormElement>();

		//
		// On dialog open fetch all options for dropdowns and reset previous edits.
		//
		const onDialogOpen = async () => {
			categoryOptions.value = await getCategories();
			formModel.basedOnText = DEFAULT_BASED_ON_TEXT;
		};
		const onDialogClose = () => {
			formEl.value?.resetFields();
			emit("close");
		};

		const onCategoryChange =  async (category: string) => {
			formModel.icdCode = "";
			formModel.snomedCode = "";

			const conditionCodes = await getConditionCodes(category);
			icdCodes.value = conditionCodes.find(item => item.display === "ICD-10-CM")?.codings || [];
			snomedCodes.value = conditionCodes.find(item => item.display === "SNOMED CT")?.codings || [];
		};

		const formRules: { [field: string]: RuleItem & { trigger?: string } } = {
			name: DEFAULT_REQUIRED_FORM_RULE,
			category: DEFAULT_REQUIRED_FORM_RULE,
			icdCode: DEFAULT_REQUIRED_FORM_RULE,
			snomedCode: DEFAULT_REQUIRED_FORM_RULE,
			basedOnText: DEFAULT_REQUIRED_FORM_RULE
		};

		const saveInProgress = ref<boolean>(false);

		const onFormSave = () => {
			formEl.value?.validate(async (valid: boolean) => {
				if (valid) {
					saveInProgress.value = true;
					try {
						await ConcernsModule.createConcern(formModel);
						emit("close");
					} finally {
						saveInProgress.value = false;
					}
				}
			});
		};

		return {
			formModel,
			categoryOptions,
			icdCodes,
			snomedCodes,
			formRules,
			formEl,
			assessmentDate,
			saveInProgress,
			onDialogOpen,
			onDialogClose,
			onFormSave,
			onCategoryChange
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="New Health Concern"
		:width="700"
		destroy-on-close
		custom-class="new-concern-dialog"
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
			class="new-concern-form"
		>
			<el-form-item
				label="Health Concern"
				prop="name"
			>
				<el-input
					v-model="formModel.name"
					placeholder="Enter health concern name"
				/>
			</el-form-item>
			<el-form-item
				label="Category"
				prop="category"
			>
				<el-select
					v-model="formModel.category"
					placeholder="Select category"
					@change="onCategoryChange"
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
					placeholder="Select code"
				>
					<el-option
						v-for="item in icdCodes"
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
					placeholder="Select code"
				>
					<el-option
						v-for="item in snomedCodes"
						:key="item.code"
						:label="`${item.display}(${item.code})`"
						:value="item.code"
					/>
				</el-select>
			</el-form-item>
			<el-form-item
				label="Based on"
				prop="basedOnText"
			>
				<el-input v-model="formModel.basedOnText" />
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
				<span v-if="!saveInProgress">Add Health Concern</span>
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.new-concern-form {
	.el-select {
		width: 100%;
	}

	::v-deep(.el-date-editor.el-input) {
		width: 125px;
	}
}
</style>
