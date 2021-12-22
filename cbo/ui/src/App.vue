<script lang="ts">
import "@/assets/scss/styles.scss";
import { defineComponent,  onMounted } from "vue";
import { ContextModule } from "@/store/context";
import { TasksModule } from "@/store/modules/tasks";
import { showUpdates } from "@/utils";

export default defineComponent({
	name: "App",
	setup() {
		onMounted(() => ContextModule.getContext());
		TasksModule.startPolling(showUpdates);
	}
});
</script>

<template>
	<div class="app-container">
		<div class="body">
			<div class="main">
				<router-view />
			</div>
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.app-container {
	width: 100vw;
	height: 100vh;
	background-color: $alice-blue;
	display: flex;
	flex-direction: column;

	.body {
		flex: 1;
		overflow: hidden;

		.main {
			height: 100%;
			width: 100%;
		}
	}
}
</style>
