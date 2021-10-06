<script lang="ts">
import { defineComponent, PropType, computed } from "vue";
import { Server } from "@/types";

export default defineComponent({
	name: "ServerEditDialog",
	props: {
		server: {
			type: Object as PropType<Server | null>,
			default: null
		}
	},
	emits: ["close"],
	setup(props, ctx) {
		const visible = computed<boolean>(() => props.server !== null);

		const onDialogClose = () => {
			ctx.emit("close");
		};

		return {
			visible,
			onDialogClose
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="Server Data"
		:width="700"
		append-to-body
		destroy-on-close
		custom-class="edit-server-dialog"
		@close="onDialogClose"
	>
		{{ server }}
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

</style>
