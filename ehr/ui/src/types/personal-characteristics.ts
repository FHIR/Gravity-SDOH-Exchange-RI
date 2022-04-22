import { PersonalPronounsCode, EthnicityCode, DetailedEthnicityCode, RaceCode, DetailedRaceCode, SexGenderCode, SexualOrientationCode, SEXUAL_ORIENTATION_OTHER } from "@/utils/personal-characteristics-valuesets";


export const METHOD_TYPES = [
	"Self Reported",
	"Derived",
	"Other"
] as const;

export type MethodType = typeof METHOD_TYPES[number]

export type MethodPayload = {
	type: "Self Reported"
	text: undefined
} | {
    type: Exclude<MethodType, "Self Reported">
    text: string
}


type PersonalPronounsPayload = ({
	type: "Personal Pronouns"
	method: MethodPayload
	value: Exclude<PersonalPronounsCode, "OTH">
	text: undefined
} | {
	type: "Personal Pronouns"
	method: MethodPayload
	value: "OTH"
	text: string
})


type EthnicityPayload = {
	type: "Ethnicity"
	method: MethodPayload
	value?: EthnicityCode
	detailedValue: DetailedEthnicityCode[]
	description: string
}


type RacePayload = {
	type: "Race"
	method: MethodPayload
	value: RaceCode[]
	detailedValue: DetailedRaceCode[]
	description: string
}

type RecordedSexGenderPayload = {
	type: "Recorded Sex Gender"
	method: MethodPayload
	value: SexGenderCode
	derivedFrom: string
}


type SexualOrientationPayload = {
	type: "Sexual Orientation"
	method: MethodPayload
} & ({
	value: Exclude<SexualOrientationCode, typeof SEXUAL_ORIENTATION_OTHER>
	text: undefined
} | {
	value: typeof SEXUAL_ORIENTATION_OTHER
	text: string
})


type GenderIdentityPayload = {
	type: "Gender Identity"
	method: MethodPayload
}


export type PersonalCharacteristic =
	| PersonalPronounsPayload
	| EthnicityPayload
	| RacePayload
	| RecordedSexGenderPayload
	| SexualOrientationPayload
	| GenderIdentityPayload
