<script lang="ts">
import { defineComponent, ref, reactive } from "vue";
import { Coding } from "@/types";
import { RuleItem } from "async-validator";
import { getCategories, getProblemCodes } from "@/api";
import moment from "moment";
import { ProblemsModule } from "@/store/modules/problems";

const DEFAULT_REQUIRED_RULE = {
	required: true,
	message: "This field is required"
};

export type FormModel = {
	name: string,
	category: string,
	codeISD: string,
	codeSNOMED: string,
	basedOn: string,
	startDate: string
};

const DEFAULT_FORM_MODEL = {
	name: "",
	category: "",
	codeISD: "",
	codeSNOMED: "",
	basedOn: "Conversation with Patient",
	startDate: ""
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
		const formModel = reactive<FormModel>(DEFAULT_FORM_MODEL);
		const categoryOptions = ref<Coding[]>([]);
		const codeISDOptions = ref<Coding[]>([]);
		const codeSNOMEDOptions = ref<Coding[]>([]);

		//
		// Clear code fields on every category change because they are connected.
		//
		const onCategoryChange = async (code: string) => {
			formModel.codeISD = "";
			formModel.codeSNOMED = "";

			const codes = await getProblemCodes(code);
			codeISDOptions.value = codes.isd;
			codeSNOMEDOptions.value = codes.snomed;
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
			codeISD: DEFAULT_REQUIRED_RULE,
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
					payload.startDate = moment(formModel.startDate).format("YYYY-MM-DD[T]HH:mm:ss");

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
			codeISDOptions,
			codeSNOMEDOptions
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
				prop="codeISD"
			>
				<el-select
					v-model="formModel.codeISD"
					placeholder="Select Code"
				>
					<el-option
						v-for="item in codeISDOptions"
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
						v-for="item in codeSNOMEDOptions"
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
					v-model="formModel.basedOn"
					placeholder="Enter based on here..."
					disabled
				/>
			</el-form-item>

			<el-form-item
				label="Assessment Date"
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
