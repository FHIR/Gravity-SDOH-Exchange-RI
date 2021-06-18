<script lang="ts">
import { defineComponent, PropType, ref } from "vue";


export default defineComponent({
	props: {
		value: {
			type: Object as PropType<File | null>,
			default: null
		},
		accept: {
			type: String,
			default: ""
		},
		disabled: {
			type: Boolean,
			default: false
		}
	},
	emits: ["update:value"],
	setup(props, ctx) {
		const inputRef = ref<HTMLInputElement>(null as unknown as HTMLInputElement);

		const activate = () => {
			!props.disabled && inputRef.value.click();
		};

		const remove = () => {
			!props.disabled && ctx.emit("update:value", null);
		};

		const onChange = () => {
			const files = inputRef.value.files || [];
			if (files.length === 0) {
				ctx.emit("update:value", null);
			} else {
				ctx.emit("update:value", files[0]);
			}
			inputRef.value.value = "";
		};

		return {
			inputRef,
			activate,
			remove,
			onChange
		};
	}
});
</script>

<template>
	<span class="file-input">
		<span
			class="cover"
			:class="{ disabled }"
		>
			<template
				v-if="value"
			>
				<span
					class="file-name"
					@click="activate"
				>
					{{ value.name }}
				</span>
				<span
					class="el-icon-delete"
					@click="remove"
				>
				</span>
			</template>

			<span
				v-else
				@click="activate"
			>
				<i class="el-icon-circle-plus-outline"></i>
				Choose file
			</span>
		</span>

		<input
			ref="inputRef"
			type="file"
			:accept="accept"
			@change="onChange"
		>
	</span>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.file-input {
	input {
		display: none;
	}

	.cover {
		cursor: pointer;

		&.disabled {
			color: $global-muted-color;
			cursor: not-allowed;
		}
	}

	.file-name {
		margin-right: 5px;
	}
}
</style>
