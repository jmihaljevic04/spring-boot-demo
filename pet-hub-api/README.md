# PetHub API application

Web application providing endpoints to consumers.

### Features

- authentication & authorization + registering new application user
- TVMaze integration: https://www.tvmaze.com/api
    - fetching TV show by name (cached by input name)
    - fetching TV show by ID (retry mechanism - backoff)
    - fetching all TV shows with pagination
- JsonPlaceholder API integration: https://jsonplaceholder.typicode.com/
    - using HttpExchange interface approach
    - using Wiremock as integration test tool
