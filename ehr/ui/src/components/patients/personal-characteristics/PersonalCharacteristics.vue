<script lang="ts">
import PersonalCharacteristicsTable from "./PersonalCharacteristicsTable.vue";
import AddDialog from "./AddDialog.vue";
import ViewDialog from "./ViewDialog.vue";
import NoItems from "@/components/patients/NoItems.vue";
import { ref, computed } from "vue";
import { PersonalCharacteristic } from "@/types/personal-characteristics";
import { getPersonalCharacteristics } from "@/api";



export default {
	components: {
		PersonalCharacteristicsTable,
		AddDialog,
		ViewDialog,
		NoItems,
	},
	setup () {
		const addDialogOpen = ref(false);
		const viewDialogId = ref<string | null>(null);
		const data = ref<PersonalCharacteristic[]>([]);
		const loading = ref(false);

		const load = async () => {
			loading.value = true;
			const resp = await getPersonalCharacteristics();
			data.value = resp;
			loading.value = false;
		};

		const closeAddDialog = () => {
			addDialogOpen.value = false;
			load();
		};

		const viewDialogItem = computed(() => viewDialogId.value ? data.value.find(item => item.id === viewDialogId.value) || null : null);

		load();

		return {
			data,
			loading,
			addDialogOpen,
			closeAddDialog,
			viewDialogId,
			viewDialogItem,
		};
	}
};
</script>

<template>
	<div v-loading="loading">
		<NoItems
			v-if="data.length === 0"
			message="No Personal Characteristics Yet"
			button-label="Add Personal Characteristics"
			@add-item="addDialogOpen = true"
		/>
		<PersonalCharacteristicsTable
			v-if="data.length > 0"
			:data="data"
			@add-item="addDialogOpen = true"
			@item-clicked="viewDialogId = $event"
		/>
		<AddDialog
			:visible="addDialogOpen"
			@close="closeAddDialog"
		/>
		<ViewDialog
			:item="viewDialogItem"
			@close="viewDialogId = null"
		/>
	</div>
</template>


<style scoped>

</style>
