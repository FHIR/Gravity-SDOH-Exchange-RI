<script lang="ts">
import { defineComponent } from "vue";
import MainHeader from "@/components/MainHeader.vue";

export default defineComponent({
	name: "ManageServers",
	components: { MainHeader }
});
</script>

<template>
	<div class="manage-servers">
		<MainHeader />
		Manage Servers
	</div>
</template>
