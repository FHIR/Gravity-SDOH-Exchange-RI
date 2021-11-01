import { getServers, createServer, updateServer, deleteServer } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Server, NewServerPayload, UpdateServerPayload } from "@/types";

export interface IServers {
	servers: Server[],
	isLoading: boolean
}

@Module({ dynamic: true, store, name: "servers" })
class Servers extends VuexModule implements IServers {
	servers: Server[] = [];
	isLoading: boolean = false;

	@Mutation
	setServers(payload: Server[]) {
		this.servers = payload;
	}

	@Mutation
	changeServer(payload: Server) {
		this.servers = this.servers.map(s => s.id === payload.id ? payload : s);
	}

	@Mutation
	removeServer(payload: number) {
		this.servers = this.servers.filter(s => s.id !== payload);
	}

	@Mutation
	setIsLoading(payload: boolean) {
		this.isLoading = payload;
	}

	@Action
	async getServers(): Promise<void> {
		this.setIsLoading(true);
		try {
			const data = await getServers();

			this.setServers(data);
		} finally {
			this.setIsLoading(false);
		}
	}

	@Action
	async createServer(payload: NewServerPayload): Promise<void> {
		const newServer = await createServer(payload);

		this.setServers([...this.servers, newServer]);
	}

	@Action
	async updateServer(payload: UpdateServerPayload): Promise<void> {
		const updatedServer = await updateServer(payload);

		this.changeServer(updatedServer);
	}

	@Action
	async deleteServer(payload: number): Promise<void> {
		await deleteServer(payload);
		this.removeServer(payload);
	}
}

export const ServersModule = getModule(Servers);
