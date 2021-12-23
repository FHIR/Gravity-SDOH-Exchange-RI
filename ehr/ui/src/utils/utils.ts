import { ElNotification } from "element-plus";
import moment from "moment";

export const showDefaultNotification = (message: string): void => {
	ElNotification({
		title: "Notification",
		iconClass: "notification-bell",
		duration: 5000,
		message
	});
};

export const prepareOccurrence = (occurrence: string[]): { end: string, start?: string } => ({
	start: moment(occurrence[0]).format("YYYY-MM-DD[T]HH:mm:ss"),
	end: moment(occurrence[1]).format("YYYY-MM-DD[T]HH:mm:ss")
});
