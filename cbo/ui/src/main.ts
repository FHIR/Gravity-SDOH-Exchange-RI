import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import element from "./vendors/element-plus";
import "./vendors/normalize";
import "./vendors/lodash";
import moment from "moment";

const app = createApp(App);

app.use(store);
app.use(router);
element(app);
app.mount("#app");

app.config.globalProperties.$filters = {
	formatDateTime(value: string): string {
		return moment(value).format("MMM DD, YYYY, H:mm A");
	}
};
