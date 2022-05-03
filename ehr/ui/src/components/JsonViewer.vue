<script lang="ts">
import { defineComponent, ref, onMounted, watch } from "vue";
import CodeMirror, { Editor } from "codemirror";
import "codemirror/lib/codemirror.css";
import "codemirror/mode/javascript/javascript.js";
import "codemirror/addon/fold/foldcode.js";
import "codemirror/addon/fold/brace-fold.js";
import "codemirror/addon/fold/foldgutter.js";
import "codemirror/addon/fold/foldgutter.css";
import "codemirror/addon/edit/matchbrackets.js";


export default defineComponent({
	props: {
		data: {
			type: String,
			required: true
		}
	},
	setup(props) {
		const rootDiv = ref<HTMLDivElement>();

		onMounted(async () => {
			const cmInstance: Editor = CodeMirror(rootDiv.value!, {
				mode: "application/json",
				readOnly: true,
				lineNumbers: true,
				gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
				foldOptions: {
					widget: " ... "
				},
				foldGutter: true,
				matchBrackets: true
			});

			watch(() => props.data, newData => {
				cmInstance.setValue(newData);
			}, { immediate: true });
		});

		return {
			rootDiv
		};
	}
});
</script>

<template>
	<div
		ref="rootDiv"
		class="json-viewer"
	>
	</div>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";

$base-color: $global-text-color;
$line-padding: 5px;
$indent-guide-border: $global-border;
$indent-width: 20px;
$folder-icon-width: 8px;

$json-key-color: #00a398;
$json-string-color: #000f5b;
$json-number-color: #00b1f9;
$json-atom-color: #000f5b;

.json-viewer {
	height: 100%;
	width: 100%;

	::v-deep(.CodeMirror) {
		height: 100%;
		color: $base-color;
		font-size: $global-font-size;
		font-weight: 300;
		position: relative;

		.CodeMirror-gutters {
			background-color: $white;
			border-right: $indent-guide-border;
		}

		.CodeMirror-lines {
			padding-top: 0;
		}

		.CodeMirror-line {
			padding-top: $line-padding;
			padding-bottom: $line-padding;
			padding-left: 1px;
		}

		.CodeMirror-linenumber {
			padding-top: $line-padding;
			padding-bottom: $line-padding;
			color: $base-color;
		}

		.CodeMirror-foldgutter {
			width: $folder-icon-width * 3;

			&-open,
			&-folded {
				padding: $line-padding 0 $line-padding $folder-icon-width;

				&::after {
					content: "";
					display: inline-block;
					height: $folder-icon-width;
					width: $folder-icon-width;
					background-color: $base-color;
					mask-image: url("~@/assets/images/arrow-right.svg");
					mask-repeat: no-repeat;
					mask-size: contain;
					mask-position: center;
				}
			}

			&-open::after {
				transform: rotate(90deg);
			}
		}

		.CodeMirror-foldmarker {
			color: $base-color;
			text-shadow: none;
			font-family: inherit;
		}

		.cm-tab {
			width: $indent-width;
		}

		.cm-tab:not(:first-child) {
			position: relative;

			&::after {
				border-left: $indent-guide-border;
				position: absolute;
				top: -$line-padding;
				bottom: -$line-padding;
				left: 0;
				content: " ";
			}
		}

		.CodeMirror-matchingbracket {
			color: inherit;
			border: $global-border;
		}

		.cm-string {
			color: $json-string-color;
		}

		.cm-property {
			color: $json-key-color;
		}

		.cm-number {
			color: $json-number-color;
		}

		.cm-atom {
			color: $json-atom-color;
		}

		.cm-id-token {
			color: $json-key-color;
		}
	}
}
</style>
