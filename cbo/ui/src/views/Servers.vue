<script lang="ts">
import { defineComponent, onMounted, ref } from "vue";
import MainHeader from "@/components/MainHeader.vue";
import Filters from "@/components/Filters.vue";
import TableCard from "@/components/TableCard.vue";
import ServeTable from "@/components/ServerTable.vue";
import { Server } from "@/types";
import { getServers } from "@/api";

export default defineComponent({
	name: "Servers",
	components: {
		ServeTable,
		TableCard,
		Filters,
		MainHeader
	},
	setup() {
		const data = ref<Server[]>([]);

		onMounted(async () => {
			const res = await getServers();
			data.value = [...res];
		});

		return {
			data
		};
	}
});
</script>

<template>
	<div class="page-container">
		<MainHeader />
		<div class="page-body">
			<Filters />
			<TableCard>
				<ServeTable :data="data" />
			</TableCard>
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.page-container {
	display: flex;
	flex-direction: column;
	height: 100%;

	.page-body {
		display: flex;
		flex-direction: column;
		flex-grow: 1;
		padding: 0 45px 45px;
	}

	.filters {
		margin-bottom: 30px;
	}

	.table-card {
		flex-grow: 1;
	}
}
</style>
