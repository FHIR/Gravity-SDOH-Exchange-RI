<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "TableCard"
});
</script>

<template>
	<div class="table-card">
		<slot></slot>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.table-card {
	background-color: $global-background;
	box-shadow: $global-box-shadow;
	border-radius: 5px;
	overflow: auto;
	padding: 0 20px;
}
</style>
