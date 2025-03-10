CREATE DATABASE pet_hub_api;
CREATE DATABASE pet_hub_batch;

CREATE USER pet_hub_api_admin WITH ENCRYPTED PASSWORD 'pet-hub-api-password';
CREATE USER pet_hub_batch_admin WITH ENCRYPTED PASSWORD 'pet-hub-batch-password';

ALTER DATABASE pet_hub_api OWNER TO pet_hub_api_admin;
ALTER DATABASE pet_hub_batch OWNER TO pet_hub_batch_admin;
