# Spring Batch Example with Spring Boot

* [Spring Batch](https://docs.spring.io/spring-boot/docs/2.5.1/reference/htmlsingle/#howto-batch-applications)

### Guides
The following guides illustrate how to use some features concretely:

* [Creating a Batch Service](https://spring.io/guides/gs/batch-processing/)

### Postgresql

Run docker on port 5432:

`$ docker run --name postgres -e POSTGRES_PASSWORD=p12345 -d postgres`

`$ docker-commpose up -d`

connect to console:

`docker exec -it spring-batch-example_postgres_1 psql -U postgres`

to list databases:

`/l`

create database:

`create database spring_batch`

connect to database

`/c`

