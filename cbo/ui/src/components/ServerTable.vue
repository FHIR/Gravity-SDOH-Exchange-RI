<script lang="ts">
import { defineComponent, PropType, computed } from "vue";
import { Server } from "@/types";
import TableWrapper from "@/components/TableWrapper.vue";

type DisplayFields = {
	id: number,
	serverName: string,
	fhirServerUrl: string,
	authServerUrl: string,
	clientId: string,
	clientSecret: string,
	lastSyncDate: string
}

export default defineComponent({
	name: "ServerTable",
	components: {
		TableWrapper
	},
	props: {
		data: {
			type: Array as PropType<Server[]>,
			required: true
		}
	},
	emits: ["server-name-click"],
	setup(props, ctx) {
		const tableData = computed<DisplayFields[]>(() => props.data.map((server: Server) => ({
			id: server.id,
			serverName: server.serverName,
			fhirServerUrl: server.fhirServerUrl,
			authServerUrl: server.authServerUrl,
			clientId: server.clientId,
			clientSecret: server.clientSecret,
			lastSyncDate: server.lastSyncDate
		})));

		const handleNameClick = (id: number) => {
			const server: Server = props.data.find(server => server.id === id)!;
			ctx.emit("server-name-click", server);
		};

		return {
			tableData,
			handleNameClick
		};
	}
});
</script>

<template>
	<TableWrapper>
		<el-table :data="tableData">
			<el-table-column label="Server Name">
				<template #default="{ row }">
					<div
						class="clickable-text"
						@click="handleNameClick(row.id)"
					>
						<span class="name">
							{{ row.serverName }}
						</span>
					</div>
				</template>
			</el-table-column>

			<el-table-column
				prop="fhirServerUrl"
				label="FHIR Server URL"
			/>

			<el-table-column
				prop="authServerUrl"
				label="Authorization Server URL"
			/>

			<el-table-column
				prop="clientId"
				label="Client ID"
			/>

			<el-table-column
				prop="clientSecret"
				label="Client Secret"
			/>

			<el-table-column label="Last Synchronization Date">
				<template #default="scope">
					{{ $filters.formatDateTime(scope.row.lastSyncDate) }}
				</template>
			</el-table-column>
		</el-table>
	</TableWrapper>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.sync-wrapper {
	display: flex;
	align-items: center;

	.sync-icon {
		background-image: url("~@/assets/images/sync-icon.svg");
		width: 16px;
		height: 16px;
		margin-right: 5px;
	}
}
</style>
