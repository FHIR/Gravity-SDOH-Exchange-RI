import { App } from "vue";
import "@/assets/scss/element-plus/index.scss";

import {
	ElContainer,
	ElHeader,
	ElMain,
	ElMenu,
	ElMenuItem,
	ElCol,
	ElRow,
	ElCard,
	ElBreadcrumb,
	ElBreadcrumbItem,
	ElTabs,
	ElTabPane,
	ElRadio,
	ElRadioGroup,
	ElRadioButton,
	ElTable,
	ElTableColumn,
	ElLoading,
	ElButton
} from "element-plus";

export default (app: App) => {
	app.use(ElContainer);
	app.use(ElHeader);
	app.use(ElMain);
	app.use(ElMenu);
	app.use(ElMenuItem);
	app.use(ElCol);
	app.use(ElRow);
	app.use(ElCard);
	app.use(ElBreadcrumb);
	app.use(ElBreadcrumbItem);
	app.use(ElTabs);
	app.use(ElTabPane);
	app.use(ElRadio);
	app.use(ElRadioGroup);
	app.use(ElRadioButton);
	app.use(ElTable);
	app.use(ElTableColumn);
	app.use(ElLoading);
	app.use(ElButton);
};
