# PIEKER Webserver

This directory contains a web-application and a Spring application, to enable persistence of PIEKER test-runs.
In detail ``app`` stores source-files of a Vue-App and ``server`` the Spring backend. You can profit of the
application by running the [docker-compose.yml](docker-compose.yml) and update in the [gradle.properties](../gradle.properties)
the configuration of `publishToServer`. If this flag is set to a positive value, the tool tries to publish the
result to the configured ``publishUrl``. By default it uses the preconfigured localhost settings. Be cautious
with ports. Due to the fact, that most applications boot on 8080 in development and/or production, the configuration
uses 8079. Any changes in the URL requires potential updates in the [docker-compose.yml](./docker-compose.yml).

## App

The web application is a minimal setup with a single page to iterate through loaded data. It is designed to display
data of a PIEKER test-runs from a selected Scenario. It enables multiple technical entrypoints to build simple
frontend and more complex end-to-end tests. Currently, the implementation does not require any complex 
test procedures due to its simple design. Details about getting started with the Vue/Typescript setup are 
named in the apps [README](./app/README.md).

### Structure
| Directory | Description                                            | 
|-----------|--------------------------------------------------------|
| `assets`  | contains stylesheets and other resources               |
| `router`  | contains an index file for the router dependency       |
| `ts`      | contains Typescript only files to support Vue dynamics |
| `views`   | contains Vue files creating different pages            |


## Server
The Spring application is a minimal CRUD-based REST API server, designed for persisting results of a PIEKER test-run.
The server stores a designated object into a correlating db-schema. Further it enables endpoints to access 
run- and scenario-specific data. These endpoints are exposed for the web-application but can be accessed without any
authentication. Therefore, be cautious when deploying the Webserver application in your environment. It is strongly
recommended to extend security features when deploying in exposed environments. 

### Structur
| Directory    | Description                                       |
|--------------|---------------------------------------------------|
| `controller` | Endpoint classes                                  |
| `dbo`        | Entities                                          |
| `dto`        | Transfer objects for communication                |
| `repository` | Interfaces to access the configured database      |
| `service`    | Manager classes for loading and manipulating data | 
| `ui`         | Endpoint classes for page references              | 