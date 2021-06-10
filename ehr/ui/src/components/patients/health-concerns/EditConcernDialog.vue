<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { TableData } from "@/components/patients/health-concerns/HealthConcerns.vue";

export default defineComponent({
	name: "EditConcernDialog",
	props: {
		visible: {
			type: Boolean,
			default: false
		},
		concern: {
			type: Object as PropType<TableData>,
			default: undefined
		}
	},
	emits: ["close"],
	setup() {
		const saveInProgress = ref<boolean>(false);

		return {
			saveInProgress
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="Health Concern Details"
		:width="700"
		append-to-body
		destroy-on-close
		custom-class="edit-concern-dialog"
	>
		<el-form
			ref="formEl"
			label-width="155px"
			label-position="left"
			size="mini"
			class="edit-concern-form"
		>
			<el-form-item label="Health Concern">
				{{ concern.name }}
			</el-form-item>
			<el-form-item label="Category">
				{{ concern.category }}
			</el-form-item>
			<el-form-item label="ICD-10 Code">
				code
			</el-form-item>
			<el-form-item label="SNOMED-CT Code">
				code
			</el-form-item>
			<el-form-item label="Based on">
				{{ concern.basedOn }}
			</el-form-item>
			<el-form-item label="Assessment Date">
				{{ $filters.formatDateTime(concern.createdAt) }}
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
				Confirm
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.edit-concern-form {
	.el-divider {
		margin: 20px 0;
	}
}

.wrapper {
	line-height: 15px;
	margin-top: 5px;
	margin-bottom: 10px;

	&:last-child {
		margin-bottom: 0;
	}
}

.item {
	background-color: $alice-blue;
	border-radius: 5px;
	padding: 0 5px;
	font-size: $global-small-font-size;

	@include dont-break-out();
}

.date {
	margin-left: 10px;
}
</style>
