<script lang="ts">
import { defineComponent, PropType, computed, reactive, ref } from "vue";
import { Server, UpdateServerPayload } from "@/types";
import { ServersModule } from "@/store/modules/servers";
import { showDefaultNotification } from "@/utils";

export type FormModel = {
	serverName: string,
	fhirServerUrl: string,
	authServerUrl: string,
	clientId: string,
	clientSecret: string
}

export default defineComponent({
	name: "ServerEditDialog",
	props: {
		server: {
			type: Object as PropType<Server | null>,
			default: null
		}
	},
	emits: ["close"],
	setup(props, ctx) {
		const visible = computed<boolean>(() => props.server !== null);
		const formEl = ref<any>(null);
		const formModel = reactive<FormModel>({
			serverName: "",
			fhirServerUrl: "",
			authServerUrl: "",
			clientId: "",
			clientSecret: ""
		});
		const validationRules = ref({
			serverName: [{
				required: true,
				message: "This field is required"
			}, {
				max: 64,
				message: "Length should be up to 64"
			}],
			fhirServerUrl: [{ required: true, message: "This field is required" }],
			authServerUrl: [{ required: true, message: "This field is required" }],
			clientId: [{ required: true, message: "This field is required" }],
			clientSecret: [{ required: true, message: "This field is required" }]
		});
		const saveInProgress = ref<boolean>(false);
		const hasChanges = computed<boolean>(() => (
			formModel.serverName !== props.server?.serverName ||
			formModel.fhirServerUrl !== props.server?.fhirServerUrl ||
			formModel.authServerUrl !== props.server?.authServerUrl ||
			formModel.clientId !== props.server?.clientId ||
			formModel.clientSecret !== props.server?.clientSecret
		));

		const onDialogOpen = () => {
			Object.assign(formModel, {
				serverName: props.server?.serverName,
				fhirServerUrl: props.server?.fhirServerUrl,
				authServerUrl: props.server?.authServerUrl,
				clientId: props.server?.clientId,
				clientSecret: props.server?.clientSecret
			});
		};
		const onDialogClose = () => {
			formEl.value?.resetFields();
			ctx.emit("close");
		};
		const onFormSave = async () => {
			const payload: UpdateServerPayload = {
				id: props.server?.id || 0,
				...formModel
			};
			saveInProgress.value = true;
			try {
				await formEl.value.validate();
				await ServersModule.updateServer(payload);
				showDefaultNotification(`Server "${formModel.serverName}" has been successfully updated!`);
				onDialogClose();
			}
			catch (err) {
				console.log(err);
			}
			finally {
				saveInProgress.value = false;
			}
		};

		return {
			visible,
			formEl,
			onDialogOpen,
			onDialogClose,
			formModel,
			validationRules,
			onFormSave,
			saveInProgress,
			hasChanges
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="Server Data"
		:width="700"
		append-to-body
		destroy-on-close
		custom-class="edit-server-dialog"
		@close="onDialogClose"
		@opened="onDialogOpen"
	>
		<el-form
			ref="formEl"
			:model="formModel"
			:rules="validationRules"
			label-width="185px"
			label-position="left"
			size="mini"
			class="edit-server-form"
		>
			<el-form-item
				prop="serverName"
				label="Server Name"
			>
				<el-input v-model="formModel.serverName" />
			</el-form-item>

			<el-form-item
				prop="fhirServerUrl"
				label="FHIR Server URL"
			>
				<el-input v-model="formModel.fhirServerUrl" />
			</el-form-item>

			<el-form-item
				prop="authServerUrl"
				label="Authorization Server URL"
			>
				<el-input v-model="formModel.authServerUrl" />
			</el-form-item>

			<el-form-item
				prop="clientId"
				label="Client ID"
			>
				<el-input v-model="formModel.clientId" />
			</el-form-item>

			<el-form-item
				prop="clientSecret"
				label="Client Secret"
			>
				<el-input v-model="formModel.clientSecret" />
			</el-form-item>

			<el-form-item label="Last Synchronization">
				{{ $filters.formatDateTime(server?.lastSyncDate) }}
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button
				round
				plain
				size="mini"
				@click="onDialogClose"
			>
				Cancel
			</el-button>
			<el-button
				type="primary"
				round
				plain
				size="mini"
				:loading="saveInProgress"
				:disabled="!hasChanges"
				@click="onFormSave"
			>
				Save Changes
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.edit-server-form {
	.el-divider {
		margin: 20px 0;
	}
}
</style>
