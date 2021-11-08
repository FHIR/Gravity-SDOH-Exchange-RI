<script lang="ts">
import "@/assets/scss/styles.scss";
import { defineComponent, ref, computed } from "vue";
import { User } from "@/types";
import { getUserInfo } from "@/api";
import AppHeader from "@/components/AppHeader.vue";
import ServiceRequests from "@/components/ServiceRequests/index.vue";
import OurTasks from "@/components/OurTasks/index.vue";
import useServiceRequests from "./state/useServiceRequests";
import useOurTasks from "./state/useOurTasks";


export type TabName = "requests" | "our-tasks";

export default defineComponent({
	components: { AppHeader, ServiceRequests, OurTasks },
	setup() {
		const user = ref<User | undefined>(undefined);
		const userName = computed<string | undefined>(() => user.value?.name || undefined);

		getUserInfo().then(async resp => {
			user.value = resp;
		});

		const activeTab = ref<TabName>("requests");

		useServiceRequests().startPolling();
		useOurTasks().startPolling();

		return {
			userName,
			activeTab
		};
	}
});
</script>

<template>
	<div class="app-container">
		<AppHeader
			:user-name="userName"
			:active-tab="activeTab"
			@update:active-tab="activeTab = $event"
		/>

		<div class="body">
			<div class="main">
				<ServiceRequests v-if="activeTab === 'requests'" />
				<OurTasks v-if="activeTab === 'our-tasks'" />
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
