<script lang="ts">
import { defineComponent, ref } from "vue";

export default defineComponent({
	name: "Login",
	setup() {
		const active = ref<string>("");
		const setActive = (item: string) => active.value = item;
		return {
			active,
			setActive
		};
	}
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
					:class="{ active: active === 'provider' }"
					@click="setActive('provider')"
				>
					<div class="circle circle-logan">
						<span class="icon"></span>
					</div>
					Provider/EHR
				</li>
				<li
					class="menu-item"
					:class="{ active: active === 'payer' }"
					@click="setActive('payer')"
				>
					<div class="circle circle-blue">
						<span class="icon"></span>
					</div>
					Payer
				</li>
				<li
					class="menu-item"
					:class="{ active: active === 'platform-originating' }"
					@click="setActive('platform-originating')"
				>
					<div class="circle circle-cerise">
						<span class="icon"></span>
					</div>
					Coordination Platform &nbsp; <span class="description">(originating tasks)</span>
				</li>
				<li
					class="menu-item"
					:class="{ active: active === 'platform-receiving' }"
					@click="setActive('platform-receiving')"
				>
					<div class="circle circle-golden">
						<span class="icon"></span>
					</div>
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

	& .login-menu {
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

		&:hover {
			box-shadow: 0 2px 5px rgba(51, 51, 51, 0.25);
		}

		.description {
			color: $grey;
		}
	}

	.circle {
		width: 28px;
		height: 28px;
		border-radius: 50%;
		margin: 0 10px;
		display: flex;
		justify-content: center;
		align-items: center;

		&-logan {
			background-color: $logan;

			.icon {
				@include icon("~@/assets/images/provider-icon.svg", 13px);
			}
		}

		&-blue {
			background-color: $bright-blue;

			.icon {
				@include icon("~@/assets/images/payer-icon.svg", 14px);
			}
		}

		&-cerise {
			background-color: $cerise;

			.icon {
				@include icon("~@/assets/images/originaiting-icon.svg", 14px);
			}
		}

		&-golden {
			background-color: $golden-tainoi;

			.icon {
				@include icon("~@/assets/images/receiving-icon.svg", 14px);
			}
		}

		.icon {
			height: 16px;
		}
	}

	.active {
		border-color: $dodger-blue;
	}
}
</style>
