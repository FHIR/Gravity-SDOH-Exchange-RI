<script lang="ts">
import { defineComponent, reactive, ref, onMounted } from "vue";
import { getGoals, getConditions, getOrganizations } from "@/api";
import { Condition, Goal, Organization } from "@/types";

export default defineComponent({
	name: "RequestDialog",
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	emits: ["close"],
	setup() {
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

		const form = reactive({
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

		onMounted(async () => {
			conditionOptions.value = await getConditions();
			goalOptions.value = await getGoals();
			performerOptions.value = await getOrganizations();
		});

		return {
			form,
			categoryOptions,
			conditionOptions,
			goalOptions,
			performerOptions
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
			:model="form"
			label-width="155px"
			label-position="left"
			size="mini"
			class="request-form"
		>
			<el-form-item label="Request Name">
				<el-input
					v-model="form.request"
					placeholder="Enter Name"
				/>
			</el-form-item>
			<el-form-item label="Category/Domain">
				<el-select
					v-model="form.category"
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
			<el-form-item label="Status">
				<el-select
					v-model="form.status"
					placeholder="Select Status"
					class="half"
				>
					<el-option
						label="Draft"
						value="draft"
					/>
				</el-select>
			</el-form-item>
			<el-form-item label="Comment">
				<el-input
					v-model="form.details"
					type="textarea"
					rows="2"
					placeholder="Enter your comment here..."
				/>
			</el-form-item>

			<el-divider />

			<el-form-item label="Priority">
				<el-radio
					v-model="form.priority"
					label="routine"
				>
					Routine
				</el-radio>
				<el-radio
					v-model="form.priority"
					label="urgent"
				>
					Urgent
				</el-radio>
				<el-radio
					v-model="form.priority"
					label="asap"
				>
					ASAP
				</el-radio>
			</el-form-item>
			<el-form-item label="Occurrence">
				<el-select
					class="half"
				>
					<el-option
						label="Until"
						value="until"
					/>
				</el-select>
				<el-date-picker v-model="form.occurrence" />
			</el-form-item>
			<el-form-item label="Problem(s)">
				<el-select
					v-model="form.conditionIds"
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
			<el-form-item label="Goals(s)">
				<el-select
					v-model="form.goalIds"
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

			<el-form-item label="Performer(s)">
				<el-select
					v-model="form.performerId"
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
			<el-form-item label="Consent">
				<el-checkbox v-model="form.consent" />
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
