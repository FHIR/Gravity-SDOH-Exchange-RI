<script lang="ts">
import { computed, defineComponent, watch, ref } from "vue";
import { Coding, Concern } from "@/types";
import { ConcernsModule } from "@/store/modules/concerns";
import HealthConcernsTable from "@/components/patients/health-concerns/HealthConcernsTable.vue";
import NewConcernDialog from "@/components/patients/health-concerns/NewConcernDialog.vue";
import NoActiveItems from "@/components/patients/NoActiveItems.vue";
import NoItems from "@/components/patients/NoItems.vue";

export type TableData = {
	id: string,
	name: string,
	assessmentDate?: string,
	startDate?: string
	resolutionDate?: string,
	basedOn: string | {
		display: string,
		id: string,
	},
	category: Coding,
	icdCode: Coding,
	snomedCode: Coding
}

export default defineComponent({
	components: {
		NoItems,
		NoActiveItems,
		HealthConcernsTable,
		NewConcernDialog
	},
	props: {
		isActive: {
			type: Boolean,
			required: true
		}
	},
	emits: ["trigger-open-assessment"],
	setup(props) {
		const isDataLoading = ref<boolean>(false);
		const newConcernDialogVisible = ref<boolean>(false);
		const activeConcerns = computed<Concern[]>(() => ConcernsModule.activeConcerns);
		const activeConcernsTableData = computed<TableData[]>(() =>
			activeConcerns.value.map((concern: Concern) => ({
				id: concern.id,
				name: concern.name,
				assessmentDate: concern.assessmentDate || "",
				startDate: concern.startDate || "",
				basedOn: concern.basedOn,
				category: concern.category,
				icdCode: concern.icdCode,
				snomedCode: concern.snomedCode
			}))
		);
		const resolvedConcerns = computed<Concern[]>(() => ConcernsModule.resolvedConcerns);
		const resolvedConcernsTableData = computed<TableData[]>(() =>
			resolvedConcerns.value.map((concern: Concern) => ({
				id: concern.id,
				name: concern.name,
				assessmentDate: concern.assessmentDate || "",
				startDate: concern.startDate || "",
				resolutionDate: concern.resolutionDate || "",
				basedOn: concern.basedOn,
				category: concern.category,
				icdCode: concern.icdCode,
				snomedCode: concern.snomedCode
			}))
		);

		watch(() => props.isActive, async active => {
			if (active) {
				isDataLoading.value = true;
				try {
					await ConcernsModule.getActiveConcerns();
					await ConcernsModule.getResolvedConcerns();
				} finally {
					isDataLoading.value = false;
				}
			}
		}, { immediate: true });

		return {
			activeConcernsTableData,
			resolvedConcernsTableData,
			isDataLoading,
			newConcernDialogVisible
		};
	}
});
</script>

<template>
	<div
		v-loading="isDataLoading"
		class="health-concerns"
	>
		<HealthConcernsTable
			v-if="activeConcernsTableData.length"
			:data="activeConcernsTableData"
			type="active"
			@add-concern="newConcernDialogVisible = true"
			@trigger-open-assessment="$emit('trigger-open-assessment', $event)"
		/>
		<NoActiveItems
			v-else-if="!activeConcernsTableData.length && resolvedConcernsTableData.length"
			message="No Active Health Concerns Yet"
			button-label="Add Health Concern"
			@add-item="newConcernDialogVisible = true"
		/>
		<HealthConcernsTable
			v-if="resolvedConcernsTableData.length"
			:data="resolvedConcernsTableData"
			type="resolved"
			class="resolved-concerns"
			title="Resolved Health Concerns"
		/>
		<NoItems
			v-if="!isDataLoading && !activeConcernsTableData.length && !resolvedConcernsTableData.length"
			message="No Health Concerns Yet"
			button-label="Add Health Concern"
			@add-item="newConcernDialogVisible = true"
		/>
		<NewConcernDialog
			:visible="newConcernDialogVisible"
			@close="newConcernDialogVisible = false"
		/>
	</div>
</template>

<style lang="scss" scoped>
.promoted-concerns {
	margin-top: 30px;
}
</style>
