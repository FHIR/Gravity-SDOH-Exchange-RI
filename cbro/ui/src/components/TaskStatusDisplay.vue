<script lang="ts">
import { defineComponent, PropType, computed } from "vue";
import { TaskStatus } from "@/types";


const StatusDisplayText: { [status in TaskStatus]: string } = {
	"ACCEPTED":       "Accepted",
	"CANCELLED":      "Cancelled",
	"COMPLETED":      "Completed",
	"DRAFT":          "Draft",
	"ENTEREDINERROR": "Entered in Error",
	"FAILED":         "Failed",
	"INPROGRESS":     "In Progress",
	"NULL":           "Null",
	"ONHOLD":         "On Hold",
	"READY":          "Ready",
	"RECEIVED":       "Received",
	"REJECTED":       "Rejected",
	"REQUESTED":      "Requested"
};


export default defineComponent({
	props: {
		status: {
			type: String as PropType<TaskStatus>,
			required: true
		}
	},
	setup(props) {
		const displayText = computed<string>(() => StatusDisplayText[props.status]);

		return {
			displayText
		};
	}
});
</script>

<template>
	<div class="task-status">
		<div
			:class="status.toLowerCase()"
			class="icon"
		>
		</div>
		<div class="label">
			{{ displayText }}
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

		&.accepted { background-image: url("~@/assets/images/status-accepted.svg"); }

		&.cancelled { background-image: url("~@/assets/images/status-cancelled.svg"); }

		&.completed { background-image: url("~@/assets/images/status-completed.svg"); }

		&.draft { }

		&.enteredinerror { }

		&.failed { background-image: url("~@/assets/images/status-failed.svg"); }

		&.inprogress { background-image: url("~@/assets/images/status-inprogress.svg"); }

		&.null { }

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
