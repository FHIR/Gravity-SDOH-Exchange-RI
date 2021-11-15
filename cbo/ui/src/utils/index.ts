import { ElNotification } from "element-plus";
import { h } from "vue";
import TaskStatusDisplay from "@/components/TaskStatusDisplay.vue";

export const showDate = (d: string) => new Date(d).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" });
export const showDateTime = (d: string) => new Date(d).toLocaleString("en-US", { month: "short", day: "numeric", year: "numeric", hour: "numeric", minute: "numeric" });

export const poll = <T>(
	makeRequest: () => Promise<T>,
	proceed: (t: T) => boolean,
	ms: number
) => {
	const next = () => {
		setTimeout(async () => {
			try {
				const resp = await makeRequest();
				if (proceed(resp)) {
					next();
				}
			} catch {
				next();
			}
		}, ms);
	};
	next();
};

export const showDefaultNotification = (message: any): void => {
	ElNotification({
		title: "Notification",
		iconClass: "notification-bell",
		duration: 5000,
		message
	});
};

export const showUpdates = (updates: any[]) => {
	updates.forEach(update => {
		const message = h("p", [
			`CP changed status of task "${update.name}" from `,
			//todo: for some reason ts pops up error about incorrect import
			// @ts-ignore
			h(TaskStatusDisplay, {
				status: update.oldStatus,
				small: true
			}),
			" to ",
			// @ts-ignore
			h(TaskStatusDisplay,{
				status: update.newStatus,
				small: true
			})
		]);

		showDefaultNotification(message);
	});
};
