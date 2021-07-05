<script lang="ts">
import { defineComponent, ref, computed } from "vue";
import NewConsentDialog from "./NewConsentDialog.vue";
import { Consent } from "@/types";
import { getConsents, getConsentAttachment } from "@/api";
import NoItems from "@/components/patients/NoItems.vue";


export default defineComponent({
	components: { NoItems, NewConsentDialog },
	setup() {
		const consents = ref<Consent[]>([]);
		const consentsLoading = ref(true);

		getConsents().then(resp => {
			consentsLoading.value = false;
			consents.value = resp;
		});

		const dialogOpened = ref(false);

		const addNewConsent = () => {
			dialogOpened.value = true;
		};

		const onConsentCreated = (newOne: Consent) => {
			dialogOpened.value = false;
			consents.value = [...consents.value, newOne];
		};

		const viewAttachment = async (consentId: string) => {
			const newWin = window.open();
			if (newWin) {
				const blob = await getConsentAttachment(consentId);
				const objectUrl = URL.createObjectURL(blob);
				newWin.location.href = objectUrl;
				URL.revokeObjectURL(objectUrl);
			}
		};

		const existingConsentNames = computed<string[]>(() => consents.value.map(({ name }) => name));


		return {
			dialogOpened,
			consentsLoading,
			consents,
			addNewConsent,
			onConsentCreated,
			viewAttachment,
			existingConsentNames
		};
	}
});
</script>

<template>
	<div
		v-loading="consentsLoading"
		class="consents"
	>
		<NoItems
			v-if="consents.length === 0"
			message="No Consents Yet"
			button-label="Add Consent"
			@add-item="addNewConsent"
		/>

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

			<el-table :data="consents">
				<el-table-column label="Consent Name">
					<template #default="scope">
						<el-button
							type="text"
							@click="viewAttachment(scope.row.id)"
						>
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
						{{ $filters.formatDateTime(scope.row.consentDate) }}
					</template>
				</el-table-column>
			</el-table>
		</div>

		<NewConsentDialog
			v-model:opened="dialogOpened"
			:existing-consent-names="existingConsentNames"
			@consent-created="onConsentCreated"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.consents {
	min-height: 340px;

	&.wait-cursor * {
		cursor: wait;
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
			font-size: $global-large-font-size;
			font-weight: $global-font-weight-medium;
		}
	}
}
</style>
