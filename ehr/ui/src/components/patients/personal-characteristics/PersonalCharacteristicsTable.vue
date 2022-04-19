<script>
import { defineComponent } from "vue";

export default defineComponent( {
	name: "PersonalCharacteristicsTable",
	data(){
		return{
			tableData:[{
				type: "Personal Pronouns",
				performer: "John Doe",
				method: "Self Reported",
				value: "he/him/his/his/himself",
				detailedValue: ""
			},
			{
				type: "Observation Ethnicity",
				performer: "John Doe",
				method: "Self Reported",
				value: "Hispanic or Latino",
				detailedValue: "American Indian or Alaska Native"
			}
			]
		};
	}
});

</script>
<template>
	<div class="table-wrapper">
		<el-table
			ref="tableEl"
			:data="tableData"
		>
			<el-table-column
				property="type"
				label="Type"
			/>

			<el-table-column
				label="Performer"
				property="performer"
			/>
			<el-table-column
				label="Method"
				property="method"
			/>

			<el-table-column
				label="Value"
				property="value"
			/>
			<el-table-column
				label="Description"
				property="description"
			/>

			<el-table-column
				label="Detailed Value"
				property="detailedValue"
			/>
		</el-table>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.title {
	margin-top: 10px;
	display: flex;
	justify-content: space-between;

	h3 {
		font-weight: $global-font-weight-medium;
		margin: 0 0 0 20px;
	}
}

.table-wrapper {
	background-color: $global-background;
	border-radius: 5px;
	border: 1px solid $global-base-border-color;
	padding: 10px 20px;
	min-height: 130px;

	+ .table-wrapper {
		margin-top: 30px;
	}
}

.icon-link {
	position: relative;
	left: 7px;
	cursor: pointer;

	@include icon("~@/assets/images/link.svg", 14px, 14px);
}

.cell-wrapper {
	display: flex;
	min-width: 60%;

	.icon-link {
		min-height: 14px;
		min-width: 14px;
	}
}

.cell-text {
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
}
</style>
