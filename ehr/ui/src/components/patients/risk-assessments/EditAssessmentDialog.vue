<script lang="ts">
import { defineComponent, PropType, ref, computed } from "vue";
import { Assessment } from "@/types";

export default defineComponent({
	props: {
		visible: {
			type: Boolean,
			default: false
		},
		assessment: {
			type: Object as PropType<Assessment | undefined>,
			default: undefined
		}
	},
	emits: ["close"],
	setup(props, { emit }) {
		const onDialogClose = () => {
			emit("close");
		};

		const currentVersionIx = ref<number>(0);

		const maxVersionIx = computed<number>(() => props.assessment?.previous?.length || 0);

		const getVersionByIx = (ass: Assessment | undefined, ix: number) => {
			if (ix === 0) {
				return ass;
			}
			if (!ass || !ass.previous) {
				return undefined;
			}
			return ass.previous[ix - 1];
		};

		const currentAssessment = computed<Assessment | undefined>(() => getVersionByIx(props.assessment, currentVersionIx.value));

		const hasPreviousVersion = computed(() => currentVersionIx.value < maxVersionIx.value);
		const previousAssessment = computed<Assessment | undefined>(() => hasPreviousVersion.value ? getVersionByIx(props.assessment, currentVersionIx.value + 1) : undefined);

		const hasNextVersion = computed(() => currentVersionIx.value > 0);
		const nextAssessment = computed<Assessment | undefined>(() => hasNextVersion.value ? getVersionByIx(props.assessment, currentVersionIx.value - 1) : undefined);

		const viewPreviousVersion = () => {
			currentVersionIx.value += 1;
		};

		const viewNextVersion = () => {
			currentVersionIx.value -= 1;
		};

		return {
			onDialogClose,
			previousAssessment,
			nextAssessment,
			currentAssessment,
			viewPreviousVersion,
			viewNextVersion
		};
	}
});
</script>

<template>
	<div class="edit-assessment-dialog">
		<el-dialog
			:model-value="visible"
			title="Observations"
			:width="700"
			@close="onDialogClose"
		>
			<div class="dialog-body">
				<el-form
					v-if="currentAssessment"
					label-width="175px"
					label-position="left"
					size="mini"
				>
					<h4>
						Summary
					</h4>
					<el-form-item label="Assessment Name">
						{{ currentAssessment.name }}
					</el-form-item>
					<el-form-item label="Assessment Date">
						{{ $filters.formatDateTime(currentAssessment.date) }}
					</el-form-item>
					<el-form-item label="Health Concern(s)">
						<span
							v-for="concern in currentAssessment.healthConcerns"
							:key="concern.id"
						>
							{{ concern.display }}
						</span>
					</el-form-item>
					<el-form-item
						v-if="previousAssessment"
						label="Past Assessment"
					>
						<el-button
							type="text"
							@click="viewPreviousVersion"
						>
							{{ previousAssessment.name }}
						</el-button>
						{{ $filters.formatDateTime(previousAssessment.date) }}
					</el-form-item>
					<el-form-item
						v-if="nextAssessment"
						label="Subsequent Assessment"
					>
						<el-button
							type="text"
							@click="viewNextVersion"
						>
							{{ nextAssessment.name }}
						</el-button>
						{{ $filters.formatDateTime(nextAssessment.date) }}
					</el-form-item>
				</el-form>

				<el-divider />

				<el-form
					v-if="currentAssessment"
					label-width="90px"
					label-position="left"
					size="mini"
				>
					<h4>
						Question-Answer Pairs
					</h4>

					<div
						v-for="(item, ix) in currentAssessment.assessmentResponse"
						:key="ix"
						class="question"
					>
						<el-form-item
							:label="`Question ${ix+1}`"
						>
							{{ item.question }}
						</el-form-item>
						<el-form-item
							label="Response"
						>
							{{ item.answer }}
						</el-form-item>
					</div>
				</el-form>
			</div>

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
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.edit-assessment-dialog {
	::v-deep(.el-dialog__body) {
		padding: 0;

		.el-button--text {
			padding-left: 0;
		}
	}
}

.dialog-body {
	padding: 5px 25px 15px 25px;
	max-height: 500px;
	overflow-y: auto;
}

.question + .question {
	margin-top: 30px;
}
</style>
