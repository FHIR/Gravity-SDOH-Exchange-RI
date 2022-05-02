<script lang="ts">
import { computed, defineComponent, ref } from "vue";
import * as VALUESETS from "@/utils/personal-characteristics-valuesets";
import FileInput from "@/components/FileInput.vue";
import { PersonalCharacteristicPayload, METHOD_TYPES, MethodType, MethodPayload } from "@/types/personal-characteristics";
import { addPersonalCharacteristic } from "@/api";
import { file2Base64 } from "@/utils/utils";


const DEFAULT_REQUIRED_RULE = {
	required: true,
	message: "This field is required"
};


type MethodModel = {
	type: MethodType
	text: string
}


type Model = {
	type: "personal_pronouns"
	method: MethodModel
	payload: {
		value: VALUESETS.PersonalPronounsCode
		text: string
	}
} | {
	type: "ethnicity"
	method: MethodModel
	payload: {
		value: VALUESETS.EthnicityCode | ""
		detailedValue: VALUESETS.DetailedEthnicityCode[]
		description: string
	}
} | {
	type: "race"
	method: MethodModel
	payload: {
		value: VALUESETS.RaceCode[]
		detailedValue: VALUESETS.DetailedRaceCode[]
		description: string
	}
} | {
	type: "sex_gender"
	method: null
	payload: {
		value: VALUESETS.SexGenderCode
		text: string
		derivedFrom: File | null
	}
} | {
	type: "sexual_orientation"
	method: MethodModel
	payload: {
		value: VALUESETS.SexualOrientationCode
		text: string
	}
} | {
	type: "gender_identity"
	method: MethodModel
	payload: {
		value: VALUESETS.GenderIdentiryCode
		text: string
	}
}

type ModelType = Model["type"]

const TYPES: readonly { code: ModelType, display: string }[] = [
	{ code: "personal_pronouns", display: "Personal Pronouns" },
	{ code: "ethnicity", display: "Ethnicity" },
	{ code: "race", display: "Race" },
	{ code: "sex_gender", display: "Recorded Sex Gender" },
	{ code: "sexual_orientation", display: "Sexual Orientation" },
	{ code: "gender_identity", display: "Gender Identity" },
] as const;

const initModel = (t: ModelType): Model => {
	const selfReported = {
		type: "SELF_REPORTED",
		text: ""
	} as const;
	switch (t) {
		case "personal_pronouns": return {
			type: t,
			method: selfReported,
			payload: {
				value: VALUESETS.PERSONAL_PRONOUNS[0].code,
				text: ""
			}
		};
		case "ethnicity": return {
			type: t,
			method: selfReported,
			payload: {
				value: "",
				detailedValue: [],
				description: ""
			}
		};
		case "race": return {
			type: t,
			method: selfReported,
			payload: {
				value: [],
				detailedValue: [],
				description: ""
			}
		};
		case "sex_gender": return {
			type: t,
			method: null,
			payload: {
				value: VALUESETS.SEX_GENDER[0].code,
				text: "",
				derivedFrom: null
			}
		};
		case "sexual_orientation": return {
			type: t,
			method: selfReported,
			payload: {
				value: VALUESETS.SEXUAL_ORIENTATION[0].code,
				text: ""
			}
		};
		case "gender_identity": return {
			type: t,
			method: selfReported,
			payload: {
				value: VALUESETS.GENDER_IDENTITY[0].code,
				text: ""
			}
		};
	}
};


const makePayload = async ({ type, method, payload }: Model): Promise<PersonalCharacteristicPayload | null> => {
	const makeMethodPayload = ({ type, text }: MethodModel): MethodPayload => type === "SELF_REPORTED" ? {
		method: "SELF_REPORTED",
		methodDetail: undefined
	} : {
		method: type,
		methodDetail: text
	};

	switch (type) {
		case "personal_pronouns": return {
			type: "PERSONAL_PRONOUNS",
			...makeMethodPayload(method),
			...(payload.value === "OTH" ? {
				value: payload.value,
				valueDetail: payload.text
			} : {
				value: payload.value,
				valueDetail: undefined
			})
		};
		case "ethnicity": return {
			type: "ETHNICITY",
			...makeMethodPayload(method),
			value: payload.value === "" ? undefined : payload.value,
			detailedValues: payload.detailedValue,
			description: payload.description === "" ? undefined : payload.description
		};
		case "race": return {
			type: "RACE",
			...makeMethodPayload(method),
			values: payload.value,
			detailedValues: payload.detailedValue,
			description: payload.description === "" ? undefined : payload.description
		};
		case "sex_gender": return payload.derivedFrom ? {
			type: "SEX_GENDER",
			method: "DERIVED_SPECIFY",
			...(payload.value === VALUESETS.SEX_GENDER_OTHER? {
				value: payload.value,
				valueDetail: payload.text
			} : {
				value: payload.value,
				valueDetail: undefined
			}),
			derivedFrom: {
				name: payload.derivedFrom.name,
				base64Content: await file2Base64(payload.derivedFrom)
			}
		} : null;
		case "sexual_orientation": return {
			type: "SEXUAL_ORIENTATION",
			...makeMethodPayload(method),
			...(payload.value === VALUESETS.SEXUAL_ORIENTATION_OTHER? {
				value: payload.value,
				valueDetail: payload.text
			} : {
				value: payload.value,
				valueDetail: undefined
			})
		};
		case "gender_identity": return {
			type: "GENDER_IDENTITY",
			...makeMethodPayload(method),
			...(payload.value === VALUESETS.GENDER_IDENTITY_OTHER? {
				value: payload.value,
				valueDetail: payload.text
			} : {
				value: payload.value,
				valueDetail: undefined
			})
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
		const formEl = ref<{
			validate:() => Promise<void>
			validateField: (f: string[]) => Promise<void>
		}>();
		const saveInProgress = ref<boolean>(false);

		const onDialogClose = () => {
			emit("close");
		};

		const onDialogOpen = async () => {
			model.value = initModel("personal_pronouns");
		};

		const valueDetailRequired = ({ type, payload }: Model) => {
			switch (type) {
				case "personal_pronouns": return payload.value === VALUESETS.PERSONAL_PRONOUNS_OTHER;
				case "sex_gender": return payload.value === VALUESETS.SEX_GENDER_OTHER;
				case "sexual_orientation": return payload.value === VALUESETS.SEXUAL_ORIENTATION_OTHER;
				case "gender_identity": return payload.value === VALUESETS.GENDER_IDENTITY_OTHER;
				default: return false;
			}
		};

		const formRules = computed(() => {
			const { type, method, payload } = model.value;
			const methodDetailRequired = type !== "sex_gender" && method.type !== "SELF_REPORTED";
			const required = {
				message: DEFAULT_REQUIRED_RULE.message,
				validator: (_: unknown, value: string) => value !== ""
			};
			const foo = () => {
				if (type === "ethnicity") {
					const field = {
						message: "At least one should be specified",
						validator: () => payload.value !== "" || payload.detailedValue.length > 0 || payload.description !== ""
					};
					return {
						"payload.value": field,
						"payload.detailedValue": field,
						"payload.description": field,
					};
				}
				if (type === "race") {
					const field = {
						message: "At least one should be specified",
						validator: () => payload.value.length > 0 || payload.detailedValue.length > 0 || payload.description !== ""
					};
					return {
						"payload.value": field,
						"payload.detailedValue": field,
						"payload.description": field,
					};
				}
				return {};
			};
			return {
				"method.text": methodDetailRequired ? required : {},
				"payload.text": valueDetailRequired(model.value) ? required : {},
				"payload.derivedFrom": {
					message: DEFAULT_REQUIRED_RULE.message,
					validator: () => type !== "sex_gender" || payload.derivedFrom !== null,
				},
				...foo(),
			};
		});


		const onFormSave = async () => {
			await formEl.value?.validate();
			saveInProgress.value = true;

			try {
				const pl = await makePayload(model.value);
				if (pl) {
					await addPersonalCharacteristic(pl);
				}
				onDialogClose();
			} finally {
				saveInProgress.value = false;
			}
		};

		const model = ref<Model>(initModel("personal_pronouns"));

		const onTypeChange = (type: ModelType) => {
			model.value = initModel(type);
		};

		return {
			model,
			formRules,
			TYPES,
			saveInProgress,
			formEl,
			METHOD_TYPES: Object.entries(METHOD_TYPES).map(([code, display]) => ({ code, display })),
			VALUESETS,
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
			class="form"
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
						:key="item.code"
						:label="item.display"
						:value="item.code"
					/>
				</el-select>
			</el-form-item>

			<el-form-item
				label="Method"
				prop="method.text"
			>
				<div v-if="model.type === 'sex_gender'">
					<el-radio
						v-for="item in METHOD_TYPES"
						:key="item.code"
						model-value="DERIVED_SPECIFY"
						:disabled="true"
						:label="item.code"
					>
						{{ item.display }}
					</el-radio>
				</div>
				<template v-else>
					<div>
						<el-radio
							v-for="item in METHOD_TYPES"
							:key="item.code"
							v-model="model.method.type"
							:label="item.code"
						>
							{{ item.display }}
						</el-radio>
					</div>
					<el-input
						v-if="model.method.type !== 'SELF_REPORTED'"
						v-model="model.method.text"
						placeholder="Specify"
					/>
				</template>
			</el-form-item>

			<template v-if="model.type === 'personal_pronouns'">
				<el-form-item
					label="Value"
					prop="payload.text"
				>
					<div
						v-for="item in VALUESETS.PERSONAL_PRONOUNS"
						:key="item.code"
					>
						<el-radio
							v-model="model.payload.value"
							:label="item.code"
						>
							{{ item.display }}
							<el-input
								v-if="item.code === VALUESETS.PERSONAL_PRONOUNS_OTHER"
								v-model="model.payload.text"
								:readonly="model.payload.value !== VALUESETS.PERSONAL_PRONOUNS_OTHER"
								size="mini"
								placeholder="specify"
								@focus="model.payload.value = VALUESETS.PERSONAL_PRONOUNS_OTHER"
							/>
						</el-radio>
					</div>
				</el-form-item>
			</template>

			<template v-if="model.type === 'ethnicity'">
				<el-form-item
					label="Value"
					prop="payload.value"
				>
					<el-radio
						v-model="model.payload.value"
						:label="''"
						@change="formEl?.validateField(['payload.value', 'payload.detailedValue', 'payload.description'])"
					>
						Not specified
					</el-radio>
					<el-radio
						v-for="item in VALUESETS.ETHNICITY"
						:key="item.code"
						v-model="model.payload.value"
						:label="item.code"
						@change="formEl?.validateField(['payload.value', 'payload.detailedValue', 'payload.description'])"
					>
						{{ item.display }}
					</el-radio>
				</el-form-item>
				<el-form-item
					label="Detailed Value"
					prop="payload.detailedValue"
				>
					<el-select
						v-model="model.payload.detailedValue"
						multiple
						filterable
						@change="formEl?.validateField(['payload.value', 'payload.detailedValue', 'payload.description'])"
						@blur="formEl?.validateField(['payload.value', 'payload.detailedValue', 'payload.description'])"
					>
						<el-option
							v-for="item in VALUESETS.DETAILED_ETHNICITY"
							:key="item.code"
							:label="item.display"
							:value="item.code"
						/>
					</el-select>
				</el-form-item>
				<el-form-item
					label="Description"
					prop="payload.description"
				>
					<el-input
						v-model="model.payload.description"
						placeholder="Enter description"
						@input="formEl?.validateField(['payload.value', 'payload.detailedValue', 'payload.description'])"
						@blur="formEl?.validateField(['payload.value', 'payload.detailedValue', 'payload.description'])"
					/>
				</el-form-item>
			</template>

			<template v-if="model.type === 'race'">
				<el-form-item
					label="Value"
					prop="payload.value"
				>
					<el-select
						v-model="model.payload.value"
						multiple
						@change="formEl?.validateField(['payload.value', 'payload.detailedValue', 'payload.description'])"
						@blur="formEl?.validateField(['payload.value', 'payload.detailedValue', 'payload.description'])"
					>
						<el-option
							v-for="item in VALUESETS.RACE"
							:key="item.code"
							:label="item.display"
							:value="item.code"
						/>
					</el-select>
				</el-form-item>
				<el-form-item
					label="Detailed Value"
					prop="payload.detailedValue"
				>
					<el-select
						v-model="model.payload.detailedValue"
						multiple
						filterable
						@change="formEl?.validateField(['payload.value', 'payload.detailedValue', 'payload.description'])"
						@blur="formEl?.validateField(['payload.value', 'payload.detailedValue', 'payload.description'])"
					>
						<el-option
							v-for="item in VALUESETS.DETAILED_RACE"
							:key="item.code"
							:label="item.display"
							:value="item.code"
						/>
					</el-select>
				</el-form-item>
				<el-form-item
					label="Description"
					prop="payload.description"
				>
					<el-input
						v-model="model.payload.description"
						placeholder="Enter description"
						@input="formEl?.validateField(['payload.value', 'payload.detailedValue', 'payload.description'])"
						@blur="formEl?.validateField(['payload.value', 'payload.detailedValue', 'payload.description'])"
					/>
				</el-form-item>
			</template>

			<template v-if="model.type === 'sex_gender'">
				<el-form-item
					label="Value"
					prop="payload.text"
				>
					<div
						v-for="item in VALUESETS.SEX_GENDER"
						:key="item.code"
					>
						<el-radio
							v-model="model.payload.value"
							:label="item.code"
						>
							{{ item.display }}
							<el-input
								v-if="item.code === VALUESETS.SEX_GENDER_OTHER"
								v-model="model.payload.text"
								:readonly="model.payload.value !== VALUESETS.SEX_GENDER_OTHER"
								size="mini"
								placeholder="specify"
								@focus="model.payload.value = VALUESETS.SEX_GENDER_OTHER"
							/>
						</el-radio>
					</div>
				</el-form-item>
				<el-form-item
					label="Derived From"
					prop="payload.derivedFrom"
				>
					<FileInput
						v-model:value="model.payload.derivedFrom"
						:disabled="saveInProgress"
						@update:value="formEl?.validateField(['payload.derivedFrom'])"
					/>
				</el-form-item>
			</template>

			<template v-if="model.type === 'sexual_orientation'">
				<el-form-item
					label="Value"
					prop="payload.text"
				>
					<div
						v-for="item in VALUESETS.SEXUAL_ORIENTATION"
						:key="item.code"
					>
						<el-radio
							v-model="model.payload.value"
							:label="item.code"
						>
							{{ item.display }}
							<el-input
								v-if="item.code === VALUESETS.SEXUAL_ORIENTATION_OTHER"
								v-model="model.payload.text"
								:readonly="model.payload.value !== VALUESETS.SEXUAL_ORIENTATION_OTHER"
								size="mini"
								placeholder="specify"
								@focus="model.payload.value = VALUESETS.SEXUAL_ORIENTATION_OTHER"
							/>
						</el-radio>
					</div>
				</el-form-item>
			</template>

			<template v-if="model.type === 'gender_identity'">
				<el-form-item
					label="Value"
					prop="payload.text"
				>
					<div
						v-for="item in VALUESETS.GENDER_IDENTITY"
						:key="item.code"
					>
						<el-radio
							v-model="model.payload.value"
							:label="item.code"
						>
							{{ item.display }}
							<el-input
								v-if="item.code === VALUESETS.GENDER_IDENTITY_OTHER"
								v-model="model.payload.text"
								:readonly="model.payload.value !== VALUESETS.GENDER_IDENTITY_OTHER"
								size="mini"
								placeholder="specify"
								@focus="model.payload.value = VALUESETS.GENDER_IDENTITY_OTHER"
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

.form {
	.el-select {
		width: 100%;
	}
}
</style>
