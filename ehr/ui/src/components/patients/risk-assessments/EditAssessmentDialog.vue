<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { TableData } from "@/components/patients/action-steps/ActionSteps.vue";

export default defineComponent({
	name: "EditAssessmentDialog",
	props: {
		visible: {
			type: Boolean,
			default: false
		},
		assessment: {
			type: Object as PropType<TableData>,
			default: undefined
		}
	},
	emits: ["close"],
	setup(props, { emit }) {
		//todo: use element-ui form type
		const formEl = ref<HTMLFormElement>();

		const onDialogClose = () => {
			emit("close");
		};

		return {
			onDialogClose,
			formEl
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="Observations"
		:width="700"
		append-to-body
		destroy-on-close
		custom-class="edit-assessment-dialog"
		@close="onDialogClose"
	>
		<el-form
			ref="formEl"
			label-width="155px"
			label-position="left"
			size="mini"
			class="edit-assessment-form"
		>
			<h4>
				Summary
			</h4>
			<el-form-item label="Assessment Name">
				{{ assessment.name }}
			</el-form-item>
			<el-form-item label="Assessment Date">
				{{ $filters.formatDateTime(assessment.createdAt) }}
			</el-form-item>
			<el-form-item label="Health Concern(s)">
				{{ assessment.concerns }}
			</el-form-item>
			<el-form-item label="Previous Assessment">
				N/A
			</el-form-item>

			<el-divider />
			<h4>
				Question-Answer Pairs
			</h4>

			<el-form-item
				label="Question 1:"
				label-position="top"
			>
				Question 1
			</el-form-item>
			<el-form-item label="Question 2:">
				Question 2
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button
				round
				size="mini"
				@click="$emit('close')"
			>
				Close
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.edit-assessment-form {
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
