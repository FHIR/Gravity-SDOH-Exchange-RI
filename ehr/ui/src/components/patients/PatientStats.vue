<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "PatientStats"
});
</script>

<template>
	<el-card class="patient-stats">
		<div class="info-block">
			<div class="info-item">
				<div class="value">
					0
				</div>
				<div class="label">
					Health Concerns
				</div>
			</div>
			<div class="info-item">
				<div class="value">
					0
				</div>
				<div class="label">
					Active Problems
				</div>
			</div>
			<div class="info-item">
				<div class="value">
					0
				</div>
				<div class="label">
					Active Goals
				</div>
			</div>
			<div class="info-item">
				<div class="value">
					0
				</div>
				<div class="label">
					Active Interventions
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
