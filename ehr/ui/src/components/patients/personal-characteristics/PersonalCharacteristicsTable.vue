<script lang="ts">
import { METHOD_TYPES, PersonalCharacteristic, TYPES } from "@/types/personal-characteristics";
import { computed, defineComponent, PropType } from "vue";

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

export default defineComponent( {
	props: {
		data: {
			type: Array as PropType<PersonalCharacteristic[]>,
			default: () => []
		}
	},
	emits: ["add-item", "item-clicked", "view-resources"],
	setup(props) {
		return {
			tableData: computed(() => props.data.map(format)),
		};
	}
});

</script>
<template>
	<div class="table-wrapper">
		<div class="title">
			<h3>
				Personal Characteristics
			</h3>
			<el-button
				plain
				round
				type="primary"
				size="mini"
				@click="$emit('add-item')"
			>
				Add Personal Characteristics
			</el-button>
		</div>
		<el-table
			ref="tableEl"
			:data="tableData"
		>
			<el-table-column
				label="Type"
				:width="200"
			>
				<template #default="scope">
					<el-button
						type="text"
						@click="$emit('item-clicked', scope?.row?.id)"
					>
						{{ scope?.row?.type }}
					</el-button>
				</template>
			</el-table-column>

			<el-table-column
				label="Performer"
				:width="200"
			>
				<template #default="scope">
					<div class="cell-text">
						{{ scope?.row?.performer }}
					</div>
				</template>
			</el-table-column>

			<el-table-column
				label="Method"
				:width="200"
			>
				<template #default="scope">
					<div class="cell-text">
						{{ scope?.row?.method }}
					</div>
				</template>
			</el-table-column>

			<el-table-column
				label="Value"
			>
				<template #default="scope">
					<div class="cell-text">
						{{ scope?.row?.value }}
					</div>
				</template>
			</el-table-column>

			<el-table-column
				label="Description"
			>
				<template #default="scope">
					<div class="cell-text">
						{{ scope?.row?.description }}
					</div>
				</template>
			</el-table-column>

			<el-table-column
				label="Detailed Value"
			>
				<template #default="scope">
					<div class="cell-text">
						{{ scope?.row?.detailedValue }}
					</div>
				</template>
			</el-table-column>

			<el-table-column
				label="Resources"
				:width="140"
			>
				<template #default="scope">
					<el-button
						type="text"
						@click="$emit('view-resources', scope?.row?.id)"
					>
						view resources
					</el-button>
				</template>
			</el-table-column>
		</el-table>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.title {
	margin-top: 10px;
	display: flex;
	justify-content: space-between;

	h3 {
		font-weight: $global-font-weight-medium;
		margin: 0 0 0 20px;
	}
}

.table-wrapper {
	background-color: $global-background;
	border-radius: 5px;
	border: 1px solid $global-base-border-color;
	padding: 10px 20px;
	min-height: 130px;

	+ .table-wrapper {
		margin-top: 30px;
	}
}

.icon-link {
	position: relative;
	left: 7px;
	cursor: pointer;

	@include icon("~@/assets/images/link.svg", 14px, 14px);
}

.cell-wrapper {
	display: flex;
	min-width: 60%;

	.icon-link {
		min-height: 14px;
		min-width: 14px;
	}
}

.cell-text {
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
}
</style>
