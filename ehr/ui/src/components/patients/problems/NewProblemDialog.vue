<script lang="ts">
import { defineComponent, ref, reactive } from "vue";
import { Coding } from "@/types";

export type FormModel = {
	name: string,
	category: string,
	code: string,
	basedOn: string,
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
			code: "",
			basedOn: "",
			startDate: ""
		});
		const categoryOptions = ref<Coding[]>([]);
		const codeOptions = ref<Coding[]>([]);

		const onDialogClose = () => {
			formEl.value?.resetFields();
			emit("close");
		};

		return {
			formModel,
			onDialogClose,
			formEl,
			categoryOptions,
			codeOptions
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
	>
		<el-form
			ref="formEl"
			:model="formModel"
			label-width="155px"
			label-position="left"
			size="mini"
			class="new-problem-form"
		>
			<el-form-item
				label="Problem"
			>
				<el-input
					v-model="formModel.name"
					placeholder="Enter problem name"
				/>
			</el-form-item>
			<el-form-item
				label="Category"
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
			>
				<el-select
					v-model="formModel.code"
					placeholder="Select Code"
				>
					<el-option
						v-for="item in codeOptions"
						:key="item.code"
						:label="item.display"
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
