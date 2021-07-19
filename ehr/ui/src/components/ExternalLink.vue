<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
});
</script>

<template>
	<span class="external-link">
		<slot></slot>
		<span class="icon"></span>
	</span>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.external-link {
	display: inline-block;
	cursor: pointer;
}

.external-link + .external-link {
	margin-left: 15px;
}

.icon {
	margin-left: 7px;
	@include icon("~@/assets/images/link.svg", 14px, 14px);
}
</style>
