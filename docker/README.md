# Docker

For running application locally `docker-compose` file is present to run containerized integrations.
If starting application with Spring command (or through IDE) and Spring Docker Compose integration is enabled,
containers are started automatically.
Containers should be started with following commands (positioned in _docker_ directory):

- download image, create and start containers: `docker compose up`
- start with created containers: `docker compose start`
- stop containers: `docker compose stop`
- stop and remove containers and volumes (clears persisted data): `docker compose down -v`

Container _pet-hub_ contains Postgres, RabbitMQ and MongoDB with its respective configuration and health-checks.
Provided health-checks are in favour of manually running Docker Compose and to have preconditions.
If Spring Docker Compose integration is enabled in local development, it would also run its version of checks.

RabbitMQ Docker container is configured manually. Reason for manual configuration is defining singular `vhost`,
but having one consumer for each runnable microservice (including additional admin user).

Postgres Docker container has additional init script to create two databases in a single container,
for each runnable microservice.

MongoDB container has additional init script which adds created user to newly created database
(not added by default; added in 'admin' DB), and initializes newly created database
(database only created upon first insert).

## Spring Boot Docker Compose

Spring Boot 3 introduced integration with Docker Compose to automate firing up Docker containers,
control their lifecycle and in many ways simplifies development process.
It leans on, also newly implemented, _ConnectionDetails_ abstraction, imagined to provide type-safe approach of
connecting to external services (database, RabbitMQ, etc.) with credentials stored in
external repository (such as AWS Secrets).
More details can be found here: https://spring.io/blog/2023/06/19/spring-boot-31-connectiondetails-abstraction

But this raises issue with local development with non-standard requirements. By default, when using this integration,
Spring reads ENV variables set in `docker-compose.yml` and uses them as connection details, ignoring properties
defined in `application.properties`. In this case, it would connect to `postgres` database with defined user,
ignoring the fact that there are two databases (for two runnable microservice).

Until this issue is bypassed, integration is disabled.
