<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "ActionButton",
	props: {
		iconClass: {
			type: String,
			required: true
		},
		label: {
			type: String,
			required: true
		}
	},
	emits: ["clicked"]
});
</script>

<template>
	<button
		class="action-button"
		@click="$emit('clicked')"
	>
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

	.icon {
		flex-shrink: 0;
	}

	.label {
		font-size: $global-font-size;
		line-height: 20px;
		margin-left: 5px;
		display: none;
		overflow: hidden;
	}

	&:hover {
		background-color: $global-background;
		color: $global-text-color;
		border-color: $global-primary-color;
		max-width: fit-content;

		@include grow-animation();

		.label {
			display: inline-block;
		}
	}

	+ .action-button {
		margin-left: 20px;
	}

	&[disabled] {
		opacity: 0.5;

		&:hover {
			border-color: $global-base-border-color;
		}
	}
}

.add-goal {
	@include icon("~@/assets/images/add-goal.svg", 20px);
}

.add-action-step {
	@include icon("~@/assets/images/add-action-step.svg", 20px);
}

.mark-as-closed {
	@include icon("~@/assets/images/mark-as-closed.svg", 21px);
}

.icon-promote {
	@include icon("~@/assets/images/concern-promote.svg", 17px, 20px);
}

.icon-resolved {
	@include icon("~@/assets/images/concern-resolved.svg", 22px, 19px);
}

.icon-remove {
	@include icon("~@/assets/images/concern-remove.svg", 20px, 19px);
}

.mark-as-completed {
	@include icon("~@/assets/images/goal-mark-as-completed.svg", 18px, 20px);
}

.remove-goal {
	@include icon("~@/assets/images/goal-remove.svg", 21px, 20px);
}

.add-target {
	@include icon("~@/assets/images/goal-add-target.svg", 20px);
}
</style>
