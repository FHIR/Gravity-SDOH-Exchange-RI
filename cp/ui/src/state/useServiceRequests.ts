import { getTask, getTasks, updateTask } from "@/api";
import { TaskWithState, UpdateTaskPayload } from "@/types";
import { computed } from "vue";
import useCollectionWithPolling from "./useCollectionWithPolling";

const { items, startPolling, markVisited, find } = useCollectionWithPolling(getTasks);

const serviceRequests = computed<TaskWithState[]>(() => items.value.map(({ item, isNew }) => ({ task: item, isNew })));

const update = async (id: string, payload: UpdateTaskPayload) => {
	await updateTask(id, payload);
	const updatedTask = await getTask(id);
	items.value = items.value.map(state => state.item.id === id ? { ...state, item: updatedTask } : state);
};


const useServiceRequests = () => ({
	serviceRequests,
	startPolling,
	markVisited,
	update,
	find
});

export default useServiceRequests;
