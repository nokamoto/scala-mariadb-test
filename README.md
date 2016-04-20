# scala-mariadb-test

[![Build Status](https://travis-ci.org/nokamoto/scala-mariadb-test.svg?branch=master)](https://travis-ci.org/nokamoto/scala-mariadb-test)

## Flyway and Scalikejdbc Reverse Engineering
```
sbt flywayClean flywayMigrate
sbt "scalikejdbcGen person"
```

## Run
```
sbt -Dhosts=${hosts} -Dusername=${username} -Dpassword=${password} -Dschema=${schema} run
```

```
sbt assembly
java -Dhosts=${hosts} -Dusername=${username} -Dpassword=${password} -Dschema=${schema} -jar target/scala-2.11/scala-mariadb-test.jar
```
