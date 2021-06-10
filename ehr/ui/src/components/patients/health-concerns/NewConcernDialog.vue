<script lang="ts">
import { defineComponent, reactive, ref } from "vue";
import { getCategories } from "@/api";
import { Condition, Coding } from "@/types";
import { RuleItem } from "async-validator";

export type FormModel = {
	name: string,
	category: string,
	icd10Code: string,
	snomedCtCode: string,
	basedOn: string,
	occurrence: string
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
		const icd10Code = ref<Coding[]>([]);
		const snomedCtCode = ref<Condition[]>([]);

		const formModel = reactive<FormModel>({
			name: "",
			category: "",
			icd10Code: "",
			snomedCtCode: "",
			basedOn: "",
			occurrence: ""
		});
		const occurrenceType = ref<string>("");
		//todo: use element-ui form type
		const formEl = ref<HTMLFormElement>();

		//
		// On dialog open fetch all options for dropdowns and reset previous edits.
		//
		const onDialogOpen = async () => {
			categoryOptions.value = await getCategories();
		};
		const onDialogClose = () => {
			formEl.value?.resetFields();
			emit("close");
		};

		//todo: seems like element-ui added few additional keys to async-validator RuleItem
		const formRules: { [field: string]: RuleItem & { trigger?: string } } = {
			name: {
				required: true,
				message: "This field is required"
			},
			category: {
				required: true,
				message: "This field is required"
			}
		};

		const saveInProgress = ref<boolean>(false);
		//
		// Disable all dates that are less than today. Used inside occurrence date-pickers.
		//
		const disabledOccurrenceDate = (time: Date): boolean => time.getTime() < Date.now();

		return {
			formModel,
			categoryOptions,
			formRules,
			formEl,
			occurrenceType,
			disabledOccurrenceDate,
			saveInProgress,
			onDialogOpen,
			onDialogClose
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="New Health Concern"
		:width="700"
		append-to-body
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
			>
				<el-select
					v-model="formModel.icd10Code"
					placeholder="Select code"
				>
					<el-option />
				</el-select>
			</el-form-item>
			<el-form-item
				label="SNOMED-CT Code"
			>
				<el-select
					v-model="formModel.snomedCtCode"
					placeholder="Select code"
				>
					<el-option />
				</el-select>
			</el-form-item>
			<el-form-item
				label="Based on"
			>
				<el-input
					v-model="formModel.basedOn"
					placeholder="Conversation with doctor"
				/>
			</el-form-item>
			<el-form-item
				label="Assessment Date"
			>
				<el-date-picker
					v-model="formModel.occurrence"
					:disabled-date="disabledOccurrenceDate"
					placeholder="Select date"
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
			>
				Add Health Concern
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
