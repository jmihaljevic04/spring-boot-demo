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

## Codebase intro

For running application locally `docker-compose` file is present to run containerized integrations.

Code style is mainly influenced by default IntelliJ IDEA style, but overwritten with `.editorconfig` file, and is
automatically applied.

Maven wrapper is available, so use it instead your local installation (if using IntelliJ IDEA, change usage to wrapper).