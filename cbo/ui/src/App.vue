<script lang="ts">
import "@/assets/scss/styles.scss";
import { defineComponent } from "vue";

export default defineComponent({
	name: "App"
});
</script>

<template>
	<el-container
		direction="vertical"
	>
		<el-main>
			<router-view />
		</el-main>
	</el-container>
</template>

<style lang="scss" scoped>

::v-deep(.el-main) {
	padding: 0;
}
</style>
