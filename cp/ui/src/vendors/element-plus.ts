import { App } from "vue";
import "@/assets/scss/element-plus/index.scss";

import "element-plus/packages/theme-chalk/src/tabs.scss";
import "element-plus/packages/theme-chalk/src/tab-pane.scss";
import "element-plus/packages/theme-chalk/src/input.scss";
import "element-plus/packages/theme-chalk/src/table.scss";
import "element-plus/packages/theme-chalk/src/table-column.scss";
import "element-plus/packages/theme-chalk/src/dialog.scss";
import "element-plus/packages/theme-chalk/src/button.scss";
import "element-plus/packages/theme-chalk/src/radio.scss";
import "element-plus/packages/theme-chalk/src/select.scss";
import "element-plus/packages/theme-chalk/src/option.scss";
import "element-plus/packages/theme-chalk/src/radio-group.scss";
import "element-plus/packages/theme-chalk/src/form.scss";
import "element-plus/packages/theme-chalk/src/form-item.scss";
import "element-plus/packages/theme-chalk/src/icon.scss";
import "element-plus/packages/theme-chalk/src/notification.scss";
import "element-plus/packages/theme-chalk/src/tag.scss";
import "element-plus/packages/theme-chalk/src/loading.scss";
import "element-plus/packages/theme-chalk/src/popover.scss";

import {
	ElTabs,
	ElTabPane,
	ElInput,
	ElTable,
	ElTableColumn,
	ElDialog,
	ElButton,
	ElRadio,
	ElSelect,
	ElOption,
	ElRadioGroup,
	ElForm,
	ElFormItem,
	ElIcon,
	ElTag,
	ElLoading,
	ElPopover
} from "element-plus";

export default (app: App) => {
	app.use(ElTabs);
	app.use(ElTabPane);
	app.use(ElInput);
	app.use(ElTable);
	app.use(ElTableColumn);
	app.use(ElDialog);
	app.use(ElButton);
	app.use(ElRadio);
	app.use(ElSelect);
	app.use(ElOption);
	app.use(ElRadioGroup);
	app.use(ElForm);
	app.use(ElFormItem);
	app.use(ElIcon);
	app.use(ElTag);
	app.use(ElLoading);
	app.use(ElPopover);
};
