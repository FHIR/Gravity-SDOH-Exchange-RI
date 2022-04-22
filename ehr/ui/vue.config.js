const CompressionPlugin = require("compression-webpack-plugin");
const StylelintPlugin = require("stylelint-webpack-plugin");

module.exports = {
	lintOnSave: process.env.NODE_ENV === "development" ? "warning" : "default",
	devServer: {
		port: 8081,
		proxy: {
			"/*": {
				target: "http://localhost:8080"
				// changeOrigin: true,
				// pathRewrite: {
				// 	"^/api": ""
				// }
			}
		}
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
