# Sample testing

This repo is to show some features to test

### Postgres database
To up a postgres database:
```
$ docker run --name local-postgres -p 5432:5432 -v /opt/postgres/data:/var/lib/postgresql/data -v <path workspace>/sample-testing/database/schema.sql:/docker-entrypoint-initdb.d/10-schema.sql -v <path workspace>/database/data.sql:/docker-entrypoint-initdb.d/20-data.sql -e POSTGRES_USER=devcave_sample_testing -e POSTGRES_PASSWORD=devcave_sample_testing -d postgres
```
