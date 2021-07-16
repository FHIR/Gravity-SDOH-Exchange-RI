<script lang="ts">
import { computed, defineComponent, onMounted } from "vue";
import { ActiveResourcesModule } from "@/store/modules/activeResources";
import { ActiveResources } from "@/types";

export default defineComponent({
	name: "PatientStats",
	setup() {
		const activeResources = computed<ActiveResources>(() => ActiveResourcesModule.activeResources);

		onMounted(async () => {
			await ActiveResourcesModule.loadActiveResources();
		});

		return {
			activeResources
		};
	}
});
</script>

<template>
	<el-card class="patient-stats">
		<div class="info-block">
			<div class="info-item">
				<div class="value">
					{{ activeResources.activeConcernsCount }}
				</div>
				<div class="label">
					Health Concerns
				</div>
			</div>
			<div class="info-item">
				<div class="value">
					{{ activeResources.activeProblemsCount }}
				</div>
				<div class="label">
					Active Problems
				</div>
			</div>
			<div class="info-item">
				<div class="value">
					{{ activeResources.activeGoalsCount }}
				</div>
				<div class="label">
					Active Goals
				</div>
			</div>
			<div class="info-item">
				<div class="value">
					{{ activeResources.activeInterventionsCount }}
				</div>
				<div class="label">
					Active Action Steps
				</div>
			</div>
		</div>
	</el-card>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.patient-stats {
	height: 100%;
}

.info-block {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 100%;
}

.info-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	margin-right: 25px;

	&:last-child {
		margin-right: 0;
	}

	.value {
		font-size: 64px;
		color: $global-primary-color;
		font-weight: $global-font-weight-medium;
	}

	.label {
		font-size: $global-font-size;
		text-align: center;
	}
}
</style>
