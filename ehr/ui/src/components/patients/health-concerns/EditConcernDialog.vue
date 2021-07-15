<script lang="ts">
import { computed, defineComponent, PropType, ref, toRefs } from "vue";
import { TableData } from "@/components/patients/health-concerns/HealthConcerns.vue";
import DropButton from "@/components/DropButton.vue";
import { ConcernsModule } from "@/store/modules/concerns";
import { ConcernAction } from "@/components/patients/health-concerns/HealthConcernsTable.vue";

const CONFIRM_TEXT = {
	"view": "",
	"promote-to-problem": "Please confirm this health concern can be promoted to a problem.",
	"mark-as-resolved": "Please confirm that this health concern can be marked as resolved.",
	"remove": "Please confirm that you want to remove this health concern."
};

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
		openPhase: {
			type: String as PropType<ConcernAction>,
			default: "view"
		},
		type: {
			type: String,
			required: true
		}
	},
	emits: ["close", "change-action", "trigger-open-assessment"],
	setup(props, { emit }) {
		const { concern, openPhase } = toRefs(props);
		const actionInProgress = ref<boolean>(false);
		const phase = ref<ConcernAction>("view");
		const confirmActionText = computed<string>(() => CONFIRM_TEXT[phase.value]);
		const showConfirm = computed<boolean>(() => phase.value !== "view");

		const onDialogClose = () => {
			emit("close");
		};

		const onDialogOpen = () => {
			phase.value = openPhase.value;
		};

		const confirmActionClick = async () => {
			actionInProgress.value = true;
			try {
				if (phase.value === "remove") {
					await ConcernsModule.removeConcern(concern.value!.id);
				} else if (phase.value === "mark-as-resolved") {
					await ConcernsModule.resolveConcern(concern.value!.id);
				} else if (phase.value === "promote-to-problem") {
					await ConcernsModule.promoteConcern(concern.value!.id);
				}
				emit("close");
			} finally {
				actionInProgress.value = false;
			}
		};

		const handleActionClick = (clickedAction: ConcernAction) => {
			phase.value = clickedAction;
		};

		return {
			actionInProgress,
			onDialogClose,
			confirmActionText,
			confirmActionClick,
			handleActionClick,
			onDialogOpen,
			showConfirm,
			phase
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
		@open="onDialogOpen"
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
				<span
					v-if="concern.basedOn.id"
					class="icon-link"
					@click="$emit('trigger-open-assessment', concern.basedOn.id)"
				>
				</span>
			</el-form-item>
			<el-form-item label="Assessment Date">
				{{ concern.assessmentDate ? $filters.formatDateTime(concern.assessmentDate) : "N/A" }}
			</el-form-item>
			<el-form-item
				v-if="type === 'resolved'"
				label="Resolution Date"
			>
				{{ concern.resolutionDate ? $filters.formatDateTime(concern.resolutionDate) : "N/A" }}
			</el-form-item>
		</el-form>
		<template #footer>
			<div
				v-if="showConfirm"
				class="confirm-text"
			>
				{{ confirmActionText }}
			</div>
			<el-button
				v-if="showConfirm"
				plain
				round
				type="primary"
				size="mini"
				@click="phase = 'view'"
			>
				Cancel
			</el-button>
			<el-button
				v-if="showConfirm"
				plain
				round
				type="primary"
				size="mini"
				:loading="actionInProgress"
				@click="confirmActionClick"
			>
				Confirm
			</el-button>
			<el-button
				v-if="type === 'resolved'"
				plain
				round
				type="primary"
				size="mini"
				@click="$emit('close')"
			>
				Close
			</el-button>
			<DropButton
				v-else-if="!showConfirm"
				label="Close"
				:items="[
					{ id: 'promote-to-problem', label: 'Promote to Problem', iconSrc: require('@/assets/images/concern-promote.svg') },
					{ id: 'mark-as-resolved', label: 'Mark As Resolved', iconSrc: require('@/assets/images/concern-resolved.svg') },
					{ id: 'remove', label: 'Remove', iconSrc: require('@/assets/images/concern-remove.svg') }
				]"
				@click="$emit('close')"
				@item-click="handleActionClick"
			/>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.confirm-text {
	text-align: left;
	margin-bottom: 20px;
	font-size: $global-font-size;
	font-weight: $global-font-weight-medium;
}

.icon-link {
	vertical-align: middle;
	position: relative;
	left: 7px;
	cursor: pointer;

	@include icon("~@/assets/images/link.svg", 14px, 14px);
}
</style>
