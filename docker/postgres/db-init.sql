CREATE DATABASE pet_hub_api;
CREATE DATABASE pet_hub_batch;

CREATE USER pet_hub_api_admin WITH ENCRYPTED PASSWORD 'pet-hub-api-password';
CREATE USER pet_hub_batch_admin WITH ENCRYPTED PASSWORD 'pet-hub-batch-password';

GRANT ALL PRIVILEGES ON DATABASE pet_hub_api TO pet_hub_api_admin;
GRANT ALL PRIVILEGES ON DATABASE pet_hub_batch TO pet_hub_batch_admin;
