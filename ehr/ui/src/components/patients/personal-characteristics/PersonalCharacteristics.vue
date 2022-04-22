<script lang="ts">
import PersonalCharacteristicsTable from "./PersonalCharacteristicsTable.vue";
import Dialog from "./Dialog.vue";
import NoItems from "@/components/patients/NoItems.vue";
import { ref } from "vue";
import { PersonalCharacteristic } from "@/types/personal-characteristics";


export default {
	components: {
		PersonalCharacteristicsTable,
		Dialog,
		NoItems
	},
	setup () {
		const addDialogOpen = ref(false);
		const data: PersonalCharacteristic[] = [];
		return {
			data,
			addDialogOpen
		};
	}
};
</script>

<template>
	<div>
		<NoItems
			v-if="data.length === 0"
			message="No Personal Characteristics Yet"
			button-label="Add Personal Characteristics"
			@add-item="addDialogOpen = true"
		/>
		<PersonalCharacteristicsTable
			v-if="data.length > 0"
			:data="data"
		/>
		<Dialog
			:visible="addDialogOpen"
			@close="addDialogOpen = false"
		/>
	</div>
</template>


<style scoped>

</style>
