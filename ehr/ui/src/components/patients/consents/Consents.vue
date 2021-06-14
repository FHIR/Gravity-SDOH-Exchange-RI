<script lang="ts">
import { computed, defineComponent, ref } from "vue";
import NewConsentDialog from "./NewConsentDialog.vue";


type Consent = {
	name: string,
	status: string,
	scope: string,
	category: string,
	organization: string,
	date: string
}

export default defineComponent({
	components: { NewConsentDialog },
	setup() {
		const foo = {
			name: "Yes",
			status: "Active",
			scope: "Privacy Consent",
			category: "IDSCL",
			organization: "Provider/EHR",
			date: "May 18, 2021, 10:00 AM"
		};

		const data = [foo, foo, foo];

		const dialogOpened = ref(false);

		const addNewConsent = () => {
			dialogOpened.value = true;
		};

		return {
			dialogOpened,
			data,
			addNewConsent
		};
	}
});
</script>

<template>
	<div
		class="consents"
	>
		<div
			v-if="data.length === 0"
			class="empty-state"
		>
			<div class="text">
				No Consents Added Yet
			</div>
			<el-button
				plain
				round
				type="primary"
				size="mini"
				@click="addNewConsent"
			>
				Add New Consent
			</el-button>
		</div>

		<div
			v-else
			class="table-wrap"
		>
			<div class="table-header">
				<span class="title">
					Valid Consents
				</span>
				<el-button
					plain
					round
					type="primary"
					size="mini"
					@click="addNewConsent"
				>
					Add New Consent
				</el-button>
			</div>

			<el-table :data="data">
				<el-table-column label="Consent Name">
					<template #default="scope">
						<el-button type="text">
							{{ scope.row.name }}
						</el-button>
					</template>
				</el-table-column>

				<el-table-column label="Status">
					<template #default="scope">
						{{ scope.row.status }}
					</template>
				</el-table-column>

				<el-table-column label="Scope">
					<template #default="scope">
						{{ scope.row.scope }}
					</template>
				</el-table-column>

				<el-table-column label="Category">
					<template #default="scope">
						{{ scope.row.category }}
					</template>
				</el-table-column>

				<el-table-column label="Organization">
					<template #default="scope">
						{{ scope.row.organization }}
					</template>
				</el-table-column>

				<el-table-column label="Consent Date">
					<template #default="scope">
						{{ scope.row.date }}
					</template>
				</el-table-column>

				<el-table-column label="Actions">
				</el-table-column>
			</el-table>
		</div>

		<NewConsentDialog
			v-model:opened="dialogOpened"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.consents {
	min-height: 340px;

	.empty-state {
		display: flex;
		flex-direction: column;
		height: 340px;
		align-items: center;
		justify-content: center;

		.text {
			font-size: 48px;
			font-weight: 400;
			color: $whisper;
			margin-bottom: 50px;
		}
	}

	.table-wrap {
		border: 1px solid $global-base-border-color;
		border-radius: 5px;
		padding: 20px;
	}

	.table-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding-left: 20px;

		.title {
			font-size: 18px;
			font-weight: 500;
		}
	}
}
</style>
