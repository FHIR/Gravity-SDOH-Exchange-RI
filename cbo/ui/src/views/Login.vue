<script lang="ts">
import LoginForm from "@/components/LoginForm.vue";
import { defineComponent } from "vue";

export default defineComponent({
	name: "Login",
	components: {
		LoginForm
	}
});
</script>

<template>
	<div class="login-page">
		<img
			class="logo"
			src="~@/assets/images/logo.svg"
			alt="logo"
			width="134"
			height="91"
		>
		<LoginForm />
	</div>
</template>

<style lang="scss" scoped>
.login-page {
	height: 100vh;
	padding: 20px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;

	.logo {
		margin-bottom: 40px;
	}
}
</style>
