<script lang="ts">
import { defineComponent, ref } from "vue";

export default defineComponent({
	name: "Login"
});
</script>

<template>
	<div class="login">
		<div class="login-menu">
			<img
				class="image"
				alt="logo"
				src="~@/assets/images/gravity-logo.svg"
			>
			<h4 class="header">
				Select the user role under which you want to login:
			</h4>
			<ul class="menu">
				<li
					class="menu-item"
				>
					<span class="icon icon-provider"></span>
					Provider/EHR
				</li>
				<li
					class="menu-item"
				>
					<span class="icon icon-payer"></span>
					Payer
				</li>
				<li
					class="menu-item"
				>
					<span class="icon icon-originating"></span>
					Coordination Platform &nbsp; <span class="description">(originating tasks)</span>
				</li>
				<li
					class="menu-item"
				>
					<span class="icon icon-receiving"></span>
					Coordination Platform &nbsp; <span class="description">(receiving tasks)</span>
				</li>
			</ul>
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.login {
	height: 100vh;
	background-color: $aqua-haze;
	display: flex;
	justify-content: center;
	align-items: center;

	.login-menu {
		width: 700px;
		height: 615px;
		background-color: white;
		display: flex;
		flex-direction: column;
		align-items: center;
		box-shadow: 0 4px 25px 0 $gainsboro;
	}

	.image {
		margin: 100px 0 50px 0;
		width: 135px;
		height: 90px;
	}

	.header {
		font-size: $global-font-size;
		font-weight: $global-font-weight-medium;
	}

	.menu-item {
		border: 1px solid $whisper;
		width: 325px;
		height: 50px;
		display: flex;
		align-items: center;
		margin: 10px 0;
		border-radius: 5px;
		font-size: $global-font-size;
		font-weight: $global-font-weight-medium;
		cursor: pointer;

		&:active {
			border-color: $dodger-blue;
		}

		&:hover {
			box-shadow: 0 2px 5px rgba(51, 51, 51, 0.25);
		}

		.description {
			color: $grey;
		}

		.icon {
			margin: 0 10px;

			&-provider {
				@include icon("~@/assets/images/provider-icon.svg", 28px);
			}

			&-payer {
				@include icon("~@/assets/images/payer-icon.svg", 28px);
			}

			&-originating {
				@include icon("~@/assets/images/originating-icon.svg", 28px);
			}

			&-receiving {
				@include icon("~@/assets/images/receiving-icon.svg", 28px);
			}
		}
	}
}
</style>
