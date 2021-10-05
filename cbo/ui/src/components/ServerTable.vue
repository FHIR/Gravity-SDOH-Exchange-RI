<script lang="ts">
import { defineComponent, PropType, computed } from "vue";
import { Server } from "@/types";
import TableWrapper from "@/components/TableWrapper.vue";

type DisplayFields = {
	name: string,
	url: string,
	authUrl: string,
	clientId: string,
	lastSync: string,
	accessUntil: string
}

export default defineComponent({
	name: "ServeTable",
	components: {
		TableWrapper
	},
	props: {
		data: {
			type: Array as PropType<Server[]>,
			required: true
		}
	},
	setup(props) {
		const tableData = computed<DisplayFields[]>(() => props.data.map((server: Server) => ({
			name: server.name,
			url: server.url,
			authUrl: server.authUrl,
			clientId: server.clientId,
			lastSync: new Date().toISOString(),
			accessUntil: new Date().toISOString()
		})));

		return {
			tableData
		};
	}
});
</script>

<template>
	<TableWrapper>
		<el-table :data="tableData">
			<el-table-column
				prop="name"
				label="Server Name"
			/>

			<el-table-column
				prop="url"
				label="FHIR Server URL"
			/>

			<el-table-column
				prop="authUrl"
				label="Authorization Server URL"
			/>

			<el-table-column
				prop="clientId"
				label="Client ID"
			/>

			<el-table-column label="Last Synchronization Date">
				<template #default="scope">
					{{ $filters.formatDateTime(scope.row.lastSync) }}
				</template>
			</el-table-column>

			<el-table-column label="Access to Server">
				<template #default="scope">
					<div class="sync-wrapper">
						<div class="sync-icon"></div>
						Until {{ $filters.formatDateTime(scope.row.accessUntil) }}
					</div>
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
