<script lang="ts">
import { defineComponent, computed } from "vue";
import { ActiveTabModule } from "@/store/activeTab";
import router from "@/router";
import { useRoute } from "vue-router";
import UserInfo from "@/components/UserInfo.vue";
import { TasksModule } from "@/store/modules/tasks";

export default defineComponent({
	name: "MainHeader",
	components: { UserInfo },
	setup() {
		const route = useRoute();
		const activeTaskLength = computed<number>(() => TasksModule.activeRequests.length);
		const inactiveTaskLength = computed<number>(() => TasksModule.inactiveRequests.length);
		const currentRoute = computed<string>(() => route.path);
		const lastSyncDate = computed<string>(() => TasksModule.lastSyncDate);

		const handleTabClick = (tab: any) => ActiveTabModule.setActiveTab(tab.paneName);
		const handleSyncClick = () => TasksModule.getTasks();

		return {
			activeTabName : ActiveTabModule.activeTab,
			handleTabClick,
			router,
			currentRoute,
			activeTaskLength,
			inactiveTaskLength,
			lastSyncDate,
			handleSyncClick
		};
	}
});
</script>

<template>
	<div class="app-header">
		<div class="cbo">
			<img
				class="gravity-logo"
				src="~@/assets/images/logo.svg"
				alt="logo"
			>
			<div class="title">
				CBO
			</div>
		</div>
		<div
			v-if="currentRoute === '/'"
			class="nav-tabs"
		>
			<el-tabs
				v-model="activeTabName"
				@tab-click="handleTabClick"
			>
				<el-tab-pane
					name="ar"
				>
					<template #label>
						<div class="label-wrap">
							{{ `Active Requests (${activeTaskLength})` }}
						</div>
					</template>
				</el-tab-pane>
				<el-tab-pane
					name="ir"
				>
					<template #label>
						<div class="label-wrap">
							{{ `Inactive Requests (${inactiveTaskLength})` }}
						</div>
					</template>
				</el-tab-pane>
			</el-tabs>
		</div>
		<div
			v-if="currentRoute === '/servers'"
			class="sub-header"
		>
			Manage Servers
		</div>
		<div
			class="sync"
			:class="{ 'sync-margin' : currentRoute === '/servers' }"
		>
			Last Synchronization: {{ lastSyncDate ? $filters.formatDateTime(lastSyncDate) : "" }}
		</div>
		<div class="right-container">
			<el-button
				plain
				round
				@click="handleSyncClick"
			>
				Synchronize
			</el-button>
			<el-button
				plain
				round
				@click="currentRoute === '/' ? router.push('/servers') : router.push('/')"
			>
				{{ currentRoute === "/" ? "Manage Servers" : "Back to Requests" }}
			</el-button>
		</div>
		<UserInfo />
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.app-header {
	height: 80px;
	flex-shrink: 0;
	border: $global-border;
	display: flex;
	align-items: center;
	background-color: $alice-blue;
	padding-right: 45px;
	box-shadow: 0 2px 5px 0 #33333340;
	margin-bottom: 30px;
	z-index: 1;

	.cbo {
		border-right: $global-border;
		padding: 10px 45px 0 45px;
		height: 100%;

		.gravity-logo {
			width: 56px;
		}

		.title {
			margin-top: 4px;
			font-size: $global-font-size;
			font-weight: 500;
			line-height: 16px;

			.secondary {
				color: $grey;
			}
		}
	}

	.nav-tabs {
		height: 100%;

		::v-deep(.el-tabs) {
			height: 100%;

			.el-tabs__header {
				height: 100%;
				margin: 0;
			}

			.el-tabs__nav-wrap {
				height: 100%;
			}

			.el-tabs__nav-wrap::after {
				display: none;
			}

			.el-tabs__nav-scroll {
				height: 100%;
			}

			.el-tabs__nav {
				height: 100%;
			}

			.el-tabs__active-bar {
				background-color: $global-primary-color;
			}

			.el-tabs__item {
				height: 100%;
				padding: 20px 0 0 0;
				width: 50%;
			}

			.el-tabs__item .label-wrap {
				padding: 0 45px;
				height: 40px;
				font-size: $global-medium-font-size;
				font-weight: $global-font-weight-normal;
				color: $global-text-color;
			}

			.el-tabs__item.is-active .label-wrap {
				font-weight: $global-font-weight-medium;
			}

			.el-tabs__item:not(:last-child) .label-wrap {
				border-right: $global-border;
			}
		}
	}

	.sub-header {
		padding: 0 45px;
		color: $global-text-color;
		font-weight: $global-font-weight-medium;
		font-size: $global-medium-font-size;
	}

	.sync {
		margin-left: 90px;
	}

	.sync-margin {
		margin-left: 320px;
	}

	.right-container {
		margin-left: auto;
		display: flex;
	}
}
</style>
