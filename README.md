<br>
<p align="center">
  <picture>
    <img src=".img/pieker-logo.svg" width="220">
  </picture>
</p>
<h3 align="center">Automated Testing Platform for Multi-Service Software Systems</h3>

<p align="center">
  <a href="https://github.com/PIEKER/Pieker/actions/workflows/build.yml">
    <img src="https://github.com/PIEKER/Pieker/actions/workflows/build.yml/badge.svg" alt="Build Status"/>
  </a>
  <a href="https://sonarcloud.io/summary/new_code?id=pieker">
    <img src="https://sonarcloud.io/api/project_badges/measure?project=pieker&metric=alert_status" alt="Quality Gate Status"/>
  </a>
  <br>
  <a href="https://deepwiki.com/PIEKER/Pieker">
    <img src="https://deepwiki.com/badge.svg" alt="DeepWiki"/>
  </a>
</p>

<hr/>

PIEKER (from _piken_ [german]: to poke, to prick; and _kieken_ [northern german]: (curiously) look for something) is a
testing and validation platform for distributed software systems that aims to provide a comprehensive solution for
integration and end-to-end testing in multi-service and cloud environments.

**Project Status:** 🚧 Under development (Proof of Concept)

For an overview of the development progress, see the [Roadmap](#roadmap).

This project is managed here: [![GitHub](https://img.shields.io/badge/GitHub-PIEKER-black?logo=github)](https://github.com/PIEKER/Pieker)


## Contents

- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Test and Build](#test-build-and-run)
- [Usage](#usage)
- [Modules](#modules)
    - [App-Module](#app-module)
    - [Architectures-Module](#architectures-module)
    - [Common-Module](#common-module)
    - [DSL-Module](#dsl-module)
    - [Evaluator-Module](#evaluator-module)
    - [Generators-Module](#generators-module)
    - [Orchestrator-Module](#orchestrator-module)
    - [Web-Application](#web-application)
- [Roadmap](#roadmap)
- [License](#license)
- [Acknowledgements](#acknowledgements)

## Project Structure

```
Pieker
├── app            # PIEKER Application Module for Executable
├── architectures  # PIEKER Architectures Module
├── common         # Module for common classes and utilities
├── dsl            # PIEKER DSL Module
├── evaluator      # PIEKER Evaluation Module
├── generators     # PIEKER Generators Module
├── orchestrator   # PIEKER Orchestrator Module
└── web            # PIEKER Web Application
```

## Getting Started

### Prerequisites

- Java 24
- Git
- Docker
- Windows or Linux based OS

### Installation

Execute: `git clone https://github.com/PIEKER/pieker.git`

### Test, Build, and Run

Run tests: `./gradlew test`

Build executables: `./gradlew assemble`

Run the application with the configuration specified in [`gradle.properties`](./gradle.properties): `./gradlew :app:run`

## Usage

To use PIEKER with Docker Compose based systems you need:

  - Java 24 (JRE) installed
  - Docker Demon running

In the [`gradle.properties`](./gradle.properties) file set the required input properties to match your system. Required 
are at least:

- `docker-compose.yml` file with the architecture of your system
- [PIEKER Interface Description](architectures/src/main/java/pieker/architectures/description/README.md) file
- [PIEKER DSL](./dsl/README.md) file with the test specification

You can run PIEKER with the following command:

`./gradlew :app:run`

It is recommended to clear the `genDir` specified in the [`gradle.properties`](./gradle.properties) before every run.

## Modules

### App-Module

This module contains the main class for the PIEKER Testing Platform executable and defines the main execution workflow.

- Directory: [`app/`](./app)

### Architectures-Module

This module contains classes representing the architecture meta-model of the PIEKER Testing Platform, including parsing,
generating, modifying, and assembling models for different architectures like Docker Compose.

- Directory: [`architectures/`](./architectures)
- Documentation: [Architecture-README](./architectures/README.md)
- Maintainer: [@simohlsen](https://github.com/simohlsen)

### Common-Module

This module contains classes for common utilities and functionalities used in other modules.

- Directory: [`common/`](./common)

### DSL-Module

This module contains classes to define and process the PIEKER Domain-Specific Language (DSL), including grammar
definition, parsing, and generating test code from Gherkin feature files.

- Directory: [`dsl/`](./dsl)
- Documentation: [DSL-README](./dsl/README.md)
- Maintainer: [@YIllmann](https://github.com/YIllmann)

### Evaluator-Module

This module contains classes to start an evaluation of generated test output.

- Directory: [`evaluator/`](./evaluator)
- Maintainer: [@YIllmann](https://github.com/YIllmann)

### Generators-Module

This module contains classes to dynamically generate test code for test containers and the corresponding container 
images based on DSL inputs.

- Directory: [`generators/`](./generators)
- Documentation: [Code-Generators-README](./generators/README.md)
- Maintainer: [@YIllmann](https://github.com/YIllmann), [@simohlsen](https://github.com/simohlsen)

### Orchestrator-Module

This module contains classes to orchestrate the test execution process, including starting and stopping of the test
environment.

- Directory: [`orchestrator/`](./orchestrator)
- Documentation: [Orchestrator-README](./orchestrator/README.md)
- Maintainer: [@simohlsen](https://github.com/simohlsen)

### Web-Application

This module contains the web application for PIEKER.

- Directory: [`web/`](./web)
- Documentation: [Webserver-README](./web/README.md)
- Maintainer: [@YIllmann](https://github.com/YIllmann)

## Roadmap

- [x] Initial concept and project setup
- [x] Define and implement architecture meta-model
- [x] Define and implement DSL for test specification
- [x] Automatic generation of test code from DSL
- [x] Automatic injection of test code into test containers and architectures
- [x] Automatic deployment of a test environment
- [x] Automatic test execution and evaluation
- [x] First prototype for testing Docker Compose architectures
- [ ] _First release for Docker Compose architectures_
- [ ] Fully functional web application for test specification, execution, and evaluation
- [ ] Expand functionality and architecture support (Azure, Terraform, ...)

## License

This software can be used under the terms of the MIT License, see [LICENSE](./LICENSE).

## Acknowledgements

This project is part of two Master's theses at the Software Engineering Group of Kiel University, Germany and supported
by Donner & Reuschel AG, Hamburg, Germany.

<p align="center">
  <img src=".img/cau-logo.svg" alt="Kiel University Logo" height="50" hspace="50" vspace="20"/>
  <img src=".img/dur-logo.svg" alt="Donner & Reuschel AG Logo" height="90" hspace="50"/>
</p>
