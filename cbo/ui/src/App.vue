<script lang="ts">
import "@/assets/scss/styles.scss";
import { defineComponent, onMounted } from "vue";
import { ContextModule } from "@/store/context";

export default defineComponent({
	name: "App",
	setup() {
		onMounted(() => ContextModule.getContext());
	}
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
