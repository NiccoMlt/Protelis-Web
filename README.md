# Protelis-Web

[![Build Status](https://travis-ci.com/NiccoMlt/Protelis-Web.svg?token=gFNEyVkpY7xNqwmKzp7q&branch=master)](https://travis-ci.com/NiccoMlt/Protelis-Web)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/02e402a5fe824dc7a05d447ed33b6c11)](https://www.codacy.com/manual/NiccoMlt/Protelis-Web?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=NiccoMlt/Protelis-Web&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/NiccoMlt/Protelis-Web/branch/master/graph/badge.svg)](https://codecov.io/gh/NiccoMlt/Protelis-Web)

Web server with frontend for Protelis.

## Repository details [![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)

The commits in this Git repository follows [Conventional Commits standard](https://www.conventionalcommits.org/en/)
with the help of [Conventional Changelog tools](https://github.com/conventional-changelog/conventional-changelog)
and [Commitizen](http://commitizen.github.io/cz-cli/); both root and frontend contains Commitizen configs to make them Commitizen-friendly.

### Backend

The repository is a standard Gradle project importable in Intellij IDEA.

The backend is built with Vert.x framework and Kotlin language.

REST APIs are documented with OpenAPI standard v3.1.

### Frontend

Even if the [frontend sourceSet](./src/main/frontend) of root (backend) project is automatically built and linked by Gradle,
it is a full-fledged Typescript project built with Yarn and `create-react-app` scripts and can be opened separately as a VS Code or WebStorm project.

## Deploy

The project is deployed using the awesome [`dpl` tool](https://github.com/travis-ci/dpl) by Travis CI creators.

In particular, CI pipeline does the following deployments:

- Master branch code is deployed on Heroku at <https://protelis-web.herokuapp.com>
- Develop branch code is deployed on Heroku at <https://protelis-web-develop.herokuapp.com>
- Master branch Redoc OpenAPI 3 documentation is deployed on Surge.sh at <https://protelis-web-api.surge.sh>
- Develop branch Redoc OpenAPI 3 documentation is deployed on Surge.sh at <https://protelis-web-api-develop.surge.sh>

## License and credits [![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

As stated in [`LICENSE` file](./LICENSE), this code is provided under GPLv3 license.

The code was bootstrapped from [my personal adaptation](https://github.com/NiccoMlt/single-page-react-vertx-howt) of official [Single Page Application development with React and Vert.x](https://how-to.vertx.io/single-page-react-vertx-howto/) tutorial, which are both provided under [Apache License 2.0](https://opensource.org/licenses/Apache-2.0).
