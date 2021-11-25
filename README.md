# Spring Batch Example
Jobs starting via rest service

### Info
* [Spring Batch](https://docs.spring.io/spring-boot/docs/2.5.1/reference/htmlsingle/#howto-batch-applications)

### Guides
The following guides illustrate how to use some features concretely:

* [Creating a Batch Service](https://spring.io/guides/gs/batch-processing/)

### Postgresql
Can be configured for docker with postgres

Run docker on port 5432:

`$ docker run --name postgres -e POSTGRES_PASSWORD=password -d postgres`

`$ docker-commpose up -d`

connect to console:

`docker exec -it spring-batch-example_postgres_1 psql -U postgres`

to list databases:

`/l`

create database:

`create database spring_batch`

connect to database

`/c`

---

Queries:

`select step_execution_id, step_name, job_execution_id, status, exit_code from batch_step_execution order by step_execution_id desc limit 5;`

`select job_execution_id, job_instance_id, status, exit_code from batch_job_execution order by job_execution_id desc limit 5;`

`select s.step_execution_id, s.step_name, s.job_execution_id, s.status, s.exit_code, j.job_instance_id, j.status, j.exit_code 
from batch_step_execution s
inner join batch_job_execution j on s.job_execution_id = j.job_execution_id
where j.job_execution_id = (select job_execution_id from batch_step_execution order by job_execution_id desc limit 1)
order by s.step_execution_id desc`

## H2 database
Can be configured for H2 database in file:

{project_dir}/db/spring_vatch.mv.db

run console:

http://localhost:8080/h2-console

## Test csv job

send request:

```
POST http://localhost:8080/runJob
Content-Type: application/json

{
    "name": "transactionJob",
    "properties": {
        "transactionFile": "transaction.csv",
        "summaryFile": "account-summary.csv"
    }
}
```

Search the output csv file in logged directory
