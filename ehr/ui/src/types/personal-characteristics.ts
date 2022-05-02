import { PersonalPronounsCode, EthnicityCode, DetailedEthnicityCode, RaceCode, DetailedRaceCode, SexGenderCode, SexualOrientationCode, SEXUAL_ORIENTATION_OTHER, PERSONAL_PRONOUNS_OTHER, SEX_GENDER_OTHER, GenderIdentiryCode, GENDER_IDENTITY_OTHER } from "@/utils/personal-characteristics-valuesets";


export type MethodPayload = {
	method: "SELF_REPORTED"
	methodDetail: undefined
} | {
	method: "DERIVED_SPECIFY" | "OTHER_SPECIFY"
	methodDetail: string
}

export type MethodType = MethodPayload["method"]

export const METHOD_TYPES: Record<MethodType, string> = {
	"SELF_REPORTED": "Self Reported",
	"DERIVED_SPECIFY": "Derived",
	"OTHER_SPECIFY": "Other",
} as const;


type PersonalPronounsPayload = {
	type: "PERSONAL_PRONOUNS"
} & MethodPayload & ({
	value: Exclude<PersonalPronounsCode, typeof PERSONAL_PRONOUNS_OTHER>
	valueDetail: undefined
} | {
	value: typeof PERSONAL_PRONOUNS_OTHER
	valueDetail: string
})


type EthnicityPayload = {
	type: "ETHNICITY"
	value?: EthnicityCode
	detailedValues: DetailedEthnicityCode[]
	description?: string
} & MethodPayload


type RacePayload = {
	type: "RACE"
	values: RaceCode[]
	detailedValues: DetailedRaceCode[]
	description?: string
} & MethodPayload


type RecordedSexGenderPayload = {
	type: "SEX_GENDER"
	method: "DERIVED_SPECIFY"
	derivedFrom: {
		name: string
		base64Content: string
	}
} & ({
	value: Exclude<SexGenderCode, typeof SEX_GENDER_OTHER>
	valueDetail: undefined
} | {
	value: typeof SEX_GENDER_OTHER
	valueDetail: string
})


type SexualOrientationPayload = {
	type: "SEXUAL_ORIENTATION"
} & MethodPayload & ({
	value: Exclude<SexualOrientationCode, typeof SEXUAL_ORIENTATION_OTHER>
	valueDetail: undefined
} | {
	value: typeof SEXUAL_ORIENTATION_OTHER
	valueDetail: string
})


type GenderIdentityPayload = {
	type: "GENDER_IDENTITY"
} & MethodPayload & ({
	value: Exclude<GenderIdentiryCode, typeof GENDER_IDENTITY_OTHER>
	valueDetail: undefined
} | {
	value: typeof GENDER_IDENTITY_OTHER
	valueDetail: string
})


export type PersonalCharacteristicPayload =
	| PersonalPronounsPayload
	| EthnicityPayload
	| RacePayload
	| RecordedSexGenderPayload
	| SexualOrientationPayload
	| GenderIdentityPayload


type Type = PersonalCharacteristicPayload["type"]

export const TYPES: Record<Type, string> = {
	"PERSONAL_PRONOUNS": "Personal Pronouns",
	"ETHNICITY": "Ethnicity",
	"RACE": "Race",
	"SEX_GENDER": "Recorded Sex Gender",
	"SEXUAL_ORIENTATION": "Sexual Orientation",
	"GENDER_IDENTITY": "Gender Identity",
} as const;


type Coding = { code: string, display: string }

export type PersonalCharacteristic = {
	id: string
	performer: {
		id: string
		display: string
	}
	type: Type
	method: MethodType
	methodDetail?: string
	hasAttachment: boolean
	value?: Coding
	valueDetail?: string
	values?: Coding[]
	detailedValues?: Coding[]
	description?: string
}
