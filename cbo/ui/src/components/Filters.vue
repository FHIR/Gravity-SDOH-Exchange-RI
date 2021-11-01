<script lang="ts">
import { defineComponent, ref } from "vue";

export default defineComponent({
	name: "Filters",
	emits: ["search", "filter"],
	setup(_, { emit }) {
		const search = ref<string>("");
		const filter = ref<string>("");

		const handleSearch = () => {
			emit("search", search.value);
		};
		const handleFilter = () => {
			emit("filter", filter.value);
		};

		return {
			search,
			filter,
			handleSearch,
			handleFilter
		};
	}
});
</script>

<template>
	<div class="filters">
		<div>
			<label>Search:</label>
			<el-input
				v-model="search"
				placeholder="Search..."
				size="mini"
				@input="handleSearch"
			/>
		</div>

		<div>
			<label>Filter by:</label>
			<el-input
				v-model="filter"
				placeholder="Filter..."
				size="mini"
				@input="handleFilter"
			/>
		</div>
		<div class="actions">
			<slot></slot>
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.filters {
	display: flex;
	flex-shrink: 0;
	width: 100%;
	padding: 20px;
	background-color: $global-background;
	box-shadow: $global-box-shadow;
	border-radius: 5px;

	label {
		margin-right: 10px;
		font-size: $global-font-size;
		font-weight: $global-font-weight-normal;
	}

	::v-deep(.el-input) {
		width: 350px;
		margin-right: 50px;
	}

	.actions {
		margin-left: auto;
	}
}
</style>
