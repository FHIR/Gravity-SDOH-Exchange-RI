import { App } from "vue";
import "@/assets/scss/element-plus/index.scss";
import "element-plus/packages/theme-chalk/src/tabs.scss";
import "element-plus/packages/theme-chalk/src/tab-pane.scss";
import "element-plus/packages/theme-chalk/src/popover.scss";

import {
	ElContainer,
	ElHeader,
	ElMain,
	ElPopover,
	ElTabs,
	ElTabPane
} from "element-plus";

export default (app: App) => {
	app.use(ElContainer);
	app.use(ElHeader);
	app.use(ElMain);
	app.use(ElPopover);
	app.use(ElTabs);
	app.use(ElTabPane);
};
