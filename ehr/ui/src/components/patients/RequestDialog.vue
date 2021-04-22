<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "RequestDialog",
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	emits: ["close"]
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="New Service Request/Task"
		:width="700"
		:append-to-body="true"
		custom-class="request-dialog"
		@close="$emit('close')"
	>
		Form goes here
		<template #footer>
			<el-button
				round
				size="mini"
				@click="$emit('close')"
			>
				Cancel
			</el-button>
			<el-button
				plain
				round
				type="primary"
				size="mini"
			>
				Create & Send
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.request-dialog {
	.el-dialog__footer {
		.el-button {
			width: 155px;
		}
	}
}
</style>
