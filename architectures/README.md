# PIEKER Architectures Module

This module contains the classes that represent the architecture meta-model of the PIEKER Testing Platform along with
specific implementations of the meta-model for different kinds of architectures, e.g. Docker Compose. Additionally,
classes
to parse model descriptions (e.g. YAML files), generate models from the parsed data, modify the model and its entities,
and assemble the model into a format that can be used to execute the underlying software are provided.

## Packages

| Package                                                           | Description                                                                                                                                                              |
|-------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [`model`](./src/main/java/pieker/architectures/model)             | Contains the classes that represent the architecture meta-model of the PIEKER Testing Platform and specific implementations for supported architectures in sub-packages. |
| [`parser`](./src/main/java/pieker/architectures/parser)           | Contains classes to parse model descriptions (e.g. docker-compose.yml)                                                                                                   |
| [`generator`](./src/main/java/pieker/architectures/generator)     | Contains classes to generated a model and entities from parsed data for supported architecture formats.                                                                  |
| [`injector`](./src/main/java/pieker/architectures/injector)       | Contains classes to inject entities into an existing model.                                                                                                              |
| [`description`](./src/main/java/pieker/architectures/description) | Contains classes to model and parse the PIEKER Dependency Description.                                                                                                   |

## Supported Architecture Formats

### Currently

- (partially) Docker Compose (`docker-compose<*.yaml/*.yml>`)
    - Package: [`pieker.architectures.compose`](./src/main/java/pieker/architectures/compose)

### Planned

- Microsoft Azure (Terraform)
- ...

## The PIEKER Architecture Meta-Model

The PIEKER architecture meta-model is the representation of the architecture of a software system used within the PIEKER
Platform. It consists of components and links between components aggregated by a model class. Components represent
services, networks, or storages (e.g. databases), which can also be nested to represent more complex architectures.
Links represent unidirectional connections between components, e.g. API connections. Specific implementations of the
meta-model, e.g. for Docker Compose, extend this model and its entities to provided additional functionality required to
represent the corresponding architecture format.

![PIEKER Architecture Model](./doc/img/pieker_architecture_model.svg)

More information about working with the architecture meta-model and its implementations can be found in
the [architecture model README](./src/main/java/pieker/architectures/model/README.md).

## The PIEKER Dependency Description

The PIEKER dependency description is a YAML-based format that defines additional properties of the system under test, like
API connections between the components. This information is not part of the component-defining files, e.g. the Docker Compose
YAML, so that an additional specification of these relationships is necessary. Further information can be found 
[here](./src/main/java/pieker/architectures/description/README.md).
