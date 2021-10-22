import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";
import Home from "@/views/Home.vue";
import Login from "@/views/Login.vue";
import Servers from "@/views/Servers.vue";
import { AuthModule } from "@/store/modules/auth";

const isAuthenticated = (to: any, from: any, next: any) => {
	if (AuthModule.isAuthenticated) {
		next();
		return;
	}
	next("/login");
};

const shouldBeAuthenticated = (to: any, from: any, next: any) => {
	if (AuthModule.isAuthenticated) {
		next("/");
		return;
	}
	next();
};

const routes: Array<RouteRecordRaw> = [
	{
		path: "/",
		name: "Home",
		component: Home,
		beforeEnter: isAuthenticated
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
