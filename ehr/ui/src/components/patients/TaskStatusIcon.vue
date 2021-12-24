<script lang="ts">
import { defineComponent, PropType } from "vue";
import { TaskStatus } from "@/types";

export default defineComponent({
	name: "TaskStatusIcon",
	props: {
		status: {
			type: String as PropType<TaskStatus>,
			default: ""
		},
		small: {
			type: Boolean,
			default: false
		},
		showLabel: {
			type: Boolean,
			default: false
		}
	}
});
</script>

<template>
	<span class="wrap">
		<span
			class="icon"
			:class="{
				[status.toLowerCase().replace(/ /g, '')]: true,
				small
			}"
		>
		</span>
		<span
			v-if="showLabel"
			class="label"
		>
			{{ status }}
		</span>
	</span>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.wrap {
	white-space: nowrap;
	display: inline-flex;
	align-items: baseline;
}

.icon {
	margin-right: 5px;

	&.completed {
		@include icon("~@/assets/images/status-completed.svg", 20px);
	}

	&.accepted {
		@include icon("~@/assets/images/status-accepted.svg", 20px);
	}

	&.cancelled {
		@include icon("~@/assets/images/status-cancelled.svg", 20px);
	}

	&.failed {
		@include icon("~@/assets/images/status-failed.svg", 20px);
	}

	&.inprogress {
		@include icon("~@/assets/images/status-in-progress.svg", 20px);
	}

	&.onhold {
		@include icon("~@/assets/images/status-on-hold.svg", 20px);
	}

	&.received {
		@include icon("~@/assets/images/status-received.svg", 20px);
	}

	&.rejected {
		@include icon("~@/assets/images/status-rejected.svg", 20px);
	}

	&.requested {
		@include icon("~@/assets/images/status-requested.svg", 20px);
	}

	&.ready {
		@include icon("~@/assets/images/status-ready.svg", 20px);
	}

	&.small {
		width: 14px;
		height: 14px;
	}
}
</style>
