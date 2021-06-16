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
	ElButton,
	ElDialog,
	ElForm,
	ElFormItem,
	ElInput,
	ElSelect,
	ElOption,
	ElDivider,
	ElDatePicker,
	ElCheckbox,
	ElPopover,
	ElNotification,
	ElDropdown,
	ElDropdownMenu,
	ElDropdownItem
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
	app.use(ElDialog);
	app.use(ElForm);
	app.use(ElFormItem);
	app.use(ElInput);
	app.use(ElSelect);
	app.use(ElOption);
	app.use(ElDivider);
	app.use(ElDatePicker);
	app.use(ElCheckbox);
	app.use(ElNotification);
	app.use(ElPopover);
	app.use(ElDropdown);
	app.use(ElDropdownMenu);
	app.use(ElDropdownItem);

	app.config.globalProperties.$notify = ElNotification;

};
