<script lang="ts">
import { defineComponent, PropType, computed } from "vue";
import { TaskStatus } from "@/types";
import TaskStatusDisplay from "@/components/TaskStatusDisplay.vue";


export default defineComponent({
	components: { TaskStatusDisplay },
	props: {
		options: {
			type: Array as PropType<TaskStatus[]>,
			required: true
		},
		modelValue: {
			type: String as PropType<TaskStatus>,
			required: true
		}
	},
	emits: ["update:model-value"],
	setup(props) {
		const dropdownOptions = computed(() => props.options.filter(option => option !== props.modelValue));
		return {
			dropdownOptions
		};
	}
});
</script>

<template>
	<div class="task-status-select">
		<el-select
			size="mini"
			:model-value="modelValue"
			:popper-append-to-body="false"
			@update:model-value="$emit('update:model-value', $event)"
		>
			<template #prefix>
				<TaskStatusDisplay
					:status="modelValue"
					icon-only
					small
				/>
			</template>
			<el-option
				v-for="item in dropdownOptions"
				:key="item"
				:value="item"
			>
				<TaskStatusDisplay
					:status="item"
					small
				/>
			</el-option>
		</el-select>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.task-status-select {
	display: inline-block;

	::v-deep(.el-select) {
		.el-input__inner {
			font-size: $global-font-size;
			font-weight: 400;
			color: #333;
		}

		.el-input__prefix {
			margin-left: 5px;
		}

		.el-select-dropdown__item {
			padding: 0 10px;
			font-size: $global-small-font-size;
			font-weight: 400;
			color: #333;
		}
	}
}
</style>
