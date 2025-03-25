<br>
<p align="center">
  <picture>
    <img src="img/pieker-logo.svg" width="220">
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
</p>

<hr/>

PIEKER (from _piken_ [german]: to poke, to prick; and _kieken_ [northern german]: (curiously) look for something) is a
testing and validation platform for distributed software systems that aims to provide a comprehensive solution for
integration and end-to-end testing in multi-service and cloud environments.

**Project Status:** 🚧 Under development (Proof of Concept)

For an overview of the development progress, see the [Roadmap](#roadmap).

## Contents

- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Test and Build](#test-and-build)
- [Usage](#usage)
- [Modules](#modules)
    - [App-Module](#app-module)
    - [Architectures-Module](#architectures-module)
    - [Code-Generators-Module](#code-generators-module)
    - [Common-Module](#common-module)
    - [DSL-Module](#dsl-module)
- [Roadmap](#roadmap)
- [License](#license)
- [Acknowledgements](#acknowledgements)

## Project Structure

```
Pieker
├── app               # PIEKER Application Module for Executable
├── architectures     # PIEKER Architectures Module
├── code-generators   # PIEKER Code Generators Module
├── common            # Module for common classes and utilities
├── dsl               # PIEKER DSL Module
├── img               # Image files
└── config            # General configuration files
```

## Getting Started

### Prerequisites

- Java 23
- Gradle (8.12)
- Git

### Installation

Execute: `git clone https://github.com/PIEKER/pieker.git`

### Test and Build

Run tests: `./gradlew test`

Build executables: `./gradlew assemble`

## Usage

TODO: Describe how to use the PIEKER Testing Platform using
the [Example System](https://github.com/PIEKER/example-system)

## Modules

### App-Module

TODO: This module contains the main class for the PIEKER Testing Platform executable.

- Directory: [`app/`](./app)
- Documentation: TODO

### Architectures-Module

This module contains classes representing the architecture meta-model of the PIEKER Testing Platform, including parsing,
generating, modifying, and assembling models for different architectures like Docker Compose.

- Directory: [`architectures/`](./architectures)
- Documentation: [Architecture-README](./architectures/README.md)
- Maintainer: [@simohlsen](https://github.com/simohlsen)

### Code-Generators-Module

This module contains classes to dynamically generate test code for test containers based on DSL inputs.

- Directory: [`code-generators/`](./code-generators)
- Documentation: [Code-Generators-README](./code-generators/README.md)
- Maintainer: [@YIllmann](https://github.com/YIllmann), [@simohlsen](https://github.com/simohlsen)

### Common-Module

This module contains classes for common utilities and functionalities used in other modules.

- Directory: [`common/`](./common)

### DSL-Module

This module contains classes to define and process the PIEKER Domain-Specific Language (DSL), including grammar
definition, parsing, and generating test code from Gherkin feature files.

- Directory: [`dsl/`](./dsl)
- Documentation: [DSL-README](./dsl/README.md)
- Maintainer: [@YIllmann](https://github.com/YIllmann)

## Roadmap

- [x] Initial concept and project setup
- [x] Define and implement architecture meta-model
- [x] Define and implement DSL for test specification
- [x] Automatic generation of test code from DSL
- [ ] _Automatic injection of test code into test containers and architectures_
- [ ] Automatic deployment of a test environment
- [ ] Automatic test execution and evaluation
- [ ] First prototype for testing Docker Compose architectures
- [ ] First release for Docker Compose architectures
- [ ] Expand functionality and architecture support (Azure, Terraform, ...)

## License

This software can be used under the terms of the MIT License, see [LICENSE](./LICENSE).

## Acknowledgements

This project is part of two Master's theses at the Software Engineering Group of Kiel University, Germany and supported
by Donner & Reuschel AG, Hamburg, Germany.

<p align="center">
  <img src="img/cau-logo.svg" alt="Kiel University Logo" height="50" hspace="50" vspace="20"/>
  <img src="img/dur-logo.svg" alt="Donner & Reuschel AG Logo" height="90" hspace="50"/>
</p>
