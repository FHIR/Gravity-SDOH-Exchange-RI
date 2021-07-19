import { ElNotification } from "element-plus";

export const showDefaultNotification = (message: string): void => {
	ElNotification({
		title: "Notification",
		iconClass: "notification-bell",
		duration: 5000,
		message
	});
};
