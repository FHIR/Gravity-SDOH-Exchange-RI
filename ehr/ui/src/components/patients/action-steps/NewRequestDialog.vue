<script lang="ts">
import { defineComponent, reactive, ref, watch } from "vue";
import { getServiceRequestGoals, getServiceRequestConditions, getOrganizations, getCategories, getRequests, getConsentList } from "@/api";
import { ServiceRequestCondition, ServiceRequestGoal, newTaskPayload, Organization, Coding } from "@/types";
import _ from "@/vendors/lodash";
import { TasksModule } from "@/store/modules/tasks";
import { RuleItem } from "async-validator";
import moment from "moment";

export type FormModel = {
	name: string,
	category: string,
	code: string,
	status: string,
	comment: string,
	priority: string,
	occurrence: string,
	conditionIds: string[],
	goalIds: string[],
	performerId: string,
	consent: string
};

export default defineComponent({
	name: "NewRequestDialog",
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	emits: ["close"],
	setup(props, { emit }) {
		const categoryOptions = ref<Coding[]>([]);
		const requestOptions = ref<Coding[]>([]);
		const conditionOptions = ref<ServiceRequestCondition[]>([]);
		const goalOptions = ref<ServiceRequestGoal[]>([]);
		const performerOptions = ref<Organization[]>([]);
		const consentOptions = ref<{ id: string, name: string }[]>([]);

		const formModel = reactive<FormModel>({
			name: "",
			category: "",
			code: "",
			//todo: just disabled starting draft state, you cannot change it on creation
			status: "Draft",
			comment: "",
			priority: "",
			occurrence: "",
			conditionIds: [],
			goalIds: [],
			performerId: "",
			consent: ""
		});
		const occurrenceType = ref<string>("");
		//todo: use element-ui form type
		const formEl = ref<HTMLFormElement>();

		//
		// On dialog open fetch all options for dropdowns and reset previous edits.
		//
		const onDialogOpen = async () => {
			categoryOptions.value = await getCategories();
			performerOptions.value = await getOrganizations();
			conditionOptions.value = await getServiceRequestConditions();
			goalOptions.value = await getServiceRequestGoals();
			consentOptions.value = await getConsentList();
		};
		const onDialogClose = () => {
			formEl.value?.resetFields();
			emit("close");
		};

		//
		// Watchers for Goals and Problems, at least one should be populated (validation)
		//
		watch(() => formModel.goalIds.length, () => {
			formEl.value?.validateField("conditionIds");
		});
		watch(() => formModel.conditionIds.length, () => {
			formEl.value?.validateField("goalIds");
		});
		//todo: seems like element-ui added few additional keys to async-validator RuleItem
		const formRules: { [field: string]: RuleItem & { trigger?: string } } = {
			name: {
				required: true,
				message: "This field is required"
			},
			category: {
				required: true,
				message: "This field is required"
			},
			code: {
				required: true,
				message: "This field is required"
			},
			priority: {
				required: true,
				trigger: "change",
				message: "This field is required"
			},
			conditionIds: {
				required: true,
				message: "This field is required",
				trigger: "change",
				validator: (rule, value: string[]): boolean => value.length > 0 || formModel.goalIds.length > 0
			},
			goalIds: {
				required: true,
				message: "This field is required",
				trigger: "change",
				validator: (rule, value: string[]): boolean => value.length > 0 || formModel.conditionIds.length > 0
			},
			performerId: {
				required: true,
				message: "This field is required"
			},
			occurrence: {
				required: true,
				message: "This field is required"
			},
			consent: {
				required: true,
				message: "This field is required",
				trigger: "change"
			}
		};
		//
		// On save button click handler. Validate form, if everything is ok save it and close dialog.
		//
		const onFormSave = () => {
			formEl.value?.validate(async (valid: boolean) => {
				if (valid) {
					//todo: omit this props because api doesn't support that
					const payload: newTaskPayload = _.omit(formModel, ["status"]);
					saveInProgress.value = true;
					if (formModel.occurrence.length > 1) {
						payload.occurrence = {
							start: moment(formModel.occurrence[0]).format("YYYY-MM-DD[T]HH:mm:ss"),
							end: moment(formModel.occurrence[1]).format("YYYY-MM-DD[T]HH:mm:ss")
						};
					} else {
						payload.occurrence = {
							end: moment(formModel.occurrence).format("YYYY-MM-DD[T]HH:mm:ss")
						};
					}
					try {
						await TasksModule.createTask(payload);
						emit("close");
					} finally {
						saveInProgress.value = false;
					}
				}
			});
		};
		const saveInProgress = ref<boolean>(false);
		//
		// Disable all dates that are less than today. Used inside occurrence date-pickers.
		//
		const disabledOccurrenceDate = (time: Date): boolean => time.getTime() < Date.now();
		//
		// Clear request field on every category change because they are connected.
		//
		const onCategoryChange = async (code: string) => {
			formModel.code = "";
			requestOptions.value = await getRequests(code);
		};
		//
		// On every until/from...to change clear model, element-ui can't work with array or date in both datepicker and range datepicker.
		//
		const onOccurrenceSelectChange = () => {
			formModel.occurrence = "";
		};

		return {
			formModel,
			categoryOptions,
			requestOptions,
			conditionOptions,
			goalOptions,
			performerOptions,
			consentOptions,
			formRules,
			formEl,
			onFormSave,
			occurrenceType,
			disabledOccurrenceDate,
			saveInProgress,
			onCategoryChange,
			onDialogOpen,
			onDialogClose,
			onOccurrenceSelectChange
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="New Service Request/Task"
		:width="700"
		append-to-body
		destroy-on-close
		custom-class="new-request-dialog"
		@close="onDialogClose"
		@open="onDialogOpen"
	>
		<el-form
			ref="formEl"
			:model="formModel"
			:rules="formRules"
			label-width="155px"
			label-position="left"
			size="mini"
			class="new-request-form"
		>
			<el-form-item
				label="Request Name"
				prop="name"
			>
				<el-input
					v-model="formModel.name"
					placeholder="Add request name"
				/>
			</el-form-item>
			<el-form-item
				label="Category/Domain"
				prop="category"
			>
				<el-select
					v-model="formModel.category"
					placeholder="Select Category/Domain"
					@change="onCategoryChange($event)"
				>
					<el-option
						v-for="item in categoryOptions"
						:key="item.code"
						:label="item.display"
						:value="item.code"
					/>
				</el-select>
			</el-form-item>
			<el-form-item
				label="Request"
				prop="code"
			>
				<el-select
					v-model="formModel.code"
					placeholder="Select Request"
					no-data-text="Select Category/Domain first"
				>
					<el-option
						v-for="item in requestOptions"
						:key="item.code"
						:label="`${item.display} (${item.code})`"
						:value="item.code"
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
					:disabled="true"
				>
					<el-option
						label="Draft"
						value="draft"
					/>
				</el-select>
			</el-form-item>
			<el-form-item
				label="Comment"
				prop="comment"
			>
				<el-input
					v-model="formModel.comment"
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
					<el-radio label="Routine" />
					<el-radio label="Urgent" />
					<el-radio label="ASAP" />
				</el-radio-group>
			</el-form-item>
			<el-form-item
				label="Occurrence"
				prop="occurrence"
			>
				<el-select
					v-model="occurrenceType"
					placeholder="Select"
					class="small"
					@change="onOccurrenceSelectChange"
				>
					<el-option
						label="Until"
						value="until"
					/>
					<el-option
						label="From...to"
						value="range"
					/>
				</el-select>
				<el-date-picker
					v-if="occurrenceType === 'until'"
					v-model="formModel.occurrence"
					:disabled-date="disabledOccurrenceDate"
					placeholder="Select date"
				/>
				<el-date-picker
					v-if="occurrenceType === 'range'"
					v-model="formModel.occurrence"
					type="daterange"
					range-separator="To"
					start-placeholder="Select date"
					end-placeholder="Select date"
					:disabled-date="disabledOccurrenceDate"
				/>
			</el-form-item>
			<el-form-item
				label="Problem(s)"
				prop="conditionIds"
			>
				<el-select
					v-model="formModel.conditionIds"
					multiple
					placeholder="Select Problem(s)"
				>
					<el-option
						v-for="item in conditionOptions"
						:key="item.id"
						:label="item.display"
						:value="item.id"
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
					placeholder="Select Goal(s)"
				>
					<el-option
						v-for="item in goalOptions"
						:key="item.id"
						:label="item.display"
						:value="item.id"
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
					placeholder="Select Performer(s)"
				>
					<el-option
						v-for="item in performerOptions"
						:key="item.id"
						:label="item.name"
						:value="item.id"
					/>
				</el-select>
			</el-form-item>
			<el-form-item
				label="Consent"
				prop="consent"
			>
				<el-select
					v-model="formModel.consent"
					placeholder="Select Consent"
				>
					<el-option
						v-for="item in consentOptions"
						:key="item.id"
						:label="item.name"
						:value="item.id"
					/>
				</el-select>
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
				:loading="saveInProgress"
				@click="onFormSave"
			>
				Create & Send
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.new-request-form {
	.el-select {
		width: 100%;

		&.half {
			width: 50%;
			margin-right: 15px;
		}

		&.small {
			width: 20%;
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
