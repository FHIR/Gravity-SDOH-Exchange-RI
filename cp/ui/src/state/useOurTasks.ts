import { getOurTask, getOurTasks, updateOurTask } from "@/api";
import { TaskWithState } from "@/types";
import { computed } from "vue";
import useCollectionWithPolling from "./useCollectionWithPolling";

const { items, startPolling, markVisited, find } = useCollectionWithPolling(getOurTasks);

const ourTasks = computed<TaskWithState[]>(() => items.value.map(({ item, isNew }) => ({ task: item, isNew })));

const cancelTask = async (id: string, statusReason: string) => {
	await updateOurTask(id, { status: "Cancelled", statusReason });
	const updatedTask = await getOurTask(id);
	items.value = items.value.map(state => state.item.id === id ? { ...state, item: updatedTask } : state);
};

const useOurTasks = () => ({
	ourTasks,
	startPolling,
	markVisited,
	find,
	cancelTask
});

export default useOurTasks;