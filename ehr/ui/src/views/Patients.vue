<script lang="ts">
import { defineComponent } from "vue";
import PatientInfo from "@/components/patients/PatientInfo.vue";
import PatientStats from "@/components/patients/PatientStats.vue";

export default defineComponent({
	name: "Patients",
	components: {
		PatientInfo,
		PatientStats
	}
});
</script>

<template>
	<div>
		<el-row :gutter="45">
			<el-col :span="17">
				<PatientInfo />
			</el-col>
			<el-col :span="7">
				<PatientStats />
			</el-col>
		</el-row>
	</div>
</template>
