<script lang="ts">
import { defineComponent, PropType, computed } from "vue";
import { Task } from "@/types";

export default defineComponent({
	emits: ["close"],
	props: {
		task: {
			type: Object as PropType<Task | null>,
			default: null
		}
	},
	setup(props, ctx) {
		const opened = computed(() => props.task !== null);

		const beforeClose = () => {
			ctx.emit("close");
		};

		return {
			opened,
			beforeClose
		};
	}
});
</script>

<template>
	<div class="dialog">
		<el-dialog
			title="Task Resources"
			:model-value="opened"
			:width="700"
			:show-close="true"
			:before-close="beforeClose"
		>
			<div class="dialog-body">
				{{ task }}
			</div>

			<div class="dialog-footer">
				<el-button
					round
					@click="beforeClose"
				>
					Cancel
				</el-button>
			</div>
		</el-dialog>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.dialog {
	::v-deep(.el-dialog) {
		.el-dialog__body {
			padding: 0;
		}

		.el-dialog__title {
			color: $global-text-color;
			font-size: $global-large-font-size;
			font-weight: 500;
		}

		.el-dialog__close {
			font-weight: 1000;
			color: $global-text-color;
		}
	}

	.dialog-body {
		border-top: $global-border;
		border-bottom: $global-border;
		padding: 25px;
		max-height: 700px;
		overflow-y: auto;
	}

	.dialog-footer {
		padding: 25px;
		display: flex;
		justify-content: flex-end;

		.el-button {
			min-width: 155px;
			font-size: $global-font-size;
			font-weight: 400;
			color: $global-text-color;
			border: $global-border;
			padding: 0;
			height: 25px;
			min-height: 25px;
		}

		.el-button + .el-button {
			margin-left: 20px;
		}
	}
}
</style>
