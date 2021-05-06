<script lang="ts">
import "@/assets/scss/styles.scss";
import { defineComponent, ref, computed } from "vue";
import { User } from "@/types";
import { getUserInfo } from "@/api";
import AppHeader from "@/components/AppHeader.vue";
import Tasks from "@/components/Tasks.vue";


export default defineComponent({
	components: { AppHeader, Tasks },
	setup() {
		const user = ref<User | undefined>(undefined);
		const userName = computed<string | undefined>(() => user.value?.name || undefined);

		getUserInfo().then(async resp => {
			user.value = resp;
		});

		return {
			userName
		};
	}
});
</script>

<template>
	<div class="app-container">
		<AppHeader
			:user-name="userName"
		/>

		<div class="body">
			<div class="main">
				<Tasks />
			</div>
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.app-container {
	width: 100vw;
	height: 100vh;
	background-color: $aqua-haze;
	display: flex;
	flex-direction: column;

	.body {
		flex: 1;
		padding: 30px 45px 45px 45px;
		overflow: hidden;

		.main {
			height: 100%;
			width: 100%;
		}
	}
}
</style>
