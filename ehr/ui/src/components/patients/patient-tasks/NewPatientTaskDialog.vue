<script lang="ts">
import { defineComponent, ref } from "vue";

export default defineComponent({
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	emits: ["close"],
	setup(_, { emit }) {
		const saveInProgress = ref<boolean>(false);

		const onDialogClose = () => {
			// todo: reset form here
			emit("close");
		};
		const onDialogOpen = () => {
			// todo: fetch needed info here
		};
		const onFormSave = () => {};

		return {
			onDialogClose,
			onDialogOpen,
			saveInProgress,
			onFormSave
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="Add Patient Task"
		:width="700"
		destroy-on-close
		custom-class="patient-task-dialog"
		@close="onDialogClose"
		@open="onDialogOpen"
	>
		<div>
			form goes here
		</div>
		<template #footer>
			<el-button
				round
				size="mini"
				@click="onDialogClose"
			>
				Cancel
			</el-button>
			<el-button
				plain
				round
				type="primary"
				size="mini"
				:loading="saveInProgress"
				@click="onFormSave"
			>
				Create
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

</style>
