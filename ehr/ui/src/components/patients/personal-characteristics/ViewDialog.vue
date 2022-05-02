<script lang="ts">
import { computed, defineComponent, PropType, ref, watch } from "vue";
import { TYPES, METHOD_TYPES, PersonalCharacteristic } from "@/types/personal-characteristics";
import { getPersonalCharacteristicsAttachment } from "@/api";

const addDetail = (value: string, detail?: string) => detail ? `${value} (${detail})` : value;

const format = (p: PersonalCharacteristic) => ({
	id: p.id,
	type: TYPES[p.type],
	performer: p.performer.display,
	method: addDetail(METHOD_TYPES[p.method], p.methodDetail),
	value: (p.value && addDetail(p.value.display, p.valueDetail)) || p.values?.map(v => v.display).join(", ") || "",
	description: p.description || "",
	detailedValue: p.detailedValues?.map(v => v.display).join(", ") || "",
});

export default defineComponent({
	props: {
		item: {
			type: Object as PropType<PersonalCharacteristic | null>,
			default: null
		}
	},
	emits: ["close"],
	setup(props, { emit }) {
		const onDialogClose = () => {
			emit("close");
		};

		const data = computed(() => props.item ? format(props.item) : null);

		type Attachment = "no" | "loading" | {
			url: string
		}

		const attachment = ref<Attachment>("no");

		const loadAttachment = async (id: string) => {
			attachment.value = "loading";
			const plain = await getPersonalCharacteristicsAttachment(id);
			const blob = new Blob([plain]);
			attachment.value = {
				url: URL.createObjectURL(blob)
			};
		};

		watch(() => props.item, item => {
			attachment.value = "no";
			if (item && item.hasAttachment) {
				loadAttachment(item.id);
			}
		});

		return {
			visible: computed(() => props.item !== null),
			data,
			onDialogClose,
			attachment,
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		:title="data?.type || ''"
		:width="700"
		:before-close="onDialogClose"
	>
		<el-form
			v-if="data"
			label-width="155px"
			label-position="left"
			size="mini"
		>
			<el-form-item label="Type">
				{{ data.type }}
			</el-form-item>

			<el-form-item label="Performer">
				{{ data.performer }}
			</el-form-item>

			<el-form-item label="Method">
				{{ data.method }}
			</el-form-item>

			<el-form-item label="Value">
				{{ data.value }}
			</el-form-item>

			<el-form-item
				v-if="data.description"
				label="Description"
			>
				{{ data.description }}
			</el-form-item>

			<el-form-item
				v-if="data.detailedValue"
				label="Detailed Value"
			>
				{{ data.detailedValue }}
			</el-form-item>

			<el-form-item
				v-if="attachment !== 'no'"
				label="Attachment"
			>
				<span v-if="attachment === 'loading'">loading...</span>
				<a
					v-else
					:href="attachment.url"
					download
				>
					download
				</a>
			</el-form-item>
		</el-form>

		<template #footer>
			<el-button
				round
				size="mini"
				@click="onDialogClose"
			>
				Close
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.form {
	.el-select {
		width: 100%;
	}
}
</style>
