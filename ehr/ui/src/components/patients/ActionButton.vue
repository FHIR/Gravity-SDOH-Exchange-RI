<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "ActionButton",
	props: {
		iconClass: {
			type: String,
			require: true
		},
		label: {
			type: String,
			require: true
		}
	}
});
</script>

<template>
	<button class="action-button">
		<span class="content">
			<span
				class="icon"
				:class="iconClass"
			></span>
			<span class="label">{{ label }}</span>
		</span>
	</button>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.action-button {
	background-color: $global-background;
	padding: 3px 12px;
	min-height: 25px;
	min-width: 44px;
	border-radius: 19px;
	border: 1px solid $global-base-border-color;
	vertical-align: center;
	cursor: pointer;

	.content {
		display: flex;
	}

	.label {
		font-size: $global-font-size;
		line-height: 16px;
		margin-left: 5px;
		display: none;
	}

	&:hover,
	&:focus {
		background-color: $global-background;
		color: $global-text-color;
		border-color: $global-primary-color;

		.label {
			display: inline-block;
		}
	}

	+ .action-button {
		margin-left: 20px;
	}
}

.add-goal {
	@include icon("~@/assets/images/add-goal.svg", 16px);
}

.add-action-step {
	@include icon("~@/assets/images/add-action-step.svg", 16px);
}

.mark-as-closed {
	@include icon("~@/assets/images/mark-as-closed.svg", 16px);
}
</style>
