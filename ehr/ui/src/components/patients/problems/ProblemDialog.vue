<script lang="ts">
import { computed, defineComponent, PropType, ref, toRefs } from "vue";
import { TableData } from "@/components/patients/problems/Problems.vue";
import { ProblemDialogPhase, ProblemActionType } from "@/components/patients/problems/ProblemsTable.vue";
import DropButton from "@/components/DropButton.vue";
import { ProblemsModule } from "@/store/modules/problems";
import { showDefaultNotification } from "@/utils/utils";

const CONFIRM_MESSAGES = {
	"view": "",
	"mark-as-closed": "Please confirm that this problem can be marked as closed.",
	// todo: after Intervention action steps will be done
	"add-action-step": "Please confirm that you want to add referral action step to this problem"
};

export default defineComponent({
	name: "ProblemDialog",
	components: { DropButton },
	props: {
		visible: {
			type: Boolean,
			default: false
		},
		problem: {
			type: Object as PropType<TableData | undefined>,
			default: undefined
		},
		openPhase: {
			type: String as PropType<ProblemDialogPhase>,
			default: "view"
		},
		status: {
			type: String,
			default: "active"
		}
	},
	emits: ["close", "trigger-add-goal", "trigger-open-assessment", "trigger-add-action-step"],
	setup(props, { emit }) {
		const { problem, openPhase } = toRefs(props);
		const phase = ref<ProblemDialogPhase>("view");
		const confirmMessage = computed<string>(() => CONFIRM_MESSAGES[phase.value]);
		const showConfirm = computed<boolean>(() => phase.value !== "view");
		const actionInProgress = ref<boolean>(false);

		const onDialogOpen = () => {
			phase.value = openPhase.value;
		};

		const onDialogClose = () => {
			emit("close");
		};

		const handleActionClick = (action: ProblemActionType) => {
			if (action === "mark-as-closed" || action === "add-action-step") {
				phase.value = action;
				return;
			}

			if (action === "add-goal") {
				emit("trigger-add-goal", problem.value!.id);
				emit("close");
			}
		};

		const handleConfirm = () => {
			if (phase.value === "mark-as-closed") {
				markAsClosed();
				return;
			}

			if (phase.value === "add-action-step") {
				emit("trigger-add-action-step", problem.value!.id);
				emit("close");
			}
		};

		const markAsClosed = async () => {
			actionInProgress.value = true;
			try {
				await ProblemsModule.closeProblem(problem.value!.id);
				showDefaultNotification("Problem was Marked as Closed.");
				emit("close");
			} finally {
				actionInProgress.value = false;
			}
		};

		return {
			phase,
			confirmMessage,
			showConfirm,
			actionInProgress,
			onDialogOpen,
			onDialogClose,
			handleActionClick,
			handleConfirm
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="Problem Details"
		:width="700"
		destroy-on-close
		custom-class="problem-dialog"
		@close="onDialogClose"
		@open="onDialogOpen"
	>
		<el-form
			label-width="155px"
			label-position="left"
			size="mini"
			class="problem-form"
		>
			<el-form-item label="Problem">
				{{ problem.name }}
			</el-form-item>
			<el-form-item label="Category">
				{{ problem.category.display }}
			</el-form-item>
			<el-form-item label="ICD-10 Code">
				{{ problem.icdCode ? `${problem.icdCode.display} (${problem.icdCode.code})` : "N/A" }}
			</el-form-item>
			<el-form-item label="SNOMED-CT Code">
				{{ problem.snomedCode ? `${problem.snomedCode.display} (${problem.snomedCode.code})` : "N/A" }}
			</el-form-item>
			<el-form-item label="Base on">
				{{ problem.basedOn.display ? problem.basedOn.display : problem.basedOn }}
				<span
					v-if="problem.basedOn.id"
					class="icon-link"
					@click="$emit('trigger-open-assessment', problem.basedOn.id)"
				>
				</span>
			</el-form-item>
			<el-form-item label="Start Date">
				{{ problem.startDate ? $filters.formatDateTime(problem.startDate) : "N/A" }}
			</el-form-item>
			<el-form-item
				v-if="status === 'closed'"
				label="Closed Date"
			>
				{{ $filters.formatDateTime(problem.resolutionDate) }}
			</el-form-item>
		</el-form>
		<template #footer>
			<div
				v-if="showConfirm"
				class="confirm-message"
			>
				{{ confirmMessage }}
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
				v-if="status === 'closed'"
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
				:items="[{ id: 'add-goal', label: 'Add Goal', iconSrc: require('@/assets/images/add-goal.svg') },
					{ id: 'add-action-step', label: 'Add Action Step', iconSrc: require('@/assets/images/add-action-step.svg') },
					{ id: 'mark-as-closed', label: 'Mark as Closed', iconSrc: require('@/assets/images/mark-as-closed.svg') }
				]"
				@click="$emit('close')"
				@item-click="handleActionClick"
			/>
			<el-button
				v-else
				:loading="actionInProgress"
				plain
				round
				type="primary"
				size="mini"
				@click="handleConfirm"
			>
				Confirm
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.confirm-message {
	margin-bottom: 20px;
	text-align: left;
	font-weight: $global-font-weight-medium;
	font-size: $global-font-size;
}

.icon-link {
	position: relative;
	left: 7px;
	cursor: pointer;

	@include icon("~@/assets/images/link.svg", 14px, 14px);
}

</style>
