# springboot3-nuxt3-template

SpringBoot 框架模板工程，用于敏捷开发

Child repo of https://github.com/sudoskys/Playground

- Mybatis Plus https://baomidou.com/introduce/
- Nuxt3 https://nuxt.com/
- Shadcn-Vue https://www.shadcn-vue.com/
- Flyway https://github.com/flyway/flyway

## Setup

- frontend
- src

## Database

postgres 5432

```shell
docker run --name myPostgresDb -p 5432:5432 -e POSTGRES_USER=postgresUser -e POSTGRES_PASSWORD=postgresPW -e POSTGRES_DB=postgresDB -d postgres
```