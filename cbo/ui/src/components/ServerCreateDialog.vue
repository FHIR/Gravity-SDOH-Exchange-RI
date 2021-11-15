<script lang="ts">
import { defineComponent, computed, reactive, ref, PropType } from "vue";
import { NewServerPayload } from "@/types";
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
	name: "ServerCreateDialog",
	props: {
		server: {
			type: Object as PropType<{
				serverName: string,
				fhirServerUrl: string,
				authServerUrl: string,
				clientId: string
			} | {}>,
			default: {}
		},
		visible: {
			type: Boolean,
			default: false
		}
	},
	emits: ["close"],
	setup(props, ctx) {
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
			formModel.serverName !== "" ||
			formModel.fhirServerUrl !== "" ||
			formModel.authServerUrl !== "" ||
			formModel.clientId !== "" ||
			formModel.clientSecret !== ""
		));

		const onDialogClose = () => {
			formEl.value?.resetFields();
			ctx.emit("close");
		};
		const onDialogOpened = () => {
			if (props.server) {
				Object.assign(formModel, props.server);
			}
		};
		const onFormSave = async () => {
			const payload: NewServerPayload = { ...formModel };
			saveInProgress.value = true;
			try {
				await formEl.value.validate();
				await ServersModule.createServer(payload);
				showDefaultNotification(`Server "${formModel.serverName}" has been successfully created!`);
				onDialogClose();
			}
			catch (err) {
				showDefaultNotification("Server not found. Check your network connection or your credentials!");
				console.log(err);
			}
			finally {
				saveInProgress.value = false;
			}
		};

		return {
			formEl,
			onDialogClose,
			formModel,
			validationRules,
			onFormSave,
			saveInProgress,
			hasChanges,
			onDialogOpened
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="New Server"
		:width="700"
		append-to-body
		destroy-on-close
		custom-class="create-server-dialog"
		@opened="onDialogOpened"
		@close="onDialogClose"
	>
		<el-form
			ref="formEl"
			:model="formModel"
			:rules="validationRules"
			label-width="185px"
			label-position="left"
			size="mini"
			class="create-server-form"
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
				Add New Server
			</el-button>
		</template>
	</el-dialog>
</template>
