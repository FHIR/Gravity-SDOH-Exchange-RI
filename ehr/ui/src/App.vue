<script lang="ts">
import "@/assets/scss/styles.scss";
import { defineComponent } from "vue";

export default defineComponent({
	name: "App"
});
</script>

<template>
	<el-container>
		<el-header>
			EHR
		</el-header>
		<el-main>
			<router-view />
		</el-main>
	</el-container>
</template>
