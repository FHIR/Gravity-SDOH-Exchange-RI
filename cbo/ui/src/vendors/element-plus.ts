import { App } from "vue";
import "@/assets/scss/element-plus/index.scss";

import {
	ElContainer,
	ElHeader,
	ElMain,
	ElPopover,
	ElTabs,
	ElTabPane,
	ElForm,
	ElFormItem,
	ElButton,
	ElInput,
	ElAlert
} from "element-plus";
//todo: use unplugin-vue-components
import "element-plus/es/components/container/style/css";
import "element-plus/es/components/header/style/css";
import "element-plus/es/components/main/style/css";
import "element-plus/es/components/popover/style/css";
import "element-plus/es/components/tabs/style/css";
import "element-plus/es/components/tab-pane/style/css";
import "element-plus/es/components/form/style/css";
import "element-plus/es/components/form-item/style/css";
import "element-plus/es/components/button/style/css";
import "element-plus/es/components/input/style/css";
import "element-plus/es/components/alert/style/css";

export default (app: App) => {
	app.use(ElContainer);
	app.use(ElHeader);
	app.use(ElMain);
	app.use(ElPopover);
	app.use(ElTabs);
	app.use(ElTabPane);
	app.use(ElForm);
	app.use(ElFormItem);
	app.use(ElButton);
	app.use(ElInput);
	app.use(ElAlert);
};
