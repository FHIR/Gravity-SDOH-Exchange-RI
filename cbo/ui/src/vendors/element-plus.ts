import { App } from "vue";

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
	ElAlert,
	ElTable,
	ElTableColumn,
	ElSelect,
	ElOption,
	ElTag,
	ElDialog,
	ElDivider,
	ElLoading,
	ElNotification
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
import "element-plus/es/components/table/style/css";
import "element-plus/es/components/table-column/style/css";
import "element-plus/es/components/dialog/style/css";
import "element-plus/es/components/divider/style/css";
import "element-plus/es/components/select/style/css";
import "element-plus/es/components/option/style/css";
import "element-plus/es/components/tag/style/css";
import "element-plus/es/components/loading/style/css";
import "element-plus/es/components/notification/style/css";

import "@/assets/scss/element-plus/index.scss";

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
	app.use(ElTable);
	app.use(ElTableColumn);
	app.use(ElDialog);
	app.use(ElDivider);
	app.use(ElSelect);
	app.use(ElOption);
	app.use(ElTag);
	app.use(ElLoading);
	app.use(ElNotification);
};
