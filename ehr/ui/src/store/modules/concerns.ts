import { VuexModule, Module, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Concern } from "@/types";

export interface IConcerns {
	concerns: Concern[]
}

@Module({ dynamic: true, store, name: "concern" })
class Concerns extends VuexModule implements IConcerns {
	concerns: Concern[] = [{ status: "Active", id: "123123",  name: "Hunger Vital Signs", createdAt: "2021-05-18T14:15:08", category: "Food Insecurity", basedOn: "Past", actions: "send to patient" }, { status: "PromotedOrResolved", id: "123123",  name: "Hunger Vital Signs", createdAt: "2021-05-18T14:15:08", category: "Food Insecurity", basedOn: "Past", actions: "send to patient" }];
}

export const ConcernsModule = getModule(Concerns);
