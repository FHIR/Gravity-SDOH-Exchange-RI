<script lang="ts">
import "@/assets/scss/styles.scss";
import { defineComponent } from "vue";
import UserInfo from "@/components/UserInfo.vue";

export default defineComponent({
	name: "MainHeader",
	components: {
		UserInfo
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
			<span class="name">EHR SDOH Exchange</span>
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
	width: 155px;
	border-right: 1px solid $global-base-border-color;
	margin-right: 45px;

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
</style>
