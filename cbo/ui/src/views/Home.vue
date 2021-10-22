<script lang="ts">
import { defineComponent, computed } from "vue";
import { ActiveTabModule } from "@/store/activeTab";
import ActiveRequests from "@/views/ActiveRequests.vue";
import InactiveRequests from "@/views/InactiveRequests.vue";
import { ACTIVE_REQUESTS_TAB } from "@/utils/constants";
import MainHeader from "@/components/MainHeader.vue";

export default defineComponent({
	name: "Home",
	components: { InactiveRequests, ActiveRequests, MainHeader },

	setup() {
		const activeTab = computed<string>(() => ActiveTabModule.activeTab);

		return {
			activeTab,
			ACTIVE_REQUESTS_TAB
		};
	}
});
</script>

<template>
	<div class="home">
		<MainHeader />
		<ActiveRequests v-if="activeTab === ACTIVE_REQUESTS_TAB" />
		<InactiveRequests v-else />
	</div>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";

.home {
	display: flex;
	flex-direction: column;
	height: 100%;
}
</style>
