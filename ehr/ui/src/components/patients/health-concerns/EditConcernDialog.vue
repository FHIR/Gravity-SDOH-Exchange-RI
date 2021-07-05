<script lang="ts">
import { computed, defineComponent, PropType, ref } from "vue";
import { TableData } from "@/components/patients/health-concerns/HealthConcerns.vue";
import DropButton from "@/components/DropButton.vue";
import { ACTION_BUTTONS } from "@/utils/constants";
import { ConcernsModule } from "@/store/modules/concerns";
import { Concern } from "@/types";

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
			type: Object as PropType<TableData | undefined>,
			default: undefined
		},
		clickedAction: {
			type: String,
			required: true
		}
	},
	emits: ["close", "change-action"],
	setup(props, { emit }) {
		const saveInProgress = ref<boolean>(false);

		const onDialogClose = () => {
			emit("close");
		};

		const confirmAction = (clickedAction: string) => {
			if (clickedAction === ACTION_BUTTONS.remove) {
				ConcernsModule.removeConcern(props.concern!.id);

				// TODO while we don't have BE
				//Temporarily, in case Promote to problem or Mark as Resolved push to "Promoted Or Resolved table"
			} else if (clickedAction === ACTION_BUTTONS.promoteToProblem || props.clickedAction === ACTION_BUTTONS.markAsResolved) {
				const promotedOrResolvedConcern: Concern = { ...props.concern!, concernStatus: "PromotedOrResolved" };
				ConcernsModule.promoteOrResolveConcern(promotedOrResolvedConcern);
			}
		};

		const handleActionClick = (clickedAction: string) => {
			emit("change-action", clickedAction);
		};

		const confirmActionText = computed<string>(() => {
			switch (props.clickedAction) {
				case ACTION_BUTTONS.promoteToProblem:
					return "Please confirm this health concern can be promoted to a problem.";
				case ACTION_BUTTONS.markAsResolved:
					return "Please confirm that this health concern can be marked as resolved.";
				case ACTION_BUTTONS.remove:
					return "Please confirm that you want to remove this health concern.";

				default: return "Save changes" ;
			}
		});


		return {
			saveInProgress,
			onDialogClose,
			ACTION_BUTTONS,
			confirmActionText,
			confirmAction,
			handleActionClick
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
			<div
				v-if="clickedAction !== ACTION_BUTTONS.default"
				class="confirm-text"
			>
				{{ confirmActionText }}
			</div>
			<el-button
				round
				size="mini"
				@click="$emit('close')"
			>
				Cancel
			</el-button>
			<DropButton
				v-if="clickedAction === ACTION_BUTTONS.default"
				:label="clickedAction"
				:items="[
					{ id: '1', label: 'Promote to Problem', iconSrc: require('@/assets/images/concern-promote.svg') },
					{ id: '2', label: 'Mark As Resolved', iconSrc: require('@/assets/images/concern-resolved.svg') },
					{ id: '3', label: 'Remove', iconSrc: require('@/assets/images/concern-remove.svg') }
				]"
				@click="$emit('close')"
				@action-click="handleActionClick"
			/>
			<el-button
				v-else
				plain
				round
				type="primary"
				size="mini"
				@click="confirmAction(clickedAction)"
			>
				Confirm
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.confirm-text {
	text-align: left;
	margin-bottom: 20px;
	font-size: $global-font-size;
	font-weight: $global-font-weight-medium;
}
</style>
