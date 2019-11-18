# Protelis on the Web - React frontend
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)

## Code quality & CI

### Type checking [![Typescript Version](https://img.shields.io/badge/typescript-3.6.4-blue.svg?logo=typescript)](https://www.typescriptlang.org/)

The frontend project is completely developed in [TypeScript](https://www.typescriptlang.org/), so transpiled Javascript code should be statically type-checked on compile-time.

TypeScript was chosen instead of [Flow](https://flow.org/) for better third-party support and personal preference.

### Code style [![Code Style](https://badgen.net/badge/code%20style/airbnb/ff5a5f?icon=airbnb)](https://github.com/airbnb/javascript)

Basic code style configuration (max line length, indent style and size, ...) on frontend project are handled by EditorConfig directly on the editor.

Advanced code style checks are done with ESLint:
the tool checks code compliance to an [Airbnb React/Javascript code style](https://github.com/airbnb/javascript) [adaptation for TypeScript](https://github.com/iamturns/eslint-config-airbnb-typescript).

### Continuous integration

The frontend project is built and checked with the (root) backend project by Travis CI on latest NodeJS LTS version.

<!-- TODO --> <!-- ## Patterns and Frameworks -->
