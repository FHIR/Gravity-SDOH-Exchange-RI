<script lang="ts">
import { defineComponent, computed, reactive, ref } from "vue";
import { NewServerPayload } from "@/types";
import { ServersModule } from "@/store/modules/servers";

export type FormModel = {
	name: string,
	url: string,
	authUrl: string,
	clientId: string
}

export default defineComponent({
	name: "ServerCreateDialog",
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	emits: ["close"],
	setup(props, ctx) {
		const formEl = ref<any>(null);
		const formModel = reactive<FormModel>({
			name: "",
			url: "",
			authUrl: "",
			clientId: ""
		});
		const validationRules = ref({
			name: [{ required: true, message: "This field is required" }],
			url: [{ required: true, message: "This field is required" }],
			authUrl: [{ required: true, message: "This field is required" }],
			clientId: [{ required: true, message: "This field is required" }]
		});
		const saveInProgress = ref<boolean>(false);
		const hasChanges = computed<boolean>(() => (
			formModel.name !== "" ||
			formModel.url !== "" ||
			formModel.authUrl !== "" ||
			formModel.clientId !== ""
		));

		const onDialogClose = () => {
			formEl.value?.resetFields();
			ctx.emit("close");
		};
		const onFormSave = async () => {
			const payload: NewServerPayload = {
				...formModel
			};
			saveInProgress.value = true;
			try {
				await formEl.value.validate();
				await ServersModule.createServer(payload);
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
			formEl,
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
		title="New Server"
		:width="700"
		append-to-body
		destroy-on-close
		custom-class="create-server-dialog"
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
				prop="name"
				label="Server Name"
			>
				<el-input v-model="formModel.name" />
			</el-form-item>

			<el-form-item
				prop="url"
				label="FHIR Server URL"
			>
				<el-input v-model="formModel.url" />
			</el-form-item>

			<el-form-item
				prop="authUrl"
				label="Authorization Server URL"
			>
				<el-input v-model="formModel.authUrl" />
			</el-form-item>

			<el-form-item
				prop="clientId"
				label="Client ID"
			>
				<el-input v-model="formModel.clientId" />
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
