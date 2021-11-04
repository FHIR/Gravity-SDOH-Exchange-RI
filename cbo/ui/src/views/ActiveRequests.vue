<script lang="ts">
import { defineComponent } from "vue";
import Tasks from "@/components/Tasks.vue";

export default defineComponent({
	name: "ActiveRequests",
	components: { Tasks }
});
</script>

<template>
	<div class="active-requests">
		<Tasks request-type="active" />
	</div>
</template>

<style scoped lang="scss">

.active-requests {
	height: 100%;
	max-height: calc(100% - 110px);
	padding: 0 45px 45px 45px;
}
</style>
