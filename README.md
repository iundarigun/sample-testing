# Sample testing

This repo is to show some features to test

### Postgres database
To up a postgres database:
```
$ docker run --name local-postgres -p 5432:5432 -v /opt/postgres/data:/var/lib/postgresql/data  -e POSTGRES_PASSWORD=postgres -d postgres
```
To connect:
```
$ docker exec -it local-postgres psql -U postgres
# create database devcave_sample_testing;
```