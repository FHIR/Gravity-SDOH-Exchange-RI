import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";
import Home from "@/views/Home.vue";
import Patients from "@/views/Patients.vue";
import Surveys from "@/views/Surveys.vue";
import ReferralOrgs from "@/views/ReferralOrgs.vue";
import Payers from "@/views/Payers.vue";

const routes: Array<RouteRecordRaw> = [
	{
		path: "/",
		name: "Home",
		component: Home,
		// todo: we don't have home page right now, straight to patients
		redirect: "/patients"
	}, {
		path: "/patients",
		name: "Patients",
		component: Patients
	}, {
		path: "/surveys",
		name: "Surveys",
		component: Surveys
	}, {
		path: "/referral",
		name: "Referral",
		component: ReferralOrgs
	}, {
		path: "/payers",
		name: "Payers",
		component: Payers
	}
];

const router = createRouter({
	history: createWebHashHistory(process.env.BASE_URL),
	routes
});

export default router;
