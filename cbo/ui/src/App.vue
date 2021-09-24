<script lang="ts">
import "@/assets/scss/styles.scss";
import { defineComponent } from "vue";
import MainHeader from "@/components/MainHeader.vue";

export default defineComponent({
	name: "App",
	components: { MainHeader }
});
</script>

<template>
	<el-container
		direction="vertical"
	>
		<MainHeader />
		<el-main>
			<router-view />
		</el-main>
	</el-container>
</template>
