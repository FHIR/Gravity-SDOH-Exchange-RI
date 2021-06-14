<script lang="ts">
import { defineComponent, reactive, ref } from "vue";
import { getCategories, getCodes } from "@/api";
import { Coding, newConcernPayload } from "@/types";
import { RuleItem } from "async-validator";
import moment from "moment";
import { ConcernsModule } from "@/store/modules/concerns";

export type FormModel = {
	name: string,
	category: string,
	code: string,
	basedOn: string,
	assessmentDate: string
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
		const codes = ref<Coding[]>([]);

		const formModel = reactive<FormModel>({
			name: "",
			category: "",
			code: "",
			basedOn: "",
			assessmentDate: ""
		});
		const assessmentDate = ref<string>("");

		const formEl = ref<HTMLFormElement>();

		//
		// On dialog open fetch all options for dropdowns and reset previous edits.
		//
		const onDialogOpen = async () => {
			categoryOptions.value = await getCategories();
			codes.value = await getCodes();
		};
		const onDialogClose = () => {
			formEl.value?.resetFields();
			emit("close");
		};

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

		const onFormSave = () => {
			formEl.value?.validate((valid: boolean) => {
				if (valid) {
					const payload: newConcernPayload = { ...formModel };
					saveInProgress.value = true;
					payload.assessmentDate = moment(formModel.assessmentDate).format("YYYY-MM-DD[T]HH:mm:ss");
					try {
						ConcernsModule.createConcern(payload);
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
			codes,
			formRules,
			formEl,
			assessmentDate,
			disabledOccurrenceDate,
			saveInProgress,
			onDialogOpen,
			onDialogClose,
			onFormSave
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
			>
				<el-select
					v-model="formModel.code"
					placeholder="Select code"
				>
					<el-option
						v-for="item in codes"
						:key="item.code"
						:label="`${item.display}(${item.code})`"
						:value="item.code"
					/>
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
					v-model="formModel.assessmentDate"
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
				@click="onFormSave"
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
