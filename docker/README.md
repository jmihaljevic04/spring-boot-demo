# Docker

Docker directory provides docker-compose file which is used by Spring Boot when starting via IDE, or it can be used for
manual starting.

Container _pet-hub_ contains Postgres, RabbitMQ and MongoDB with its respective configuration and health-checks.

All images are configured by provided environment variables, **except** for RabbitMQ which is configured manually.
Reason for manual configuration is defining singular `vhost`, but having one consumer for each runnable microservice (
including additional admin user).
