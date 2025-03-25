# PIEKER Architecture Meta-Model

This package contains the classes that represent the architecture meta-model of the PIEKER Testing Platform.

Specific implementation of the meta-model for different kinds of architectures can be found the corresponding packages, 
e.g. for Docker Compose in [`pieker.architectures.compose`](../compose/model). The package [`common.model`](../common/model) 
contains general implementations of model entities that may be relevant for multiple implementations of the model.

### Meta-Model

![PIEKER Architecture Model](../../../../../../doc/img/pieker_architecture_model.svg)

### Model Implementations

#### Docker Compose

The following diagram shows a specific implementation of the PIEKER architecture meta-model for Docker Compose. Note that
classes and their attributes are not final. For now, this shows a basic implementation that should illustrate the concept.

![Docker Compose Architecture Model](../../../../../../doc/img/compose_architecture_model.svg)

- Package: [`pieker.architectures.compose`](../compose/model)
- Supported Features:
  - services
  - (coming soon) volumes, networks, ...

#### Common

- Package: [`pieker.architectures.common`](../common)
- Provided Functionality:
  - Model Entities:
    - Links:
      - [API-Link](../common/model/ApiLink.java)
      - ...