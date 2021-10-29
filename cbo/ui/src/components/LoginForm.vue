<script lang="ts">
import { defineComponent, ref, computed } from "vue";
import { AuthModule } from "@/store/modules/auth";
import router from "@/router/index";

const USERNAME = "user";
const PASSWORD = "user";

export default defineComponent({
	name: "LoginForm",
	setup () {
		const username = ref<string>("");
		const password = ref<string>("");
		const loginError = ref<boolean>(false);
		const loginButtonEnabled = computed<boolean>(() => username.value !== "" && password.value !== "");

		const handleLogin = (): void => {
			if (username.value === USERNAME && password.value === PASSWORD) {
				AuthModule.login(true);
				router.push("/");
				return;
			}

			loginError.value = true;
		};
		const hideValidation = (): void => {
			loginError.value = false;
		};

		return {
			username,
			password,
			loginError,
			loginButtonEnabled,
			handleLogin,
			hideValidation
		};
	}
});
</script>

<template>
	<div class="login-form">
		<h3>Community-Based Organization App</h3>
		<h4>Log into the CBO app</h4>
		<el-form @keyup.enter="handleLogin">
			<el-form-item>
				<el-input
					v-model="username"
					placeholder="Username"
					@input="hideValidation"
				/>
			</el-form-item>

			<el-form-item>
				<el-input
					v-model="password"
					placeholder="Password"
					:show-password="true"
					@input="hideValidation"
				/>
			</el-form-item>

			<el-button
				class="log-in-button"
				round
				plain
				:disabled="!loginButtonEnabled"
				@click="handleLogin"
			>
				Log In
			</el-button>

			<el-alert
				v-show="loginError"
				type="error"
				:closable="false"
			>
				We don’t recognize this username or password. Double-check your information and try again.
			</el-alert>
		</el-form>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.login-form {
	position: relative;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 40px;
	width: 400px;
	height: 465px;
	background-color: $global-background;
	box-shadow: 0 4px 10px rgba(51, 51, 51, 0.1);

	h3 {
		font-size: $global-medium-font-size;
		font-weight: $global-font-weight-medium;
		margin: 10px 0;
	}

	h4 {
		font-size: $global-font-size;
		font-weight: $global-font-weight-light;
		margin: 0 0 30px;
	}

	::v-deep(.el-form) {
		width: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	::v-deep(.el-form-item) {
		margin-bottom: 10px;
		width: 100%;
	}

	.log-in-button {
		margin-top: 50px;
	}

	::v-deep(.el-alert) {
		position: absolute;
		top: 0;
	}
}
</style>
