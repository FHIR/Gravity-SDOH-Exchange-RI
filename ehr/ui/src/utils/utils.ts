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

export const prepareOccurrence = (occurrence: string): { end: string, start?: string } => {
	let finalizeOccurrence;
	if (occurrence.length > 1) {
		finalizeOccurrence = {
			start: moment(occurrence[0]).format("YYYY-MM-DD[T]HH:mm:ss"),
			end: moment(occurrence[1]).format("YYYY-MM-DD[T]HH:mm:ss")
		};
	} else {
		finalizeOccurrence = {
			end: moment(occurrence).format("YYYY-MM-DD[T]HH:mm:ss")
		};
	}
	return finalizeOccurrence;
};

export const file2Base64 = (file: File): Promise<string> =>
	new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.readAsDataURL(file);
		reader.onload = () => {
			if (typeof reader.result !== "string") {
				resolve("");
			} else {
				resolve(reader.result.replace(/^data:.*?,/, ""));
			}
		};
		reader.onerror = error => reject(error);
	});
