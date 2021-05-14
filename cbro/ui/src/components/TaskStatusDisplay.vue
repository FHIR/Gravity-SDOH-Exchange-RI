<script lang="ts">
import { defineComponent, PropType, computed } from "vue";
import { TaskStatus } from "@/types";


export default defineComponent({
	props: {
		status: {
			type: String as PropType<TaskStatus>,
			required: true
		},
		iconOnly: {
			type: Boolean,
			default: false
		},
		small: {
			type: Boolean,
			default: false
		}
	}
});
</script>

<template>
	<div class="task-status">
		<div
			:class="{
				[status.toLowerCase().replace(/\ /g, '')]: true,
				small: small
			}"
			class="icon"
		>
		</div>
		<div
			v-if="!iconOnly"
			class="label"
		>
			{{ status }}
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.task-status {
	display: flex;
	align-items: center;

	.icon {
		width: 20px;
		height: 20px;
		flex-shrink: 0;
		background-size: contain;
		background-repeat: no-repeat;
		background-position: center;

		&.small {
			width: 14px;
			height: 14px;
		}

		&.accepted { background-image: url("~@/assets/images/status-accepted.svg"); }

		&.cancelled { background-image: url("~@/assets/images/status-cancelled.svg"); }

		&.completed { background-image: url("~@/assets/images/status-completed.svg"); }

		&.draft { }

		&.enteredinerror { }

		&.failed { background-image: url("~@/assets/images/status-failed.svg"); }

		&.inprogress { background-image: url("~@/assets/images/status-inprogress.svg"); }

		&.onhold { background-image: url("~@/assets/images/status-onhold.svg"); }

		&.ready { }

		&.received { background-image: url("~@/assets/images/status-received.svg"); }

		&.rejected { background-image: url("~@/assets/images/status-rejected.svg"); }

		&.requested { background-image: url("~@/assets/images/status-requested.svg"); }
	}

	.label {
		margin-left: 5px;
	}
}
</style>
