# Spring Boot Demo

This is a Spring Boot demo application, made as a pet project, displaying some of (IMO) best practises when developing
web application within Spring framework. It consists of REST APIs secured by JWT, batch scheduled jobs, sending emails,
cache and much more.

***

## Prerequisites

Installation:

- NodeJS
- Git
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

- When commiting use style described here: https://cbea.ms/git-commit/

- This application is developed trying to follow following document: https://12factor.net/

- Interesting articles about pull requests: https://medium.com/@sam-cooper/i-merge-my-own-pull-requests-3001fe247be2
and https://medium.com/better-programming/i-review-my-own-pull-requests-83f74937ccf8

- Guide how to name test classes and
methods: https://www.codurance.com/publications/2014/12/13/naming-test-classes-and-methods <br>
TDD methodology should be applied where possible, but not in strict mode.

- Why is having shared libraries (dependencies) hard to
maintain: https://phauer.com/2016/dont-share-libraries-among-microservices/

- When updating dependency versions (Spring-related), it's useful to always check compatibility matrix.
  Example for version 3.3.8: https://docs.spring.io/spring-boot/3.3/appendix/dependency-versions/coordinates.html

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


When releasing new version, _SNAPSHOT_ suffix is removed and _develop_ branch is merged into _release_ branch. When
deploying said release to production, nothing is changed and no artifacts are being built. It reuses built artifact for
UAT.
When new version is released (merged into _release_ branch), project version is incremented and _SNAPSHOT_ suffix
is added.

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

## Release flow

Described above is general release flow and how branches should be managed. Below release technique and commands will be
described.

Each release is versioned and tagged with Git tag.

Versioning is done with simple SemVer strategy: **MAJOR**.**MINOR**.**PATCH** (e.g. 2.1.0).
Major version represents major changes like upgrading to new framework version or overhaul of architecture, and is
increased only by code owners. Minor is incremented on each release. Patch is incremented on each hotfix.
_SNAPSHOT_ suffix is being used only during development of new release and is removed when deploying release.

Versioning is done automatically with CI/CD tool and `maven-version-plugin` and `maven-release-plugin`.

Example commands:

1. `mvn -B -Darguments=-DskipTests release:prepare release:clean` remove _SNAPSHOT_ suffix, tag with current versions,
   push it to repo, set new development version (increment patch and add suffix) and also push it to repo. `clean`
   cleans
   utility auto-generated files
2. next command would be `mvn release:perform` but that is still in **TODO** due to setting up artifact repository

- `mvn build-helper:parse-version versions:set -DgenerateBackupPoms=false -DnewVersion=${parsedVersion.majorVersion}.${parsedVersion.nextMinorVersion}.0-SNAPSHOT versions:commit`
  manually set next version for all modules, which in this case increments minor version

***

## API versioning

Even though it is useful in some cases, API versioning won't be implemented here due to following reasons:

- significant increase of code complexity (new endpoints, new DTOs, logic branching)
- introduces complexity on database level (new schemas => data migration, data consistency)
- increase scope of testing, especially compatibility testing

To sum up, versioning introduces complexity in maintaining APIs while introducing complexity for integrators in
adapting.

Versioning does have its pros, but there are ways to mitigate them without introducing mentioned issues:

- proper API documentation/communication with integrators
- staging environments where integrators can adapt their queries (simultaneous releases)
- blue/green or canary deployments (special production environment where integrator can adapt)

***

## Dependency management

All dependencies and their versions are managed in parent/root _pom.xml_ file, and used/resolved in child _pom.xml_
files.

New dependencies should be always declared in root file, alongside with their version. But before adding new, always
check compatibility with existing ones and if they are already present as transitive dependencies.

Useful commands to:

- check used/unused and declared/undeclared dependencies: `mvn dependency:analyze`
- check mismatches between declared and resolved: `mvn dependency:analyze-dep-mgt`
- check duplicate declarations: `mvn dependency:analyze-duplicate`
- check dependency tree and resolved dependencies in project: `mvn dependency:tree`

***

## Codebase intro

For running application locally check README in _/docker_ directory.

Code style is mainly influenced by default IntelliJ IDEA style, but overwritten with `.editorconfig` file, and is
automatically applied. Additionally, Checkstyle has been introduced into build phase to validate codebase by defined
configuration.
If there are some violations, build will fail.

Maven wrapper is available, so use it instead your local installation (if using IntelliJ IDEA, change usage to wrapper).

This application is imagined as a web service (API), so it hasn't implemented translations, proper error mapping to keys
etc.

### Microservices

Project contains two runnable microservices: `api` and `batch`. Purpose of it is to split functionalities and load to
separate
executable _jars_.
For instance, first one serves API endpoints and can be easily scaled based on incoming load, while second one is
responsible for handling scheduled jobs, which may be "heavy" but won't impact API performance.

In real microservice architecture, these should have two separate databases (Database per Service pattern), but since
this is example application due to simplicity they will share it (one example is sync between entities on DB).
In production environment this will also work with right configuration.
What we need to keep on mind is database migrations, because they will be executed on shared database by first
microservice which will start.

Another advantage of splitting is splitting dependencies. For example, `api` microservice doesn't need to have
dependency for _spring-batch_.

Regarding dependencies, common approach is to have `common`, `shared` or some similarly named _jar_ which is used in
both microservices (but is not application by itself), and provides shared dependencies, domain classes, utilities etc.
Although it simplifies development and remove need for most of the duplication, it just hides another dependency which
has
to be maintained and is weak point for both microservices. Some examples are:

- changes made in common module require redeploy of both microservices (tightly coupled)
- in time combined with poor decisions, becomes cluttered with a bunch of logic
- often hides need for extracting logic to another application (microservice)
- if used by someone outside of this project (shared utilities on org level), provides transitive dependencies (which
  are often not handled properly) and discourages people on making changes due to many unknowns

### Communication between microservices

Applications support both synchronous and asynchronous communication between themselves. Sync is done via HTTP (REST
APIs) where we are expecting response immediately and consciously blocking further process.
Async is done via RabbitMQ pub/sub mechanism in cases where response and execution time is not necessary, for example
triggering to send an email or process something.

### RabbitMQ

RabbitMQ, as a async messaging tool, has been implemented with single virtual host and, by default, two consumers: `api`
and `batch` application.
Infrastructure supports multiple instance of each application, meaning having multiple consumers of f.e. `api`
application.

In general, it's imagined that `api` is always producer (sender), because all changes come through API and `batch`
application is always
listener. There are exceptions in rule, where `batch` has to send retry message back to queue.

If there is need for it, producers and listeners can be disabled with application property.

Retry policies for producer and listener has been implemented separately, one using `spring-retry` and latter using
retry and dead-letter queue.
For more details, check RabbitMQ configuration classes.

Since for most of the tests RabbitMQ isn't necessary, property is present to disable spinning-up container.

Due to shared configuration between both application, RabbitMQ has been extracted to separate microservice (acting just
as a library) called `rabbitmq`.

### Flyway

Project is using default naming schema for Flyway migrations: `Vx.x__migration-description.sql`

First version represents major project version and should be in sync. So, it can be said that migrations are grouped by
major project version.
Second version is plain iterating number of migration.

### OpenAPI (Swagger)

Application has available OpenAPI UI, for example at: _localhost:8080/api/swagger-ui.html_. It can be only accessed by
existing user (authentication is required).

### Actuator

Actuator has four endpoint categories enabled (which are accessible only by admin role):

- health API app: _localhost:8080/api/actuator/health/pet-hub-api_ (available for unauthenticated requests also)
- health BATCH app: _localhost:8081/api/batch/actuator/health/pet-hub-batch_ (available for unauthenticated requests
  also)
- info: _localhost:8080/api/actuator/info_
- metrics: _localhost:8080/api/actuator/metrics_
- Prometheus-formatted metrics: _localhost:8080/api/actuator/prometheus_

Besides those four, Actuator also provides basic liveness and readiness probes within _/health_ endpoint (which are not
protected by authentication).

### Testcontainers

Project is utilizing Testcontainers via Spring Boot dependency. Images (containers) are **not** defined by
_docker-compose.yaml_ file.
To have benefit of reusing Testcontainers when executing tests locally, developer must add following line to
_.testcontainers.properties_ file: `testcontainers.reuse.enable=true`.
File is located at: C:\Users\<Username>.

Container reuse feature is only available within application itself (API and BATCH applications will have separate
containers.)

### ArchUnit

ArchUnit are tests which validate codebase architecture/structure by defined rules, presented as unit tests.
Since they are scanning specifically by package names and location, each module has to implement its own copy of rules.
Also, some modules can have only subset of rules (f.e. `rabbitmq` which doesn't need DDD architecture, or controller
rules).

Revise all rules containing `.allowEmptyShould(true)` if it is still necessary. Goal is to have zero allows of that
kind.

Caveat is if there is need for changing common rule, change has to be applied on all modules manually.

### Mockito

At the moment, while using `@MockBean` annotation to mock/stub beans or method invocations, strict stubbing is not
enabled by default.
That results in duplicate code (stubbing - verifying) at the moment. We always want to have verify of mocked bean,
because some `void` methods could be invoked and tests won't cover that automatically.

Reason why we are using whole infrastructure of `@MockBean` (which requires whole application context) is because of
underlying configuration necessary for starting application.
Isolated service which could be tested with unit tests should not start whole application context, rather just use
Mockito (which has strict stubbing enabled by default).

More info can be found here: https://github.com/spring-projects/spring-framework/issues/33318
