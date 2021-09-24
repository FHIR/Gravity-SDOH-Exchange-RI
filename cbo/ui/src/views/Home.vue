<script lang="ts">
import { defineComponent, computed } from "vue";
import { ActiveTabModule } from "@/store/activeTab";
import ActiveRequests from "@/views/ActiveRequests.vue";
import InactiveRequests from "@/views/InactiveRequests.vue";
import { ACTIVE_REQUESTS_TAB } from "@/utils/constants";

export default defineComponent({
	name: "Home",
	components: { InactiveRequests, ActiveRequests },

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
		<ActiveRequests v-if="activeTab === ACTIVE_REQUESTS_TAB" />
		<InactiveRequests v-else />
	</div>
</template>
