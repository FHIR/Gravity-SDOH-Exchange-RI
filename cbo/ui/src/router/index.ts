import { createRouter, createWebHashHistory, RouteRecordRaw, NavigationGuardNext, RouteLocationNormalized } from "vue-router";
import Home from "@/views/Home.vue";
import Login from "@/views/Login.vue";
import Servers from "@/views/Servers.vue";
import { AuthModule } from "@/store/modules/auth";

const isAuthenticated = (to: RouteLocationNormalized, RouteLocationNormalized: any, next: NavigationGuardNext) => {
	if (AuthModule.isAuthenticated) {
		next();
		return;
	}
	next("/login");
};

const shouldBeAuthenticated = (to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) => {
	if (AuthModule.isAuthenticated) {
		next("/");
		return;
	}
	next();
};

const checkOutsideParams = (to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) => {
	const { query } = to;

	if (query.serverName && query.fhirServerUrl && query.authServerUrl && query.clientId) {
		next({ path: "/servers", query: { ...query } });
		return;
	}
	next();
};

const routes: Array<RouteRecordRaw> = [
	{
		path: "/",
		name: "Home",
		component: Home,
		beforeEnter: [isAuthenticated, checkOutsideParams]
	},
	{
		path: "/servers",
		name: "Servers",
		component: Servers,
		beforeEnter: isAuthenticated
	},
	{
		path: "/login",
		name: "login",
		component: Login,
		beforeEnter: shouldBeAuthenticated
	}
];

const router = createRouter({
	history: createWebHashHistory(process.env.BASE_URL),
	routes
});

export default router;
