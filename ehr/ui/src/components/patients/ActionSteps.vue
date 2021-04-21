<script lang="ts">
import { defineComponent, ref } from "vue";
import RequestTable from "@/components/patients/RequestTable.vue";

export default defineComponent({
	name: "ActionSteps",
	components: {
		RequestTable
	},
	setup() {
		const activeGroup = ref<string>("referrals");

		return {
			activeGroup
		};
	}
});
</script>

<template>
	<div class="action-steps">
		<el-radio-group
			v-model="activeGroup"
			size="mini"
		>
			<el-radio-button label="interventions">
				Interventions
			</el-radio-button>
			<el-radio-button label="referrals">
				Referrals
			</el-radio-button>
		</el-radio-group>
		<div v-if="activeGroup === 'interventions'">
			Interventions
		</div>
		<div v-else>
			<RequestTable />
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.el-radio-group {
	margin-bottom: 10px;
}
</style>
