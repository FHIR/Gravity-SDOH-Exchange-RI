<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "NoItems",
	props: {
		message: {
			type: String,
			default: "No Items"
		},
		buttonLabel: {
			type: String,
			default: "Add Item"
		}
	},
	emits: ["add-item"]
});
</script>

<template>
	<div class="no-data">
		<h2>{{ message }}</h2>
		<el-button
			plain
			round
			type="primary"
			size="mini"
			@click="$emit('add-item')"
		>
			{{ buttonLabel }}
		</el-button>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.no-data {
	height: 240px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: flex-start;
	background-color: $global-background;

	h2 {
		color: $global-muted-color;
		font-size: $global-xxxlarge-font-size;
		font-weight: $global-font-weight-normal;
		margin-bottom: 50px;
	}
}
</style>
