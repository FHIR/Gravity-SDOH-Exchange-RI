<script lang="ts">
import { defineComponent, ref, reactive } from "vue";
import { Coding } from "@/types";
import { RuleItem } from "async-validator";
import { getCategories, getConditionCodes } from "@/api";
import { ProblemsModule } from "@/store/modules/problems";

const DEFAULT_REQUIRED_RULE = {
	required: true,
	message: "This field is required"
};

export type FormModel = {
	name: string,
	category: string,
	codeICD: string,
	codeSNOMED: string
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
			codeICD: "",
			codeSNOMED: ""
		});
		const categoryOptions = ref<Coding[]>([]);
		const icdCodesOptions = ref<Coding[]>([]);
		const snomedCodesOptions = ref<Coding[]>([]);

		//
		// Clear code fields on every category change because they are connected.
		//
		const onCategoryChange = async (category: string) => {
			formModel.codeICD = "";
			formModel.codeSNOMED = "";

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
		};

		const formRules: { [field: string]: RuleItem & { trigger?: string } } = {
			name: DEFAULT_REQUIRED_RULE,
			category: DEFAULT_REQUIRED_RULE,
			codeICD: DEFAULT_REQUIRED_RULE,
			codeSNOMED: DEFAULT_REQUIRED_RULE
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
		title="Add Problem"
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
				prop="codeICD"
			>
				<el-select
					v-model="formModel.codeICD"
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
				prop="codeSNOMED"
			>
				<el-select
					v-model="formModel.codeSNOMED"
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
			>
				<el-input
					model-value="Conversation with Patient"
					disabled
				/>
			</el-form-item>

			<el-form-item
				label="Creation Date"
			>
				<el-date-picker
					:model-value="new Date()"
					type="date"
					disabled
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
