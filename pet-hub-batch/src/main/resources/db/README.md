# Database objects

Root of this directory can contain stored procedures, functions and views. Motive behind is to have versioned database
objects in a single file, which can be used for editing (improving) mentioned objects. With this versioning in place,
there is no need for searching latest version of database object in migrations.

It is mandatory to **update** objects here whenever there is a need to update them via database migration.

Suggestion is to make local changes here and then create database migration, and commit them together.
