import { getOurTasks } from "@/api";
import { TaskWithState } from "@/types";
import { computed } from "vue";
import useCollectionWithPolling from "./useCollectionWithPolling";

const { items, startPolling, markVisited, find } = useCollectionWithPolling(getOurTasks);

const ourTasks = computed<TaskWithState[]>(() => items.value.map(({ item, isNew }) => ({ task: item, isNew })));

const useOurTasks = () => ({
	ourTasks,
	startPolling,
	markVisited,
	find
});

export default useOurTasks;