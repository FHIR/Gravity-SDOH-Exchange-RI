import { poll } from "@/utils";
import { computed, ref, Ref } from "vue";

type WithState<T> = {
	item: T,
	isNew: boolean
}

const useCollectionWithPolling = <T extends { id: string }>(getItems: () => Promise<T[]>) => {
	const items: Ref<WithState<T>[]> = ref([]);

	const isItemNew = (it: T) => {
		const existing = items.value.find(({ item }) => it.id === item.id);
		return existing === undefined || existing.isNew;
	};

	const updateItems = (newResponse: T[]) => {
		items.value = newResponse.map(item => ({
			item,
			isNew: isItemNew(item)
		}));
	};

	const find = (id: string) => computed(() => items.value.find(({ item }) => item.id === id)?.item || null);

	const markVisited = (id: string) => {
		items.value = items.value.map(item => item.item.id === id ? { ...item, isNew: false } : item);
	};

	const startPolling = (interval=5000) => {
		getItems().then(resp => {
			items.value = resp.map(item => ({ item, isNew: false }));
		});

		poll(
			getItems,
			newResp => {
				updateItems(newResp);
				return true;
			},
			interval
		);
	};

	return {
		items,
		startPolling,
		find,
		markVisited
	};
};

export default useCollectionWithPolling;