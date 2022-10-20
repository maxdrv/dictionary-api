Before starting the application locally, you need to start the database
`cd dependency-stub && docker-compose up -d`

build fat jar with command `bash mvnw package` on mac, linux or `.\mvnw.cmd package` on windows

start fat jar with command `java -jar ./target/dictionary-api.jar`

# Problems
## mapstruct
`No property named "id" exists in source parameter` or similar error
but fields actually exist (via lombok annotations)
because annotation processing out of order
- in case of maven build - just fix order of annotation processors in pom so lombok is before mapstruct
- in case of Idea run
  Preferences -> Build, Execution, Deployment -> Compiler -> Annotation Processors
  fix paths to annotation processors the way lombok is before mapstruct 
  example: `/Users/derevnin-ma/.m2/repository/org/projectlombok/lombok/1.18.22/lombok-1.18.22.jar:/Users/derevnin-ma/.m2/repository/org/mapstruct/mapstruct/1.5.2.Final/mapstruct-1.5.2.Final.jar`

#Development
fetch('http://localhost:8081')
.then(res => res.json())
.then(console.log)