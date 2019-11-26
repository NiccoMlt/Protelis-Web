# Protelis on the Web - React frontend
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)

## Use cases

[![UML diagram](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/NiccoMlt/Protelis-Web/feature/redux/docs/src/use-cases.puml)](../../../docs/src/use-cases.puml)

## Code quality & CI

### Type checking [![Typescript Version](https://img.shields.io/badge/typescript-3.6.4-blue.svg?logo=typescript)](https://www.typescriptlang.org/)

The frontend project is completely developed in [TypeScript](https://www.typescriptlang.org/), so transpiled Javascript code should be statically type-checked on compile-time.

TypeScript was chosen instead of [Flow](https://flow.org/) for better third-party support and personal preference.

### Code style [![Code Style](https://badgen.net/badge/code%20style/airbnb/ff5a5f?icon=airbnb)](https://github.com/airbnb/javascript)

Basic code style configuration (max line length, indent style and size, ...) on frontend project are handled by EditorConfig directly on the editor.

Advanced code style checks are done with [ESLint](https://eslint.org/):
the tool checks code compliance to an [Airbnb React/Javascript code style](https://github.com/airbnb/javascript) [adaptation for TypeScript](https://github.com/iamturns/eslint-config-airbnb-typescript).

ESLint was chosen instead of TSLint beacause of the [deprecation announcement](https://medium.com/palantir/tslint-in-2019-1a144c2317a9) for the latter (see issue [#4534](https://github.com/palantir/tslint/issues/4534)).

### Continuous integration

The frontend project is built and checked with the (root) backend project by Travis CI on latest NodeJS LTS version.

## Patterns and Frameworks

The frontend application is written with React and wants to fit the best practices of the library.

[![Facebook Flux data flow](https://raw.githubusercontent.com/facebook/flux/master/img/flux-diagram-white-background.png)](https://github.com/facebook/flux)

The application adopts a variation of the Flux pattern to manage the state and makes use of Redux library to do that.

The main differences between Facebook Flux and Redux are:

- **one** single `store` split in `slice`s (domains);
- no singleton `dispatcher` entity; the `store` has the dispatching process baked in;
- the **`reducer`s** wrap the logic of what to do on the data based on the received actions (business logic); it's a simple function that receives the previous state and one action, and it returns the new state based on that action.
- The state’s immutability, in Redux, is achieved easily by making the `reducer`s pure functions (with no side effects).
