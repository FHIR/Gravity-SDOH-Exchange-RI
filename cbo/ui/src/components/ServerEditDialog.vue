<script lang="ts">
import { defineComponent, PropType, computed, reactive, ref } from "vue";
import { Server, UpdateServerPayload } from "@/types";
import { ServersModule } from "@/store/modules/servers";
import { showDefaultNotification } from "@/utils/utils";

export type FormModel = {
	name: string,
	url: string,
	authUrl: string,
	clientId: string
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
			formModel.name !== props.server?.name ||
			formModel.url !== props.server?.url ||
			formModel.authUrl !== props.server?.authUrl ||
			formModel.clientId !== props.server?.clientId
		));

		const onDialogOpen = () => {
			Object.assign(formModel, {
				name: props.server?.name,
				url: props.server?.url,
				authUrl: props.server?.authUrl,
				clientId: props.server?.clientId
			});
		};
		const onDialogClose = () => {
			formEl.value?.resetFields();
			ctx.emit("close");
		};
		const onFormSave = async () => {
			const payload: UpdateServerPayload = {
				id: props.server?.id || "",
				...formModel
			};
			saveInProgress.value = true;
			try {
				await formEl.value.validate();
				await ServersModule.updateServer(payload);
				onDialogClose();
			}
			catch (err) {
				console.log(err);
			}
			finally {
				saveInProgress.value = false;
				showDefaultNotification(`Server "${formModel.name}" has been successfully updated!`);
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

			<el-form-item label="Last Synchronization">
				{{ $filters.formatDateTime(new Date().toISOString()) }}
			</el-form-item>

			<el-divider />

			<el-form-item label="Access to Server">
				{{ $filters.formatDateTime(server?.accessUntil) }}
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
