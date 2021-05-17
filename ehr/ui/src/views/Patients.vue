<script lang="ts">
import { defineComponent } from "vue";
import PatientInfo from "@/components/patients/PatientInfo.vue";
import PatientStats from "@/components/patients/PatientStats.vue";
import Breadcrumbs from "@/components/patients/Breadcrumbs.vue";
import Tabs from "@/components/patients/Tabs.vue";

export default defineComponent({
	name: "Patients",
	components: {
		PatientInfo,
		PatientStats,
		Breadcrumbs,
		Tabs
	}
});
</script>

<template>
	<div class="patients">
		<el-row>
			<Breadcrumbs />
		</el-row>
		<el-row :gutter="45">
			<el-col
				:xl="17"
				:lg="17"
				:md="24"
			>
				<PatientInfo />
			</el-col>
			<el-col
				:xl="7"
				:lg="7"
				:md="24"
			>
				<PatientStats />
			</el-col>
		</el-row>
		<el-row>
			<Tabs />
		</el-row>
	</div>
</template>

<style lang="scss" scoped>
.patients .el-row {
	margin-bottom: 30px;
}
</style>
