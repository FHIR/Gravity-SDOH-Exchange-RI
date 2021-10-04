<script lang="ts">
import { defineComponent } from "vue";
import Tasks from "@/components/Tasks.vue";

export default defineComponent({
	name: "InactiveRequests",
	components: { Tasks }
});
</script>

<template>
	<div class="inactive-requests">
		<Tasks request-type="inactive" />
	</div>
</template>

<style lang="scss" scoped>
.inactive-requests {
	height: 100%;
	padding: 0 45px 45px 45px;
}
</style>
