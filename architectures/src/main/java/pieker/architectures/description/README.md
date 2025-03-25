# PIEKER Dependency Description

The PIEKER Dependency Description is a YAML-based format that defines additional properties of the system under test.
In particular, links such as API connections between the components are not part of component-defining files, e.g. the 
Docker Compose YAML, so that an additional specification of these relationships is necessary.

## File Structure

```yaml
components:

  - name: "serviceA"
    links:
      - type: API
        target: "serviceC"
        targetUrlEnv: "SERVICE_C_URL"
      - type: STORAGE
        target: "db"
        usernameEnv: "DB_USERNAME"
        passwordEnv: "DB_PASSWORD"

  - name: "serviceB"
    links:
      - type: API
        target: "serviceD"
        targetUrlEnv: "SERVICE_D_URL"
```

The dependency description file consists of a list of `components`. Each component has a `name`, that equals its name 
in the architecture model defined by e.g. the Docker Compose YAML, and a list of `links` to other components. Each link 
has at least a `type` and a `target` component. The type specifies the kind of connection, e.g. `API` or `STORAGE` 
(defined in [ComponentLink.LinkType](../model/ComponentLink.java)), and the target is the name of the component that the
link points to. Links are unidirectional, i.e. if a connection is bidirectional, two connections must be defined for 
each component.

Additionally, each link type has specific properties that must be defined. For example, an `API` link must have at least
the name of the environment variable that contains the URL of the target component (`targetUrlEnv`).

## Object Structure

This model resembles the structure of objects that define the dependency description of the PIEKER Architectures Module.
The implementation of this model is located in the [`pieker.architectures.description`](.) package.

![PIEKER Dependency Description Model](../../../../../../doc/img/pieker_dependency_desc.svg)
