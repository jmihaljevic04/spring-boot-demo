# Spring Boot Demo

This is a Spring Boot demo application, made as a pet project, displaying some of (IMO) best practises when developing
web application within Spring framework. It consists of REST APIs secured by JWT, batch scheduled jobs, sending emails,
cache and much more.

***

## Prerequisites

Installation:

- NodeJS
- GIT
- Docker
- Java 21
- IDE (instructions are based on IntelliJ IDEA)

Maven is not necessary locally since it is provided as wrapper within project.

To pull/push to GitHub codebase, generate SSH keys with following
guide: https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent

In your IDE enable _Annotation processing_ in favor of Lombok. If not present, in IDE as plugins install Lombok and
SonarLint. <br>
Also, on commit set next options:

- Optimize imports
- Analyze code
- Check TODO
- Reformat code

***

## Helpful guides

When commiting use style described here: https://cbea.ms/git-commit/

This application is developed trying to follow following document: https://12factor.net/

Interesting articles about pull requests: https://medium.com/@sam-cooper/i-merge-my-own-pull-requests-3001fe247be2
and https://medium.com/better-programming/i-review-my-own-pull-requests-83f74937ccf8

Guide how to name test classes and
methods: https://www.codurance.com/publications/2014/12/13/naming-test-classes-and-methods <br>
TDD methodology should be applied where possible, but not in strict mode.

***

## Git flow

Commit regularly. Don't complete whole feature in one commit, but also don't commit every line changed. Find balance.
Make commits tell a story. Separate them in completed chunks of whole feature.

Development uses simple Git flow with three protected branches:

- _develop_ (used for developing SNAPSHOT versions),
- _release_ (used for releasing finalized version, non-SNAPSHOT, to UAT/STAGE) and
- _main_ (used for deploying code, and already built artifact, to production from _release_ branch)

Reason for having this kind of flow (instead of maybe GitHub flow), is to have proper release versioning which is often
required.
With this approach, it is easier to do hotfixes (create hotfix branch from appropriate environment, often production and
_main_ branch), keep track of deployed releases and working on next release simultaneously, troubleshoot issues on
specific environment (check-out branch/release and run it locally).

Each release is versioned and tagged with Git tag.

Versioning is done with simple SemVer strategy: **MAJOR**.**MINOR**.**PATCH** (e.g. 2.1.0).
Major version represents major changes like upgrading to new framework version or overhaul of architecture, and is
increased only by code owners. Minor is incremented on each release. Patch is incremented on each hotfix.
_SNAPSHOT_ suffix is being used only during development of new release and is removed when deploying release.

Versioning is done automatically with CI/CD tool and `maven-version-plugin`.
When releasing new version, _SNAPSHOT_ suffix is removed and _develop_ branch is merged into _release_ branch. When
deploying said release to production, nothing is changed and no artifacts are being built. It reuses built artifact for
UAT.
When new version is released (merged into _release_ branch), project version is being incremented and _SNAPSHOT_ suffix
is being added.

### How to create branches and merge them?

When working on new feature, check out latest _develop_ branch and create new _feature_ branch. Example:
_feature/{ISSUE_ID}-{short description}_.

When working on hotfix for production, check out latest _main_ branch and create new _hotfix_ branch. Example:
_hotfix/{ISSUE_ID}-{short description}_.

When working on bugfix for current release or release on UAT, check out latest relevant branch and create new _bugfix_
branch. Example: _bugfix/{ISSUE_ID}-{short description}_

When feature/hotfix/bugfix is completed, create a pull request. One of the code owner has to be of the reviewers.
Creator of PR (assignee) is responsible for that PR, which means: test coverage of newly added/modified code, resolve
merge conflicts, resolve all comments, regularly update PR with target branch (preferably use rebase for cleaner
history), make sure PR build is successful and is the one who merges PR after all checks are okay.

***

## Codebase intro

For running application locally `docker-compose` file is present to run containerized integrations.

Code style is mainly influenced by default IntelliJ IDEA style, but overwritten with `.editorconfig` file, and is
automatically applied.

Maven wrapper is available, so use it instead your local installation (if using IntelliJ IDEA, change usage to wrapper).
