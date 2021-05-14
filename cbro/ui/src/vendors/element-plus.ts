import { App } from "vue";
import "@/assets/scss/element-plus/index.scss";

import "element-plus/packages/theme-chalk/src/tabs.scss";
import "element-plus/packages/theme-chalk/src/tab-pane.scss";
import "element-plus/packages/theme-chalk/src/input.scss";
import "element-plus/packages/theme-chalk/src/table.scss";
import "element-plus/packages/theme-chalk/src/table-column.scss";

import {
	ElTabs,
	ElTabPane,
	ElInput,
	ElTable,
	ElTableColumn
} from "element-plus";

export default (app: App) => {
	app.use(ElTabs);
	app.use(ElTabPane);
	app.use(ElInput);
	app.use(ElTable);
	app.use(ElTableColumn);
};
