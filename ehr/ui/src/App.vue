<script lang="ts">
import "@/assets/scss/styles.scss";
import { defineComponent, onMounted } from "vue";
import MainHeader from "@/components/MainHeader.vue";
import { ContextModule } from "@/store/modules/context";

export default defineComponent({
	name: "App",
	components: {
		MainHeader
	},
	setup() {
		onMounted(() => ContextModule.getContext());
	}
});
</script>

<template>
	<el-container
		direction="vertical"
		class="main-container"
	>
		<MainHeader />
		<el-main class="main-area">
			<router-view />
		</el-main>
	</el-container>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.main-container {
	height: 100vh;
}

.main-area {
	background-color: $aqua-haze;
}
</style>
