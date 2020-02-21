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

### Frontend

The frontend, originally integrated as a resource in this project, was migrated to [NiccoMlt/protelis-web-frontend](https://github.com/NiccoMlt/protelis-web-frontend).

## Deploy

The project is deployed using the awesome [`dpl` tool](https://github.com/travis-ci/dpl) by Travis CI creators.

In particular, CI pipeline does the following deployments:

- Master branch code is deployed on Heroku at <https://protelis-web.herokuapp.com>
- Develop branch code is deployed on Heroku at <https://protelis-web-develop.herokuapp.com>

### Docker

The software can be easily deployed as a Docker container.

To build your own (in this example we will call it simply `protelis-web`) you have to build it:

```bash
# Build the shadow JAR file, which should be located in ./build/libs/protelis-on-web-all.jar
./gradlew clean shadowJar

# Build the Docker image
docker build -t protelis-web .
```

Then, run it with:

```bash
# Run the container:
# - as a deamon,
# - binding internal 8080 to actual 80 port and 8443 to 443
# - naming the container protelis-web to easily recognize it
docker run -d -p80:8080 -p 443:8443 --name protelis-web protelis-web
```

## License and credits [![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

As stated in [`LICENSE` file](./LICENSE), this code is provided under GPLv3 license.

The code was bootstrapped from [my personal adaptation](https://github.com/NiccoMlt/single-page-react-vertx-howt) of official [Single Page Application development with React and Vert.x](https://how-to.vertx.io/single-page-react-vertx-howto/) tutorial, which are both provided under [Apache License 2.0](https://opensource.org/licenses/Apache-2.0).
