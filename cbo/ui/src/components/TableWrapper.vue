<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "TableWrapper"
});
</script>

<template>
	<div class="table-wrapper">
		<slot></slot>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.table-wrapper {
	height: 100%;

	::v-deep(.el-table) {
		height: 100%;
		display: flex;
		flex-direction: column;
		font-size: $global-medium-font-size;
		font-weight: $global-font-weight-normal;
		color: $global-text-color;

		.el-table__header-wrapper {
			flex-shrink: 0;
		}

		.el-table__body-wrapper {
			flex: 1;
			overflow: overlay;
		}

		&::before {
			display: none;
		}

		.cell {
			overflow-x: hidden;
			white-space: nowrap;
		}

		.el-table__header {
			th {
				padding: 0;
				height: 65px;
				border: none;
				font-weight: $global-font-weight-normal;
				color: $grey;
			}
		}
	}

	::v-deep(.el-table__body) {
		border-spacing: 0 10px;
		margin: -9px 0 -9px 0;

		td {
			padding: 0;
			height: 50px;
			border-top: $global-border;
			border-bottom: $global-border;

			&:first-child {
				border-left: $global-border;
				border-top-left-radius: 5px;
				border-bottom-left-radius: 5px;
			}

			&:last-child {
				border-right: $global-border;
				border-top-right-radius: 5px;
				border-bottom-right-radius: 5px;
			}
		}

		.clickable-text {
			color: $global-primary-color;
			text-decoration: underline;
			font-weight: $global-font-weight-normal;
			font-size: $global-medium-font-size;
			cursor: pointer;
		}

		.cell:empty::after {
			content: "--";
		}
	}
}
</style>
