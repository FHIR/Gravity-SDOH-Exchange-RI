<script lang="ts">
import { computed, defineComponent, PropType, reactive, ref } from "vue";
import { TableData } from "@/components/patients/problems/Problems.vue";
import { ProblemDialogMode } from "@/components/patients/problems/ProblemsTable.vue";
import DropButton from "@/components/DropButton.vue";
import moment from "moment";
import { ProblemsModule } from "@/store/modules/problems";

export type FormModel = {
	id: string,
	name: string,
	basedOn: string,
	startDate: string,
	closedDate?: string,
	goals: number,
	actionSteps: number,
	clinicalStatus: string,
	codeISD: string,
	codeSNOMED: string,
	category: string
};

const CONFIRM_MESSAGES = {
	"view": "",
	"add-goal": "",
	"add-action-step": "",
	"mark-as-close": "Please confirm that this problem can be marked as closed."
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
			type: Object as PropType<TableData>,
			default: undefined
		},
		mode: {
			type: String as PropType<ProblemDialogMode>,
			default: "view"
		}
	},
	emits: ["close", "change-mode"],
	setup(props, { emit }) {
		const formModel = reactive<FormModel>({
			id: "",
			name: "",
			basedOn: "",
			startDate: "",
			closedDate: "",
			category: "",
			goals: 0,
			actionSteps: 0,
			clinicalStatus: "",
			codeISD: "",
			codeSNOMED: ""
		});
		const mode = computed<ProblemDialogMode>(() => props.mode);
		const confirmMessage = computed<string>(() => CONFIRM_MESSAGES[mode.value]);
		const showConfirm = computed<boolean>(() => mode.value !== "view");
		const actionInProgress = ref<boolean>(false);

		const onDialogOpen = () => {
			Object.assign(formModel, props.problem);
		};

		const onDialogClose = () => {
			emit("close");
		};

		const handleActionClick = (action: ProblemDialogMode) => {
			emit("change-mode", action);
		};

		const handleConfirm = () => {
			if(mode.value === "mark-as-close") {
				markAsClosed();
			}
		};

		const markAsClosed = async () => {
			const payload = {
				id: props.problem.id,
				closeDate: moment(new Date()).format("YYYY-MM-DD[T]HH:mm:ss"),
				status: "resolved"
			};

			actionInProgress.value = true;
			try {
				await ProblemsModule.updateProblem(payload);
				emit("close");
			} finally {
				actionInProgress.value = false;
			}
		};

		return {
			formModel,
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
		@opened="onDialogOpen"
	>
		<el-form
			:model="formModel"
			label-width="155px"
			label-position="left"
			size="mini"
			class="problem-form"
		>
			<el-form-item label="Problem">
				{{ formModel.name }}
			</el-form-item>
			<el-form-item label="Category">
				{{ formModel.category }}
			</el-form-item>
			<el-form-item label="ICD-10 Code">
				{{ formModel.codeISD }}
			</el-form-item>
			<el-form-item label="SNOMED-CT Code">
				{{ formModel.codeSNOMED }}
			</el-form-item>
			<el-form-item label="Base on">
				{{ formModel.basedOn }}
			</el-form-item>
			<el-form-item label="Creation Date">
				{{ $filters.formatDateTime(formModel.startDate) }}
			</el-form-item>
			<el-form-item
				v-if="formModel.closedDate"
				label="Closed Date"
			>
				{{ $filters.formatDateTime(formModel.closedDate) }}
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
				@click="$emit('close')"
			>
				Cancel
			</el-button>
			<el-button
				v-if="problem?.status === 'resolved'"
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
					{ id: 'mark-as-close', label: 'Mark as Closed', iconSrc: require('@/assets/images/mark-as-closed.svg') }
				]"
				@click="$emit('close')"
				@item-click="handleActionClick"
			/>
			<el-button
				v-else
				plain
				round
				type="primary"
				size="mini"
				v-loading="actionInProgress"
				@click="handleConfirm"
			>
				Confirm
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.confirm-message {
	margin-bottom: 20px;
	text-align: left;
	font-weight: $global-font-weight-medium;
	font-size: $global-font-size;
}
</style>
