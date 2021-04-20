export type Patient = {
	address: string | null,
	age: number | null,
	dob: string | null,
	gender: string | null,
	id: string | null,
	name: string | null
	//todo: keys below we don't have in our api yet
	language: string | null,
	phone: string | null,
	email: string | null,
	employmentStatus: string | null,
	race: string | null,
	ethnicity: string | null,
	educationLevel: string | null,
	maritalStatus: string | null,
	insurance: string | null
};

export type User = {
	id: string | null,
	name: string | null,
	userType: string | null
};

export type ContextResponse = {
	patient: Patient,
	user: User
}
