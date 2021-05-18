<script lang="ts">
import "@/assets/scss/styles.scss";
import { defineComponent, computed } from "vue";
import { ContextModule } from "@/store/modules/context";

export default defineComponent({
	name: "UserInfo",
	setup() {
		const userName = computed<string | null | undefined>(() => ContextModule.user?.name);
		const userType = computed<string | null | undefined>(() => ContextModule.user?.userType);

		return {
			userName,
			userType
		};
	}
});
</script>

<template>
	<div class="user-info">
		<div class="avatar">
			<img
				src="~@/assets/images/avatar.svg"
				alt="avatar"
				class="image"
			>
		</div>
		<el-popover
			placement="bottom"
			:width="140"
			:offset="-10"
			trigger="click"
			:append-to-body="false"
		>
			<a
				class="logout"
				href="/logout"
			>Logout</a>
			<template #reference>
				<div class="details">
					<span class="name">{{ userName }}</span>
					<span class="type">{{ userType }}</span>
				</div>
			</template>
		</el-popover>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.user-info {
	height: 100%;
	display: flex;
	justify-content: center;
}

.details {
	display: flex;
	flex-direction: column;
	justify-content: center;
	cursor: pointer;

	.name {
		font-size: $global-font-size;
		font-weight: $global-font-weight-bold;
	}

	.type {
		font-size: $global-small-font-size;
	}
}

.avatar {
	display: flex;
	flex-direction: column;
	justify-content: center;
	margin-right: 20px;

	.image {
		width: 42px;
		height: 42px;
	}
}

::v-deep(.el-popper.is-light) {
	text-align: center;
	font-size: $global-font-size;
	font-weight: $global-font-weight-normal;
	cursor: pointer;
}
</style>
