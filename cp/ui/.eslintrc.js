module.exports = {
	"root": true,
	"env": {
		"node": true
	},
	"extends": [
		"eslint:recommended",
		"plugin:vue/vue3-recommended"
	],
	"rules": {
		"vue/comment-directive": 0,
		"semi": ["error", "always"],
		"curly": "error",
		"quotes": ["warn", "double"],
		"dot-notation": "error",
		"no-cond-assign": "warn",
		"no-nested-ternary": "warn",
		"arrow-body-style": ["error", "as-needed"],
		"arrow-parens": ["error", "as-needed"],
		"prefer-arrow-callback": "warn",
		"no-debugger": "warn",
		"no-extra-bind": "error",
		"no-fallthrough": "off",
		"no-use-before-define": "off",
		"prefer-const": ["warn", {
			"ignoreReadBeforeAssign": true
		}],
		"no-var": "error",
		"no-shadow-restricted-names": "error",
		"no-undef": "error",
		"no-unused-vars": "warn",
		"no-console": "off",
		"no-irregular-whitespace": "warn",
		"comma-dangle": ["warn", "never"],
		"func-call-spacing": ["error", "never"],
		"no-trailing-spaces": "error",
		"no-unneeded-ternary": "warn",
		"object-property-newline": ["warn", {
			"allowMultiplePropertiesPerLine": true
		}],
		"one-var-declaration-per-line": ["error", "initializations"],
		"constructor-super": "warn",
		"no-dupe-class-members": "error",
		"no-duplicate-imports": "error",
		"no-useless-constructor": "warn",
		"object-shorthand": ["warn", "properties"],
		"prefer-destructuring": "warn",
		"prefer-template": "error",
		"object-curly-spacing": ["error", "always"],
		"camelcase": ["error", {
			"allow": ["^\\$_"]
		}],
		"indent": ["warn", "tab", {
			"SwitchCase": 1
		}],
		"vue/html-indent": ["warn", "tab", {
			"attribute": 1,
			"baseIndent": 1,
			"closeBracket": 0,
			"alignAttributesVertically": true,
			"ignores": []
		}],
		"vue/html-self-closing": ["error", {
			"html": {
				"normal": "never"
			}
		}],
		"vue/no-v-model-argument": 0,
		"no-empty": 0
	},
	"parserOptions": {
		"parser": "@typescript-eslint/parser"
	}
};
