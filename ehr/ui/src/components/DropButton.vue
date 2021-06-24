<script lang="ts">
import { defineComponent, PropType } from "vue";

type Item = {
	id: string,
	label: string,
	iconSrc: string
}

export default defineComponent({
	props: {
		items: {
			type: Array as PropType<Item[]>,
			required: true
		},
		label: {
			type: String,
			required: true
		}
	},
	emits: ["click", "item-click"]
});
</script>

<template>
	<span class="drop-button">
		<el-dropdown
			split-button
			trigger="click"
			popper-class="drop-button-dropdown"
			@click="$emit('click')"
			@command="$emit('item-click', $event)"
		>
			{{ label }}
			<template #dropdown>
				<el-dropdown-menu>
					<el-dropdown-item
						v-for="item in items"
						:key="item.id"
						:command="item.id"
					>
						<span
							class="icon"
							:style="`background-image: url(${item.iconSrc})`"
						></span>
						<span class="label">{{ item.label }}</span>
					</el-dropdown-item>
				</el-dropdown-menu>
			</template>
		</el-dropdown>
	</span>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
$chevron-width: 30px;

.drop-button {
	margin-left: 20px;

	::v-deep(.el-dropdown) .el-button-group {
		display: inline-flex;
	}

	::v-deep(.el-button) {
		padding: 0;
		min-height: 25px;
		border-radius: 25px;
		border: 1px solid $global-primary-color;

		&:first-child {
			width: 173px - $chevron-width;
			border-right: none;
		}

		&:last-child {
			width: $chevron-width;
			border-left: none;

			&::before {
				display: none;
			}
		}
	}
}
</style>

<style lang="scss">
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.drop-button-dropdown {
	border-radius: 5px !important;
	border: 1px solid $silver !important;
	box-shadow: $global-box-shadow;

	.el-popper__arrow {
		left: 152px !important;
	}

	.el-popper__arrow::before {
		border: 1px solid $silver !important;
	}

	.el-dropdown-menu {
		padding: 8px 0;
	}

	.el-dropdown-menu__item {
		padding: 0 10px;
		height: 25px;
		display: flex;
		align-items: center;
		width: 172px;
		font-size: $global-small-font-size;
		font-weight: $global-font-weight-normal;

		&:hover {
			background-color: transparentize($silver, 0.85) !important;
			color: $global-text-color !important;
		}

		.icon {
			@include icon(null, 16px, 16px);
		}

		.icon + .label {
			margin-left: 6px;
		}
	}
}
</style>
