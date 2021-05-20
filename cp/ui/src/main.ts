import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import element from "./vendors/element-plus";
import "./vendors/normalize";
import "./vendors/lodash";

const app = createApp(App);

app.use(store);
app.use(router);
element(app);
app.mount("#app");
