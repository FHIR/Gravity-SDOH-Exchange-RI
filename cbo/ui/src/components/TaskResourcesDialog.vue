<script lang="ts">
import { defineComponent, PropType, computed, ref, watch } from "vue";
import { Resources } from "@/types";
import { getTaskResources } from "@/api";
import JsonViewer from "@/components/JsonViewer.vue";


const prettyPrint = (raw: string) => JSON.stringify(JSON.parse(raw), null, "\t");

const prepareData = (data: Resources) => [
	{ label: "Task", data: prettyPrint(data.task) },
	{ label: "Service Request", data: prettyPrint(data.serviceRequest) },
	{ label: "Requester", data: prettyPrint(data.requester) },
	{ label: "Patient", data: prettyPrint(data.patient) },
	{ label: "Consent", data: prettyPrint(data.consent) },
	{ label: "Conditions", data: JSON.stringify(data.conditions.map(str => JSON.parse(str)), null, "\t") },
	{ label: "Goals", data: JSON.stringify(data.goals.map(str => JSON.parse(str)), null, "\t") },
	{ label: "Procedures", data: JSON.stringify(data.procedures.map(str => JSON.parse(str)), null, "\t") }
];

export default defineComponent({
	components: { JsonViewer },
	props: {
		taskId: {
			type: String as PropType<string | null>,
			default: null
		}
	},
	emits: ["close"],
	setup(props, ctx) {
		const opened = computed(() => props.taskId !== null);

		const originalData = ref<Resources | null>(null);

		watch(() => props.taskId, async taskId => {
			originalData.value = null;
			if (taskId) {
				originalData.value = await getTaskResources(taskId);
			}
		}, { immediate: true });

		const data = computed(() => originalData.value ? prepareData(originalData.value) : null);

		const beforeClose = () => {
			ctx.emit("close");
		};

		return {
			opened,
			beforeClose,
			data
		};
	}
});
</script>

<template>
	<div class="dialog">
		<el-dialog
			title="Task Resources"
			:model-value="opened"
			:width="900"
			:show-close="true"
			:before-close="beforeClose"
		>
			<div
				v-loading="!data"
				class="dialog-body"
			>
				<div
					v-if="data"
					class="main"
				>
					<el-tabs
						tab-position="left"
						class="tabs"
					>
						<el-tab-pane
							v-for="item in data"
							:key="item.label"
							:label="item.label"
							:lazy="true"
							class="pane"
						>
							<JsonViewer :data="item.data" />
						</el-tab-pane>
					</el-tabs>
				</div>
			</div>

			<div class="dialog-footer">
				<el-button
					round
					@click="beforeClose"
				>
					Close
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
		height: 600px;
		overflow: hidden;
	}

	.main {
		width: 100%;
		height: 100%;
		overflow: hidden;
	}

	::v-deep(.el-tabs) {
		height: 100%;

		.el-tabs__nav-wrap {
			border-right: $global-border;

			&::after {
				display: none;
			}
		}

		.el-tabs__item {
			color: $global-text-color;
			font-weight: 400;

			&.is-active {
				color: $global-primary-color;
			}
		}

		.el-tabs__active-bar {
			background-color: $global-primary-color;
			width: 1px;
		}

		.el-tabs__content {
			height: 100%;
		}
	}

	.pane {
		height: 100%;
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
