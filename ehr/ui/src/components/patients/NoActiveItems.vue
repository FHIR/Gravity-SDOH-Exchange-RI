<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "NoActiveItems",
	props: {
		message: {
			type: String,
			default: "No Active Items"
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
	<div
		class="no-active-items"
	>
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

.no-active-items {
	display: flex;
	justify-content: center;
	align-items: center;
	border: $global-border;
	border-radius: 5px;
	padding: 20px;
	margin-bottom: 30px;

	h2 {
		color: $global-muted-color;
		font-size: $global-large-font-size;
		font-weight: $global-font-weight-normal;
		width: 100%;
		text-align: center;
		margin: 0;
	}
}
</style>
