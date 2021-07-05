<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { TableData } from "@/components/patients/health-concerns/HealthConcerns.vue";
import DropButton from "@/components/DropButton.vue";

export default defineComponent({
	name: "EditConcernDialog",
	components: {
		DropButton
	},
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
	setup(props, { emit }) {
		const saveInProgress = ref<boolean>(false);

		const onDialogClose = () => {
			emit("close");
		};

		return {
			saveInProgress,
			onDialogClose
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="Health Concern Details"
		:width="700"
		destroy-on-close
		custom-class="edit-concern-dialog"
		@close="onDialogClose"
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
				{{ concern.category.display }}
			</el-form-item>
			<el-form-item label="ICD-10 Code">
				{{ concern.icdCode ? `${concern.icdCode.display} (${concern.icdCode.code})` : "N/A" }}
			</el-form-item>
			<el-form-item label="SNOMED-CT Code">
				{{ concern.snomedCode ? `${concern.snomedCode.display} (${concern.snomedCode.code})` : "N/A" }}
			</el-form-item>
			<el-form-item label="Based on">
				{{ concern.basedOn.display ? concern.basedOn.display : concern.basedOn }}
			</el-form-item>
			<el-form-item label="Assessment Date">
				{{ $filters.formatDateTime(concern.assessmentDate) }}
			</el-form-item>
		</el-form>
		<template #footer>
			<DropButton
				label="Close"
				:items="[
					{ id: '1', label: 'Promote to Problem', iconSrc: require('@/assets/images/concern-promote.svg') },
					{ id: '2', label: 'Mark As Resolved', iconSrc: require('@/assets/images/concern-resolved.svg') },
					{ id: '3', label: 'Remove', iconSrc: require('@/assets/images/concern-remove.svg') }
				]"
				@click="$emit('close')"
			/>
		</template>
	</el-dialog>
</template>
