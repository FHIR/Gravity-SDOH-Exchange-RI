<script lang="ts">
import { defineComponent, ref } from "vue";
import { RuleItem } from "async-validator";
import * as VALUESETS from "@/utils/personal-characteristics-valuesets";
import FileInput from "@/components/FileInput.vue";
import { PersonalCharacteristic, METHOD_TYPES, MethodType, MethodPayload } from "@/types/personal-characteristics";
import { addPersonalCharacteristic } from "@/api";


const DEFAULT_REQUIRED_RULE = {
	required: true,
	message: "This field is required"
};


type MethodModel = {
	type: MethodType
	text: string
}


type Model = {
	type: "Personal Pronouns"
	method: MethodModel
	payload: {
		value: VALUESETS.PersonalPronounsCode
		text: string
	}
} | {
	type: "Ethnicity"
	method: MethodModel
	payload: {
		value: VALUESETS.EthnicityCode | undefined
		detailedValue: VALUESETS.DetailedEthnicityCode[]
		description: string
	}
} | {
	type: "Race"
	method: MethodModel
	payload: {
		value: VALUESETS.RaceCode[]
		detailedValue: VALUESETS.DetailedRaceCode[]
		description: string
	}
} | {
	type: "Recorded Sex Gender"
	method: MethodModel
	payload: {
		value: VALUESETS.SexGenderCode
		derivedFrom: File | null
	}
} | {
	type: "Sexual Orientation"
	method: MethodModel
	payload: {
		value: VALUESETS.SexualOrientationCode
		text: string
	}
} | {
	type: "Gender Identity"
	method: MethodModel
	payload: {}
}

type Type = typeof TYPES[number]

const TYPES = [
	"Personal Pronouns",
	"Ethnicity",
	"Race",
	"Recorded Sex Gender",
	"Sexual Orientation",
	"Gender Identity"
] as const;

const initModel = (t: Type): Model => {
	const selfReported = {
		type: "Self Reported",
		text: ""
	} as const;
	switch (t) {
		case "Personal Pronouns": return {
			type: t,
			method: selfReported,
			payload: {
				value: "LA29518-0",
				text: ""
			}
		};
		case "Ethnicity": return {
			type: t,
			method: selfReported,
			payload: {
				value: undefined,
				detailedValue: [],
				description: ""
			}
		};
		case "Race": return {
			type: t,
			method: selfReported,
			payload: {
				value: [],
				detailedValue: [],
				description: ""
			}
		};
		case "Recorded Sex Gender": return {
			type: t,
			method: selfReported,
			payload: {
				value: "LA13504-8",
				derivedFrom: null
			}
		};
		case "Sexual Orientation": return {
			type: t,
			method: selfReported,
			payload: {
				value: "LA22877-7",
				text: ""
			}
		};
		case "Gender Identity": return {
			type: t,
			method: selfReported,
			payload: {}
		};
	}
};


const makePayload = ({ type, method, payload }: Model): PersonalCharacteristic | null => {
	const makeMethodPayload = ({ type, text }: MethodModel): MethodPayload => type === "Self Reported" ? {
		type: "Self Reported",
		text: undefined
	} : {
		type,
		text
	};

	switch (type) {
		case "Personal Pronouns": return {
			type: "Personal Pronouns",
			method: makeMethodPayload(method),
			...(payload.value === "OTH" ? {
				value: payload.value,
				text: payload.text
			} : {
				value: payload.value,
				text: undefined
			})
		};
		case "Ethnicity": return {
			type: "Ethnicity",
			method: makeMethodPayload(method),
			value: payload.value,
			detailedValue: payload.detailedValue,
			description: payload.description
		};
		case "Race": return {
			type: "Race",
			method: makeMethodPayload(method),
			value: payload.value,
			detailedValue: payload.detailedValue,
			description: payload.description
		};
		case "Recorded Sex Gender": return payload.derivedFrom ? {
			type: "Recorded Sex Gender",
			method: makeMethodPayload(method),
			value: payload.value,
			derivedFrom: "file should be attached somehow"
		} : null;
		case "Sexual Orientation": return {
			type: "Sexual Orientation",
			method: makeMethodPayload(method),
			...(payload.value === VALUESETS.SEXUAL_ORIENTATION_OTHER? {
				value: payload.value,
				text: payload.text
			} : {
				value: payload.value,
				text: undefined
			})
		};
		case "Gender Identity": return {
			type: "Gender Identity",
			method: makeMethodPayload(method)
		};
	}
};


export default defineComponent({
	components: { FileInput },
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	emits: ["close"],
	setup(_, { emit }) {
		const formEl = ref<HTMLFormElement>();
		const saveInProgress = ref<boolean>(false);

		const onDialogClose = () => {
			// formEl.value?.resetFields();
			emit("close");
		};

		const onDialogOpen = async () => {
		};

		const formRules: { [field: string]: RuleItem & { trigger?: string } } = {
		};


		const onFormSave = () => {
			formEl.value?.validate(async (valid: boolean) => {
				if (valid) {
					saveInProgress.value = true;

					try {
						const pl = makePayload(model.value);
						if (pl) {
							addPersonalCharacteristic(pl);
						}
						emit("close");
					} finally {
						saveInProgress.value = false;
					}
				}
			});
		};

		const model = ref<Model>(initModel("Personal Pronouns"));

		const onTypeChange = (type: Type) => {
			model.value = initModel(type);
		};

		return {
			model,
			formRules,
			TYPES,
			saveInProgress,
			formEl,
			METHOD_TYPES,
			PRONOUNS: VALUESETS.PERSONAL_PRONOUNS,
			SEXUAL_ORIENTATIONS: VALUESETS.SEXUAL_ORIENTATION,
			SEXUAL_ORIENTATION_OTHER: VALUESETS.SEXUAL_ORIENTATION_OTHER,
			ETHNICITY: VALUESETS.ETHNICITY,
			DETAILED_ETHNICITY: VALUESETS.DETAILED_ETHNICITY,
			RACES: VALUESETS.RACE,
			DETAILED_RACES: VALUESETS.DETAILED_RACE,
			SEX_GENDERS: VALUESETS.SEX_GENDER,
			onDialogClose,
			onDialogOpen,
			onFormSave,
			onTypeChange
		};
	}
});
</script>

<template>
	<el-dialog
		:model-value="visible"
		title="Add Personal Characteristics"
		:width="700"
		destroy-on-close
		:before-close="onDialogClose"
		@open="onDialogOpen"
	>
		<el-form
			ref="formEl"
			:model="model"
			:rules="formRules"
			label-width="155px"
			label-position="left"
			size="mini"
			class="new-goal-form"
		>
			<el-form-item
				label="Type"
				prop="type"
			>
				<el-select
					:model-value="model.type"
					@change="onTypeChange"
				>
					<el-option
						v-for="item in TYPES"
						:key="item"
						:label="item"
						:value="item"
					/>
				</el-select>
			</el-form-item>

			<el-form-item
				label="Method"
			>
				<div>
					<el-radio
						v-for="item in METHOD_TYPES"
						:key="item"
						v-model="model.method.type"
						:label="item"
					>
						{{ item }}
					</el-radio>
				</div>
				<el-input
					v-if="model.method.type !== 'Self Reported'"
					v-model="model.method.text"
					placeholder="Specify"
				/>
			</el-form-item>

			<template v-if="model.type === 'Personal Pronouns'">
				<el-form-item
					label="Value"
				>
					<div
						v-for="item in PRONOUNS"
						:key="item.code"
					>
						<el-radio
							v-model="model.payload.value"
							:label="item.code"
						>
							{{ item.display }}
							<el-input
								v-if="item.code === 'OTH'"
								v-model="model.payload.text"
								:readonly="model.payload.value !== 'OTH'"
								size="mini"
								placeholder="specify"
								@focus="model.payload.value = 'OTH'"
							/>
						</el-radio>
					</div>
				</el-form-item>
			</template>

			<template v-if="model.type === 'Ethnicity'">
				<el-form-item
					label="Value"
				>
					<el-radio
						v-model="model.payload.value"
						:label="undefined"
					>
						Not specified
					</el-radio>
					<el-radio
						v-for="item in ETHNICITY"
						:key="item.code"
						v-model="model.payload.value"
						:label="item.code"
					>
						{{ item.display }}
					</el-radio>
				</el-form-item>
				<el-form-item
					label="Detailed Value"
				>
					<el-select
						v-model="model.payload.detailedValue"
						multiple
						filterable
					>
						<el-option
							v-for="item in DETAILED_ETHNICITY"
							:key="item.code"
							:label="item.display"
							:value="item.code"
						/>
					</el-select>
				</el-form-item>
				<el-form-item
					label="Description"
				>
					<el-input
						v-model="model.payload.description"
						placeholder="Enter description"
					/>
				</el-form-item>
			</template>

			<template v-if="model.type === 'Race'">
				<el-form-item
					label="Value"
				>
					<el-select
						v-model="model.payload.value"
						multiple
					>
						<el-option
							v-for="item in RACES"
							:key="item.code"
							:label="item.display"
							:value="item.code"
						/>
					</el-select>
				</el-form-item>
				<el-form-item
					label="Detailed Value"
				>
					<el-select
						v-model="model.payload.detailedValue"
						multiple
						filterable
					>
						<el-option
							v-for="item in DETAILED_RACES"
							:key="item.code"
							:label="item.display"
							:value="item.code"
						/>
					</el-select>
				</el-form-item>
				<el-form-item
					label="Description"
				>
					<el-input
						v-model="model.payload.description"
						placeholder="Enter description"
					/>
				</el-form-item>
			</template>

			<template v-if="model.type === 'Recorded Sex Gender'">
				<el-form-item
					label="Value"
				>
					<el-radio
						v-for="item in SEX_GENDERS"
						:key="item.code"
						v-model="model.payload.value"
						:label="item.code"
					>
						{{ item.display }}
					</el-radio>
				</el-form-item>
				<el-form-item
					label="Derived From"
				>
					<FileInput
						v-model:value="model.payload.derivedFrom"
						:disabled="saveInProgress"
						accept="application/pdf,.pdf"
					/>
				</el-form-item>
			</template>

			<template v-if="model.type === 'Sexual Orientation'">
				<el-form-item
					label="Value"
				>
					<div
						v-for="item in SEXUAL_ORIENTATIONS"
						:key="item.code"
					>
						<el-radio
							v-model="model.payload.value"
							:label="item.code"
						>
							{{ item.display }}
							<el-input
								v-if="item.code === SEXUAL_ORIENTATION_OTHER"
								v-model="model.payload.text"
								:readonly="model.payload.value !== SEXUAL_ORIENTATION_OTHER"
								size="mini"
								placeholder="specify"
								@focus="model.payload.value = SEXUAL_ORIENTATION_OTHER"
							/>
						</el-radio>
					</div>
				</el-form-item>
			</template>
		</el-form>
		<template #footer>
			<el-button
				round
				size="mini"
				@click="onDialogClose"
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
				Save
			</el-button>
		</template>
	</el-dialog>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.new-goal-form {
	.el-select:not(.achievement-status) {
		width: 100%;
	}
}
</style>
