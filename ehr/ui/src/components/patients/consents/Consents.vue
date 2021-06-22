<script lang="ts">
import { defineComponent, ref } from "vue";
import NewConsentDialog from "./NewConsentDialog.vue";
import { Consent } from "@/types";
import { getConsents, getConsentAttachment } from "@/api";


export default defineComponent({
	components: { NewConsentDialog },
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

		const attachmentLoading = ref(false);

		const viewAttachment = async (consentId: string) => {
			try {
				attachmentLoading.value = true;
				const blob = await getConsentAttachment(consentId);
				// Potential memory leak
				const objectUrl = URL.createObjectURL(blob);
				const newWin = window.open();
				if (newWin) {
					newWin.location.href = objectUrl;
				}
			} finally {
				attachmentLoading.value = false;
			}
		};


		return {
			dialogOpened,
			consentsLoading,
			consents,
			addNewConsent,
			onConsentCreated,
			viewAttachment,
			attachmentLoading
		};
	}
});
</script>

<template>
	<div
		v-loading="consentsLoading"
		class="consents"
		:class="{ 'wait-cursor': attachmentLoading }"
	>
		<div
			v-if="consents.length === 0"
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

	.empty-state {
		display: flex;
		flex-direction: column;
		height: 340px;
		align-items: center;
		justify-content: center;

		.text {
			font-size: 48px;
			font-weight: $global-font-weight-normal;
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
			font-size: $global-large-font-size;
			font-weight: $global-font-weight-medium;
		}
	}
}
</style>
