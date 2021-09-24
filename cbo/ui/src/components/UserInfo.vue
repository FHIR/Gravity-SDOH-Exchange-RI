<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "UserInfo"
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
					<span class="name">Colin</span>
					<span class="surname">Brooks</span>
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
	margin-left: 50px;
}

.details {
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
	font-size: $global-font-size;
	font-weight: $global-font-weight-bold;

	.surname {
		margin-left: 5px;
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
