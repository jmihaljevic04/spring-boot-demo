# Docker

For running application locally `docker-compose` file is present to run containerized integrations. If running with
Spring command (or through IDE), containers are started automatically.
If necessary, containers can be started with following commands (positioned in _docker_ directory):

- download and start containers: `docker compose up`
- start with downloaded containers: `docker compose start`
- stop containers: `docker compose stop`
- stop and remove containers and volumes: `docker compose down -v`

Container _pet-hub_ contains Postgres, RabbitMQ and MongoDB with its respective configuration and health-checks.
Provided health-checks are in favour of manually running Docker Compose and to have preconditions,
but Spring Boot in local development would also run their version of checks).

RabbitMQ Docker container is configured manually. Reason for manual configuration is defining singular `vhost`,
but having one consumer for each runnable microservice (including additional admin user).

Postgres Docker container has additional init script to create two databases in a single container,
for each runnable microservice.
