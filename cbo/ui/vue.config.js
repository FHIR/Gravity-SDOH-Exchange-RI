const CompressionPlugin = require("compression-webpack-plugin");
const StylelintPlugin = require("stylelint-webpack-plugin");

module.exports = {
	lintOnSave: process.env.NODE_ENV === "development" ? "warning" : "default",
	devServer: {
		port: 8082
	},
	configureWebpack: {
		plugins: [
			new CompressionPlugin(),
			new StylelintPlugin({
				files: ["./src/**/*.{vue,scss}"]
			})
		]
	}
};
