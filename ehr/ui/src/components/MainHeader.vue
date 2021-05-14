<script lang="ts">
import "@/assets/scss/styles.scss";
import { defineComponent, ref, watch } from "vue";
import UserInfo from "@/components/UserInfo.vue";
import { LocationQueryValue, useRoute } from "vue-router";

export default defineComponent({
	name: "MainHeader",
	components: {
		UserInfo
	},
	setup() {
		const route = useRoute();
		const app = ref<LocationQueryValue | LocationQueryValue[]>();
		const secondName = ref<LocationQueryValue | LocationQueryValue[]>();

		watch(() => route.query.app, () => {
			app.value = route.query.app;
			secondName.value = route.query.secondName;
		},{ immediate: true });

		return {
			app,
			secondName
		};
	}
});
</script>

<template>
	<el-header
		class="main-header"
		height="80"
	>
		<div class="logo-container">
			<img
				class="image"
				src="~@/assets/images/logo.svg"
				alt="logo"
			>
			<span class="name">{{ app }} <span
				v-if="secondName"
				class="grey"
			>({{ secondName }})</span></span>
		</div>
		<el-menu
			mode="horizontal"
			:router="true"
			:default-active="$route.path"
		>
			<el-menu-item index="/patients">
				Patients
			</el-menu-item>
			<el-menu-item index="/surveys">
				SDOH Surveys
			</el-menu-item>
			<el-menu-item index="/referral">
				Referral Organizations
			</el-menu-item>
			<el-menu-item index="/payers">
				Payers
			</el-menu-item>
		</el-menu>
		<div class="right-container">
			<UserInfo />
		</div>
	</el-header>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.main-header {
	display: flex;
	flex-direction: row;
	height: 80px;
	border-bottom: 1px solid $global-base-border-color;
}

.logo-container {
	display: flex;
	flex-direction: column;
	justify-content: center;
	min-width: 155px;
	border-right: 1px solid $global-base-border-color;
	margin-right: 45px;
	padding-right: 10px;

	.name {
		font-size: $global-font-size;
		font-weight: $global-font-weight-bold;
	}

	.image {
		width: 56px;
		height: 38px;
		margin-bottom: 6px;
	}
}

.right-container {
	margin-left: auto;
}

.grey {
	color: $grey;
	font-weight: 500;
}
</style>
