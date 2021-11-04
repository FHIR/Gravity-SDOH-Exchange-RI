<script lang="ts">
import { defineComponent, onMounted, ref, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
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
		const showLoader = computed<boolean>(() => ServersModule.isLoading);
		const serverInEdit = ref<Server | null>(null);
		const serverCreateVisible = ref<boolean>(false);
		const serverFromOutside = ref<{
			serverName: string,
			fhirServerUrl: string,
			authServerUrl: string,
			clientId: string
		} | {}>({});
		const route = useRoute();
		const router = useRouter();

		onMounted(async () => {
			handleParamsFromOutside();
			await ServersModule.getServers();
		});
		const handleParamsFromOutside = () => {
			const { query } = route;

			if (query.serverName && query.fhirServerUrl && query.authServerUrl && query.clientId) {
				serverFromOutside.value = { ...query };
				serverCreateVisible.value = true;
				router.replace({
					query: undefined
				});
			}
		};

		const editServer = (server: Server) => {
			serverInEdit.value = server;
		};
		const closeEditDialog = () => {
			serverInEdit.value = null;
		};
		const closeCreateDialog = () => {
			serverCreateVisible.value = false;
			serverFromOutside.value = {};
		};

		return {
			data,
			serverInEdit,
			editServer,
			closeEditDialog,
			serverCreateVisible,
			closeCreateDialog,
			serverFromOutside,
			showLoader
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
					:loading="showLoader"
					@server-name-click="editServer"
				/>
			</TableCard>
		</div>

		<ServerEditDialog
			:server="serverInEdit"
			@close="closeEditDialog"
		/>
		<ServerCreateDialog
			:server="serverFromOutside"
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
		max-height: calc(100% - 110px);
	}

	.filters {
		margin-bottom: 30px;
	}

	.table-card {
		flex-grow: 1;
	}
}
</style>
