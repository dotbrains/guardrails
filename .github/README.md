# GuardRails

A modular, maintainable, customizable security-compliant DevOps strategy for use with Travis CI.

This is a boilerplate for projects that wish to become more compliant with IT security standards and utilize a modular and maintainable DevOps strategy. It contains a set of sub-projects that can be used to create a new project from scratch that extends this paradigm.

Currently, this boilerplate is only compatible with a custom image pipeline infrastructure due to its dependence on a specific container registry. However, it can be easily adapted to work with other CI/CD solutions by replacing the current registry with another cloud provider.

Additionally, this project should be used by teams that cannot make use of a centralized CI/CD platform because their project cannot support [trunk-based development](https://trunkbaseddevelopment.com/).

## Gitflow

![image](https://www.goodtechthings.com/content/images/size/w1920/2022/12/GitFlow.png)
_Credit: [Good Tech Things](https://www.goodtechthings.com/git-flow/)_

This boilerplate is based on the [Gitflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) workflow. It contains the following branches:

- `main` or `master`: The main or master branch. Contains the latest stable version of the project.
- `development`: The development branch. Contains the latest development version of the project.
- `#.#.#`: A release branch. Contains the latest version of the project for a specific release.

Please see the [Gitflow](/resources/gitflow_proposal_221018.pdf) presentation for more information.

## Getting Started

Start by cloning the project.

```bash
git clone --recurse-submodules https://github.com/dotbrains/guardrails
```

Then, execute the [`change_organization_name.py`](scripts/change_organization_name.py) script to change the organization name to your organization name.

```bash
python3 ./scripts/change_organization_name.py <organization_name>
```

This will change the organization name in any file that contains:

```text
{ORGANIZATION}
```

## Features

- Custom Image Pipeline
- [Travis CI](https://travis-ci.com/) CI/CD
- [Docker](https://www.docker.com/) containerization
- [AppScan](https://cloud.appscan.com/) SAST
- [Contrast](https://www.contrastsecurity.com/) IAST (optional)
- [Detect Secrets](https://github.com/Yelp/detect-secrets) for secret detection

### Java

- [Maven](https://maven.apache.org/) build tool
- [Spring Boot](https://spring.io/projects/spring-boot) REST-based microservice
- [git-code-format-maven-plugin](https://github.com/Cosium/git-code-format-maven-plugin) for code formatting

### JavaScript

- [Node.js](https://nodejs.org/) integration
- [pnpm](https://pnpm.js.org/) integration
- [Husky](https://github.com/typicode/husky) for git hooks
- [Prettier](https://prettier.io/) for code formatting

### Logging & Monitoring

- [Mezmo](https://www.mezmo.com/) for logging (formerly LogDNA)
- [Dynatrace](https://www.dynatrace.com/) for monitoring

To integrate **Mezmo** within your project, you must first create a Mezmo instance through your cloud provider and obtain its credentials. Then, in your CI/CD platform, add your Mezmo instance credentials under the appropriate integration settings.

To integrate **Dynatrace**, toggle the monitoring option in your CI/CD platform's integration settings.

### Mend (formerly WhiteSource) for Open Source Vulnerability Scanning

Mend is utilized at the repository level to scan for vulnerabilities in the project's open source dependencies through the use of its _renovate_ and _remediate_ features. To integrate **Mend** within your repository, please follow their onboarding instructions [here](https://www.mend.io/).

## Sub-projects

The following sub-projects are included in this boilerplate:

- [base-images-blueprint](https://github.com/dotbrains/base-images-blueprint): A set of base images used by the CI/CD pipeline
- [boilerplate-technical-documentation](https://github.com/dotbrains/boilerplate-technical-documentation): A base project for technical documentation.
- [appscan-sast-travis-integration](https://github.com/dotbrains/appscan-sast-travis-integration): A base project for AppScan SAST integration with Travis CI.
- [contrast-security-sast-travis-integration](https://github.com/dotbrains/contrast-security-sast-travis-integration): A base project for Contrast Security SAST integration with Travis CI.
- [create-tagged-releases](https://github.com/dotbrains/create-tagged-releases): A Python script to create tagged releases for GitHub repositories.
- [Travis CI Shared Configurations](https://github.com/dotbrains/travis-shared-config): A set of shared configurations for Travis CI used to ensure consistency across multiple projects.
- [java](java): A base project for Java projects
- [kotlin](kotlin): A base project for Kotlin projects
- [js](js): A base project for JavaScript projects

## Travis CI

The following environment variables are required to be set in Travis CI:

_Container Registry_:

- `REGISTRY_USER`: The username of the container registry
- `REGISTRY_PASSWORD`: The password of the container registry
- `REGISTRY_NAMESPACE`: The namespace of the container registry (e.g., your organization name)

_AppScan_:

- `APPSCAN_API_KEY`: The API key of your AppScan integration
- `APPSCAN_API_SECRET`: The API secret of your AppScan integration
- `APPSCAN_APP_ID`: The application ID of your AppScan integration

_Contrast_:

- `CONTRAST__API__API_KEY`: The API key of your Contrast integration
- `CONTRAST__API__USER_NAME`: The username of your Contrast integration
- `CONTRAST__API__ORGANIZATION`: The organization of your Contrast integration
- `CONTRAST__API__SERVICE_KEY`: The service key of your Contrast integration

### Stages

Build stages are executed depending on what the Travis event type is and which branch is being built. The following table shows the stages that are executed for each event type and branch.

| Stage                                         | Event Types            | Branches                                   |
| --------------------------------------------- | ---------------------- | ------------------------------------------ |
| [Prepare Cache](#prepare-cache)               | `pull_request`         | `development`, `master`, `tagged releases` |
| [Detect Secrets](#detect-secrets)             | `pull_request`         | `development`, `master`, `tagged releases` |
| [Static Code Analysis](#static-code-analysis) | `pull_request`, `push` | `development`, `master`                    |
| [Build](#build)                               | `pull_request`         | `development`, `master`, `tagged releases` |
| [Deploy Artifacts](#deploy-artifacts)         | `push`                 | `development`, `tagged releases`           |

#### Prepare Cache

This stage warms up the cache by installing the dependencies of the project.

#### Detect Secrets

This stage detects secrets in the project using the [Detect Secrets](https://github.com/Yelp/detect-secrets) tool. The stage fails if any unaudited secrets are found.

Before committing code, you should run the `detect-secrets` tool locally to ensure that you are not committing any secrets. If you have the tool installed, it can be run with the following commands:

```bash
detect-secrets scan --update .secrets.baseline
detect-secrets audit .secrets.baseline
```

#### Static Code Analysis

This stage runs the [AppScan](https://cloud.appscan.com) static code analysis CLI tool on the project. The build fails if any _new_ High or Critical issues are found.

If your build fails due to new High or Critical issues, check the report in the AppScan dashboard to see what the issues are. If you believe that the issues are false positives, you can mark them as noise in the scan report with a reason why. You must promote your scan if you marked any issues as false positive. If you do not promote your scan, the issues will be reported again in the next pipeline execution resulting in another failure.

#### Build

This stage builds the project and ensures that the application has no build-time errors. The stage fails if the project fails to build.

#### Deploy Artifacts

This stage builds the container images and deploys them to the specified container image repository.

If the branch is `development`, the images are tagged with the branch name. If the build is triggered by a tagged release, the images are tagged with the release tag.

## Spring Boot Custom Banner

The Spring Boot banner is generated using the [Spring Boot Banner Generator](https://devops.datenkollektiv.de/banner.txt/index.html).

The banners can be found in the `src/main/resources` directory and are named `banner.txt`.

## License

© DotBrains

This software is free and may be redistributed under the terms specified in the [LICENSE] file.

[license]: LICENSE
