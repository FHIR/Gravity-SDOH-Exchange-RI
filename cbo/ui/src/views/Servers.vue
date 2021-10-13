<script lang="ts">
import { defineComponent, onMounted, ref, computed } from "vue";
import MainHeader from "@/components/MainHeader.vue";
import Filters from "@/components/Filters.vue";
import TableCard from "@/components/TableCard.vue";
import ServerTable from "@/components/ServerTable.vue";
import ServerEditDialog from "@/components/ServerEditDialog.vue";
import ServerCreateDialog from "@/components/ServerCreateDialog.vue";
import { Server } from "@/types";
import { ServersModule } from "@/store/modules/servers";

export default defineComponent({
	name: "Servers",
	components: {
		ServerTable,
		TableCard,
		Filters,
		MainHeader,
		ServerEditDialog,
		ServerCreateDialog
	},
	setup() {
		const data = computed<Server[]>(() => ServersModule.servers);
		const serverInEdit = ref<Server | null>(null);
		const serverCreateVisible = ref<boolean>(false);

		onMounted(async () => {
			await ServersModule.getServers();
		});

		const editServer = (server: Server) => {
			serverInEdit.value = server;
		};
		const closeEditDialog = () => {
			serverInEdit.value = null;
		};
		const closeCreateDialog = () => {
			serverCreateVisible.value = false;
		};

		return {
			data,
			serverInEdit,
			editServer,
			closeEditDialog,
			serverCreateVisible,
			closeCreateDialog
		};
	}
});
</script>

<template>
	<div class="page-container">
		<MainHeader />
		<div class="page-body">
			<Filters>
				<el-button
					type="primary"
					round
					plain
					size="mini"
					@click="serverCreateVisible = true"
				>
					Add New Server
				</el-button>
			</Filters>
			<TableCard>
				<ServerTable
					:data="data"
					@server-name-click="editServer"
				/>
			</TableCard>
		</div>

		<ServerEditDialog
			:server="serverInEdit"
			@close="closeEditDialog"
		/>
		<ServerCreateDialog
			:visible="serverCreateVisible"
			@close="closeCreateDialog"
		/>
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
