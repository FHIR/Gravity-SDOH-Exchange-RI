<script lang="ts">
import { defineComponent, PropType, ref, reactive, watch } from "vue";
import FileInput from "@/components/FileInput.vue";
import { createConsent } from "@/api";

export default defineComponent({
	components: { FileInput },
	props: {
		opened: {
			type: Boolean,
			default: false
		},
		existingConsentNames: {
			type: Array as PropType<string[]>,
			default: () => []
		}
	},
	emits: ["update:opened", "consent-created"],
	setup(props, ctx) {
		const formModel = reactive({
			file: null as File | null,
			name: ""
		});

		watch(() => formModel.file, () => {
			formRef.value?.validateField("file");
		});

		const formRef = ref<any>();

		const nameIsNotUsedValidator = (_: any, value: string, cb: (msg?: string) => void) => {
			if (props.existingConsentNames.includes(value)) {
				cb("Name already used");
			}
			cb();
		};

		const rules = {
			file: { required: true, message: "This field is required" },
			name: [
				{ required: true, message: "This field is required" },
				{ validator: nameIsNotUsedValidator }
			]
		};

		const resetFields = () => {
			formModel.file = null;
			formModel.name = "";
			formRef.value?.clearValidate();
		};

		watch(() => props.opened, () => {
			resetFields();
		});

		const saveInProgress = ref(false);

		const save = async () => {
			const valid = await formRef.value!.validate();
			if (!valid) {
				return;
			}
			if (formModel.file === null || formModel.name === "") {
				return;
			}
			saveInProgress.value = true;
			try {
				const resp = await createConsent(formModel.name, formModel.file!);
				ctx.emit("consent-created", resp);
				resetFields();
			} finally {
				saveInProgress.value = false;
			}
		};

		const beforeClose = () => {
			if (!saveInProgress.value) {
				ctx.emit("update:opened", false);
			}
		};

		return {
			formModel,
			formRef,
			rules,
			saveInProgress,
			save,
			beforeClose
		};
	}
});
</script>

<template>
	<div class="new-consent-dialog">
		<el-dialog
			:model-value="opened"
			title="New Consent"
			:width="700"
			:before-close="beforeClose"
		>
			<el-form
				ref="formRef"
				:model="formModel"
				:rules="rules"
				label-width="155px"
				label-position="left"
				size="mini"
				@submit.prevent="save"
			>
				<el-form-item
					label="Consent Document"
					prop="file"
				>
					<FileInput
						v-model:value="formModel.file"
						:disabled="saveInProgress"
						accept="application/pdf,.pdf"
					/>
				</el-form-item>

				<el-form-item
					label="Consent Name"
					prop="name"
				>
					<el-input
						v-model="formModel.name"
						:disabled="saveInProgress"
						placeholder="Enter Consent Name"
					/>
				</el-form-item>
			</el-form>

			<template #footer>
				<el-button
					round
					size="mini"
					@click="beforeClose"
				>
					Cancel
				</el-button>
				<el-button
					plain
					round
					type="primary"
					size="mini"
					:loading="saveInProgress"
					@click="save"
				>
					Save Consent
				</el-button>
			</template>
		</el-dialog>
	</div>
</template>
