<script lang="ts">
import { computed, defineComponent, onMounted, ref } from "vue";
import { Coding, Concern } from "@/types";
import { ConcernsModule } from "@/store/modules/concerns";
import HealthConcernsTable from "@/components/patients/health-concerns/HealthConcernsTable.vue";

export type TableData = {
	name: string,
	assessmentDate: string,
	basedOn: string | {
		display: string,
		id: string,
	},
	category: Coding,
	icdCode: Coding,
	snomedCode: Coding
}

export default defineComponent({
	name: "HealthConcerns",
	components: {
		HealthConcernsTable
	},
	setup() {
		const isRequestLoading = ref<boolean>(false);
		const activeConcerns = computed<Concern[]>(() => ConcernsModule.activeConcerns);
		const activeConcernsTableData = computed<TableData[]>(() =>
			activeConcerns.value.map((concern: Concern) => ({
				name: concern.name,
				assessmentDate: concern.date,
				basedOn: concern.basedOn,
				category: concern.category,
				icdCode: concern.icdCode,
				snomedCode: concern.snomedCode
			}))
		);
		const resolvedConcerns = computed<Concern[]>(() => ConcernsModule.resolvedConcerns);
		const resolvedConcernsTableData = computed<TableData[]>(() =>
			resolvedConcerns.value.map((concern: Concern) => ({
				name: concern.name,
				assessmentDate: concern.date,
				basedOn: concern.basedOn,
				category: concern.category,
				icdCode: concern.icdCode,
				snomedCode: concern.snomedCode
			}))
		);

		onMounted(async () => {
			isRequestLoading.value = true;
			try {
				await ConcernsModule.getActiveConcerns();
				await ConcernsModule.getResolvedConcerns();
			} finally {
				isRequestLoading.value = false;
			}
		});

		return {
			activeConcernsTableData,
			resolvedConcernsTableData,
			isRequestLoading
		};
	}
});
</script>

<template>
	<div class="health-concerns">
		<div
			v-loading="isRequestLoading"
		>
			<HealthConcernsTable
				v-if="activeConcernsTableData.length"
				:data="activeConcernsTableData"
				type="ActiveConcerns"
			/>
			<div
				v-if="!isRequestLoading && !activeConcernsTableData.length"
				class="no-request-data"
			>
				<h2>No Health Concerns Yet</h2>
				<el-button
					plain
					round
					type="primary"
					size="mini"
				>
					Add Health Concern
				</el-button>
			</div>
			<HealthConcernsTable
				v-if="resolvedConcernsTableData.length"
				:data="resolvedConcernsTableData"
				type="ResolvedConcerns"
				class="resolved-concerns"
				title="Resolved Health Concerns"
			/>
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.no-request-data {
	height: 240px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: flex-start;
	background-color: $global-background;

	h2 {
		color: $whisper;
		font-size: $global-xxxlarge-font-size;
		font-weight: $global-font-weight-normal;
		margin-bottom: 50px;
	}
}

.promoted-concerns {
	margin-top: 30px;
}
</style>
