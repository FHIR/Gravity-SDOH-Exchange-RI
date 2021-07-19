<script lang="ts">
import { defineComponent, PropType } from "vue";
import { Assessment } from "@/types";
import ExternalLink from "@/components/ExternalLink.vue";

export default defineComponent({
	components: { ExternalLink },
	props: {
		data: {
			type: Array as PropType<Assessment[]>,
			required: true
		}
	},
	emits: ["title-click", "open-concern"]
});
</script>

<template>
	<div
		class="table-wrapper"
	>
		<el-table :data="data">
			<el-table-column
				label="Assessment Name"
			>
				<template #default="scope">
					<el-button
						type="text"
						@click="$emit('title-click', scope.row)"
					>
						{{ scope.row.name }}
					</el-button>
				</template>
			</el-table-column>

			<el-table-column
				label="Assessment Date"
			>
				<template #default="scope">
					{{ $filters.formatDateTime(scope.row.date) }}
				</template>
			</el-table-column>

			<el-table-column
				label="Identified Health Concerns"
			>
				<template #default="scope">
					<ExternalLink
						v-for="concern in scope.row.healthConcerns"
						:key="concern.id"
						@click="$emit('open-concern', concern.id)"
					>
						{{ concern.display }}
					</ExternalLink>
				</template>
			</el-table-column>
			<el-table-column />
		</el-table>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.title {
	margin: 10px 20px 0;

	h3 {
		font-weight: $global-font-weight-medium;
		margin: 0;
	}
}

.table-wrapper {
	background-color: $global-background;
	border-radius: 5px;
	border: 1px solid $global-base-border-color;
	padding: 10px 20px;
	min-height: 130px;
}
</style>
