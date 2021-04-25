<script lang="ts">
import { defineComponent, reactive, ref, onMounted } from "vue";
import { getGoals, getConditions, getOrganizations } from "@/api";
import { Condition, Goal, newTaskPayload, Organization } from "@/types";
import _ from "@/vendors/lodash";
import { TasksModule } from "@/store/modules/tasks";

export default defineComponent({
	name: "RequestDialog",
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	emits: ["close"],
	setup(props, { emit }) {
		//todo: use serviceRequestCategory type as value
		const categoryOptions = reactive([{
			value: "EDUCATION_DOMAIN",
			label: "Education Domain"
		}, {
			value: "EMPLOYMENT_DOMAIN",
			label: "Employment Domain"
		}, {
			value: "FINANCIAL_STRAIN_DOMAIN",
			label: "Financial Strain Domain"
		}, {
			value: "FOOD_INSECURITY_DOMAIN",
			label: "Food Insecurity Domain"
		}, {
			value: "HOUSING_INSTABILITY_AND_HOMELESSNESS_DOMAIN",
			label: "Housing Instability and Homelessness Domain"
		}, {
			value: "INADEQUATE_HOUSING_DOMAIN",
			label: "Inadequate Housing Domain"
		}, {
			value: "INTERPERSONAL_VIOLENCE_DOMAIN",
			label: "Interpersonal Violence Domain"
		}, {
			value: "SDOH_RISK_RELATED_TO_VETERAN_STATUS",
			label: "SDOH Risk Related to Veteran Status"
		}, {
			value: "SOCIAL_ISOLATION_DOMAIN",
			label: "Social Isolation Domain"
		}, {
			value: "STRESS_DOMAIN",
			label: "Stress Domain"
		}, {
			value: "TRANSPORTATION_INSECURITY_DOMAIN",
			label: "Transportation Insecurity Domain"
		}]);
		const conditionOptions = ref<Condition[]>([]);
		const goalOptions = ref<Goal[]>([]);
		const performerOptions = ref<Organization[]>([]);
		const formModel = reactive({
			request: "",
			category: "",
			//todo: we don't have status in api as param, you can't create task with specific status
			status: "",
			details: "",
			//todo: no api for that
			priority: "",
			//todo: no api for that
			occurrence: "",
			conditionIds: [],
			goalIds: [],
			performerId: "",
			consent: false
		});
		const occurrenceType = ref<string>("until");

		onMounted(async () => {
			conditionOptions.value = await getConditions();
			goalOptions.value = await getGoals();
			performerOptions.value = await getOrganizations();
		});

		//todo: use Rules type from async-validator
		const formRules = {
			request: {
				required: true,
				message: "This field is required"
			},
			category: {
				required: true,
				message: "This field is required"
			},
			//todo: remove for now, api doesn't support priority field
			// priority: {
			// 	required: true,
			// 	trigger: "change",
			// 	message: "This field is required"
			// },
			conditionIds: {
				required: true,
				message: "This field is required"
			},
			goalIds: {
				required: true,
				message: "This field is required"
			},
			performerId: {
				required: true,
				message: "This field is required"
			},
			consent: {
				required: true,
				message: "This field is required"
			}
		};
		const formEl = ref<HTMLFormElement>();
		const onFormSave = () => {
			formEl.value?.validate(async (valid: boolean) => {
				if (valid) {
					//todo: omit this props because api doesn't support that
					const payload: newTaskPayload = _.omit(formModel, ["status", "priority", "occurrence"]);
					await TasksModule.createTask(payload);
					emit("close");
				}
			});
		};

		return {
			formModel,
			categoryOptions,
			conditionOptions,
			goalOptions,
			performerOptions,
			formRules,
			formEl,
			onFormSave,
			occurrenceType
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="New Service Request/Task"
		:width="700"
		:append-to-body="true"
		:destroy-on-close="true"
		custom-class="request-dialog"
		@close="$emit('close')"
	>
		<el-form
			ref="formEl"
			:model="formModel"
			:rules="formRules"
			label-width="155px"
			label-position="left"
			size="mini"
			class="request-form"
		>
			<el-form-item
				label="Request Name"
				prop="request"
			>
				<el-input
					v-model="formModel.request"
					placeholder="Enter Name"
				/>
			</el-form-item>
			<el-form-item
				label="Category/Domain"
				prop="category"
			>
				<el-select
					v-model="formModel.category"
					placeholder="Select Category/Domain"
				>
					<el-option
						v-for="item in categoryOptions"
						:key="item.value"
						:label="item.label"
						:value="item.value"
					/>
				</el-select>
			</el-form-item>
			<el-form-item
				label="Status"
				prop="status"
			>
				<el-select
					v-model="formModel.status"
					placeholder="Select Status"
					class="half"
				>
					<el-option
						label="Draft"
						value="draft"
					/>
				</el-select>
			</el-form-item>
			<el-form-item
				label="Comment"
				prop="details"
			>
				<el-input
					v-model="formModel.details"
					type="textarea"
					rows="2"
					placeholder="Enter your comment here..."
				/>
			</el-form-item>

			<el-divider />

			<el-form-item
				label="Priority"
				prop="priority"
			>
				<el-radio-group v-model="formModel.priority">
					<el-radio label="routine">
						Routine
					</el-radio>
					<el-radio label="urgent">
						Urgent
					</el-radio>
					<el-radio label="asap">
						ASAP
					</el-radio>
				</el-radio-group>
			</el-form-item>
			<el-form-item
				label="Occurrence"
				prop="occurrence"
			>
				<el-select
					v-model="occurrenceType"
					class="half"
				>
					<el-option
						label="Until"
						value="until"
					/>
					<el-option
						label="Range"
						value="range"
					/>
				</el-select>
				<el-date-picker v-model="formModel.occurrence" />
			</el-form-item>
			<el-form-item
				label="Problem(s)"
				prop="conditionIds"
			>
				<el-select
					v-model="formModel.conditionIds"
					multiple
					placeholder="Select Problem"
				>
					<el-option
						v-for="item in conditionOptions"
						:key="item.conditionId"
						:label="item.conditionId"
						:value="item.conditionId"
					/>
				</el-select>
			</el-form-item>
			<el-form-item
				label="Goals(s)"
				prop="goalIds"
			>
				<el-select
					v-model="formModel.goalIds"
					multiple
					placeholder="Select Goal"
				>
					<el-option
						v-for="item in goalOptions"
						:key="item.goalId"
						:label="item.goalId"
						:value="item.goalId"
					/>
				</el-select>
			</el-form-item>

			<el-divider />

			<el-form-item
				label="Performer(s)"
				prop="performerId"
			>
				<el-select
					v-model="formModel.performerId"
					placeholder="Select Performer"
				>
					<el-option
						v-for="item in performerOptions"
						:key="item.organizationId"
						:label="item.name"
						:value="item.organizationId"
					/>
				</el-select>
			</el-form-item>
			<el-form-item
				label="Consent"
				prop="consent"
			>
				<el-checkbox v-model="formModel.consent" />
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button
				round
				size="mini"
				@click="$emit('close')"
			>
				Cancel
			</el-button>
			<el-button
				plain
				round
				type="primary"
				size="mini"
				@click="onFormSave"
			>
				Create & Send
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.request-dialog {
	.el-dialog__footer {
		.el-button {
			width: 155px;
		}
	}
}

.request-form {
	.el-select {
		width: 100%;

		&.half {
			width: 50%;
			margin-right: 15px;
		}
	}

	.el-divider {
		margin: 20px 0;
	}

	::v-deep(.el-date-editor.el-input) {
		width: 125px;
	}
}
</style>
