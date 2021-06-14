<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import FileInput from "@/components/FileInput.vue";

export default defineComponent({
	components: { FileInput },
	props: {
		opened: {
			type: Boolean,
			default: false
		}
	},
	emits: ["update:opened"],
	setup(_, ctx) {
		const file = ref<File | null>(null);
		const name = ref("");
		const status = ref("Active");
		const scope = ref("Patient Privacy (Code)");
		const date = ref("");

		const beforeClose = () => {
			ctx.emit("update:opened", false);
		};

		return {
			file,
			name,
			status,
			scope,
			date,
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
				label-width="155px"
				label-position="left"
				size="mini"
			>
				<el-form-item label="Category">
					IDSCL (healthcare information disclosure)
				</el-form-item>

				<el-form-item label="Consent Document">
					<FileInput
						v-model:value="file"
					/>
				</el-form-item>

				<el-form-item label="Consent Name">
					<el-input
						v-model="name"
					/>
				</el-form-item>

				<el-form-item label="Status">
					<el-select
						v-model="status"
					>
					</el-select>
				</el-form-item>

				<el-form-item label="Scope">
					<el-select
						v-model="scope"
					>
					</el-select>
				</el-form-item>

				<el-form-item label="Consent Date">
					<el-date-picker
						placeholder="Select date"
						v-model="date"
						class="consent-date-picker"
					/>
				</el-form-item>

				<el-form-item label="Organization">
					Provider/EHR
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
				>
					Save Consent
				</el-button>
			</template>
		</el-dialog>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.new-consent-dialog {
	::v-deep(.el-date-editor.consent-date-picker) {
		width: 130px;
	}
}
</style>
