<script lang="ts">
import { defineComponent, computed } from "vue";
import { ContextModule } from "@/store/modules/context";
import { Phone, Email } from "@/types";

export default defineComponent({
	name: "PatientInfo",
	setup() {
		const name = computed<string | null | undefined>(() => ContextModule.patient?.name || "N/A");
		const id = computed<string | null | undefined>(() => ContextModule.patient?.id || "N/A");
		const dob = computed<string | null | undefined>(() => ContextModule.patient?.dob || "N/A");
		const gender = computed<string | null | undefined>(() => ContextModule.patient?.gender || "N/A");
		const lang = computed<string | null | undefined>(() => ContextModule.patient?.language || "N/A");
		const address = computed<string | null | undefined>(() => ContextModule.patient?.address || "N/A");
		const employmentStatus = computed<string | null | undefined>(() => ContextModule.patient?.employmentStatus || "N/A");
		const race = computed<string | null | undefined>(() => ContextModule.patient?.race || "N/A");
		const ethnicity = computed<string | null | undefined>(() => ContextModule.patient?.ethnicity || "N/A");
		const educationLevel = computed<string | null | undefined>(() => ContextModule.patient?.education || "N/A");
		const maritalStatus = computed<string | null | undefined>(() => ContextModule.patient?.maritalStatus || "N/A");
		const insurance = computed<string | null | undefined>(() => ContextModule.patient?.insurances.join(", ") || "N/A");

		const phone = computed<string | null | undefined>(() => ContextModule.patient?.phones[0]?.phone || "N/A");
		const allPhones = computed<Phone[]>(() => ContextModule.patient?.phones || []);
		const email = computed<string | null | undefined>(() => ContextModule.patient?.emails[0]?.email || "N/A");
		const allEmails = computed<Email[]>(() => ContextModule.patient?.emails || []);


		return {
			name,
			id,
			dob,
			gender,
			lang,
			address,
			phone,
			email,
			employmentStatus,
			race,
			ethnicity,
			educationLevel,
			maritalStatus,
			insurance,
			allPhones,
			allEmails
		};
	}
});
</script>

<template>
	<el-card class="patient-info">
		<div class="heading">
			<span class="name">{{ name }}</span>
			<div class="insurance">
				<span class="label">Insurance: </span>
				<span class="value">{{ insurance }}</span>
			</div>
		</div>
		<el-row>
			<el-col
				:span="7"
				class="info-block"
			>
				<div class="info-item">
					<div class="label">
						Patient ID:
					</div>
					<div class="value">
						{{ id }}
					</div>
				</div>
				<div class="info-item">
					<div class="label">
						Day of Birthday:
					</div>
					<div class="value">
						{{ dob }}
					</div>
				</div>
				<div class="info-item">
					<div class="label">
						Gender:
					</div>
					<div class="value">
						{{ gender }}
					</div>
				</div>
				<div class="info-item">
					<div class="label">
						Primary Language:
					</div>
					<div class="value">
						{{ lang }}
					</div>
				</div>
			</el-col>
			<el-col
				:span="10"
				class="info-block"
			>
				<div class="info-item">
					<div class="label">
						Home Address:
					</div>
					<div class="value">
						{{ address }}
					</div>
				</div>
				<div class="info-item">
					<div class="label">
						Phone Number:
					</div>
					<div class="value">
						{{ phone }}

						<el-popover
							v-if="allPhones.length > 1"
							placement="right"
							trigger="hover"
							width="auto"
						>
							<template #reference>
								<span class="info-icon"></span>
							</template>
							<table class="info-icon-content">
								<tr
									v-for="phone in allPhones"
									:key="phone.phone"
								>
									<td class="label">
										{{ phone.use }}:
									</td>
									<td class="value">
										{{ phone.phone }}
									</td>
								</tr>
							</table>
						</el-popover>
					</div>
				</div>
				<div class="info-item">
					<div class="label">
						Email Address:
					</div>
					<div class="value">
						{{ email }}

						<el-popover
							v-if="allEmails.length > 1"
							placement="right"
							trigger="hover"
							width="auto"
						>
							<template #reference>
								<span class="info-icon"></span>
							</template>
							<table class="info-icon-content">
								<tr
									v-for="email in allEmails"
									:key="email.email"
								>
									<td class="label">
										{{ email.use }}:
									</td>
									<td class="value">
										{{ email.email }}
									</td>
								</tr>
							</table>
						</el-popover>
					</div>
				</div>
				<div class="info-item">
					<div class="label">
						Employment Status:
					</div>
					<div class="value">
						{{ employmentStatus }}
					</div>
				</div>
			</el-col>
			<el-col
				:span="7"
				class="info-block"
			>
				<div class="info-item">
					<div class="label">
						Race:
					</div>
					<div class="value">
						{{ race }}
					</div>
				</div>
				<div class="info-item">
					<div class="label">
						Ethnicity:
					</div>
					<div class="value">
						{{ ethnicity }}
					</div>
				</div>
				<div class="info-item">
					<div class="label">
						Education Level:
					</div>
					<div class="value">
						{{ educationLevel }}
					</div>
				</div>
				<div class="info-item">
					<div class="label">
						Marital Status:
					</div>
					<div class="value">
						{{ maritalStatus }}
					</div>
				</div>
			</el-col>
		</el-row>
	</el-card>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.patient-info {
	height: 100%;
}

.heading {
	display: flex;
	justify-content: space-between;
	margin-bottom: 25px;

	.name {
		font-size: $global-xxlarge-font-size;
		font-weight: $global-font-weight-medium;
	}
}

.info-block {
	border-right: 1px solid $global-base-border-color;
	padding-left: 20px;
	padding-right: 20px;

	&:last-child {
		border-right: 0;
		padding-right: 0;
	}

	&:first-child {
		padding-left: 0;
	}
}

.info-item {
	display: flex;
	margin-bottom: 16px;

	&:last-child {
		margin-bottom: 0;
	}

	.label {
		width: 155px;
		color: $grey;
		flex-shrink: 0;
	}

	.value {
		display: flex;
		align-items: baseline;

		@include dont-break-out();
	}

	.info-icon {
		margin-left: 10px;
		flex-shrink: 0;

		@include icon("~@/assets/images/info.svg", 12px, 12px);
	}
}

.info-icon-content {
	.label {
		color: $global-text-muted-color;
	}
}

.insurance {
	display: flex;
	align-items: flex-end;

	.label {
		color: $grey;
		margin-right: 5px;
	}

	.value {
		font-weight: $global-font-weight-medium;
	}
}
</style>
