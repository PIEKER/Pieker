# PIEKER DSL

This document contains all information about architecture and grammar definition of the PIEKER DSL. During development
some ideas, open questions and identified challenges will be added [here](#challenges).

## Contents
- [Module Structure](#module-structure)
- [DSL](#dsl)
  - [Parser](#parser)
  - [Variables](#variables)
  - [Values](#values)
    - [Automatic Value Generation](#automatic-value-generation)
    - [Load Value from File](#load-value-from-file)
  - [Code-Engine](#code-engine)
  - [Validator](#validator)
  - [Test-Properties in Then](#test-properties-in-then)
- [Development Notes](#development-notes)
  - [Challenges](#challenges)
  - [ADF](#adf)

## Module Structure
| directory                                                                   | content                                                                                                      |
|-----------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------|
| [```antlr/```](src/main/java/pieker/dsl/antlr)                              | contains Gherkin grammar and the correlating generated Gherkin Parser.                                       |
| [```architecture/```](src/main/java/pieker/dsl/architecture)                | contains Java classes to process the PIEKER model tree to generate test code.                                |
| [```parser/```](src/main/java/pieker/dsl/parser)                            | contains Java classes to interpret the Gherkin resource (```.feature```) files and create runtime instances. |
| [```model/```](src/main/java/pieker/dsl/model)                              | contains Java classes representing the PIEKER model tree.                                                    |
| [```Main.java```](src/main/java/pieker/dsl/Main.java)                       | is the entrypoint class of this module. All module processes are orchestrated from here.                     |
| [```ArgumentHandler.java```](src/main/java/pieker/dsl/ArgumentHandler.java) | lists every possible argument to usable for pieker.dsl                                                       |
| [```resource/dsl```](src/main/resources/dsl)                                | directory to place Gherkin resource (```.feature```) files for further processing.                           |


## DSL
The DSL is designed to support Behaviour Driven Development. That means a test step can be defined using ``Given``, ``When``
and ``Then`` keywords. However, the scope of each keyword is slightly adapted in favour to the aim of this tool. 
Each section of a Gherkin (``.feature``) file is explained in the code example below. In the following, subsections of the
Gherking file are declared as:
- _test-configuration_: includes the whole file, starting on the 'feature' node
- _test-run_: includes a single section beginning with a 'scenario' node. (A _test-configuration_ can contain multiple _test-runs_)
- _test-step_: includes a single section beginning with a 'step' node. (A _test-run_ can contain multiple _test-steps_)

The idea is to allow a single configuration file but multiple run possibilities. Further, an integration test can be 
defined by including _test-runs_ via an enable flag or else.
A detailed example of the PIEKER DSL is located [here](./src/main/resources/dsl/example.feature).

### Parser

A brief overview of the [```.dsl/parse```](./src/main/java/pieker/dsl/parser) directory and its dependencies is documented
[here](./doc/images/classdiagram-dsl.svg). The parsing algorithm itself is documented [here](./doc/images/sequenzdiagramm-main-entry.svg).

In general the idea of the parsing algorithm is, to construct a traversable model-hierarchy. This hierarchy represents a Gherkin file
as accessible runtime Java classes. Its root node is the [Feature](./src/main/java/pieker/dsl/model/Feature.java) class. Walking
sub-nodes of each model, allows the developer to access specific characteristics and implement further processing. As displayed
in the parsing-sequence-diagram, the [FeatureParser](./src/main/java/pieker/dsl/parser/FeatureParser.java) orchestrates the construction
of the Pieker-Model-Tree (PMT). It provides an API method, requiring only a file-path along with a root node object. Calling the API, the
parser begins to initiate setup instances, meaning to load a Gherkin file and construct an AST based on 
ANTLR [grammar](./src/main/java/pieker/dsl/antlr/grammar) and [generated code](./src/main/java/pieker/dsl/antlr/gen). Depending on, whether 
the setup detects major syntax issues, violating the defined grammar, the parser continues to walk the AST translating all relevant
data to the provided root node object, the PMT. Due to the inheritance relationship of the FeatureParser class to the 
[PiekerParserBaseListener](./src/main/java/pieker/dsl/antlr/gen/PiekerParserBaseListener.java) as shown in the class diagram, the parser
implements node-specific 'Listener Methods'. Such methods are called by ParseTreeWalker, a dependency class of ANTLR, allowing to
implement the required translation behaviour. For example, entering a 'scenario' node, the parser retrieves meta-data and links the newly
created object with its predecessor node. Tracking of predecessor nodes requires caching information of the latest node, a walker 
has visited. To balance caching and processing, only 'feature', 'scenario' and 'step' nodes have listener methods. Each 
node following a 'step', such as 'given' 'when' and 'then', are handled directly after entering the correlating 'step' 
node. As a result only the 'feature' latest 'scenario' node , needs to be cached to add corresponding links. The walker 
uses depth-first-search supporting the implementation. Therefore, each 'step' processed, is rightfully linked to its 
predecessor 'scenario'.

### Variables
PIEKER DSL supports the usage of variables. Variables can be defined using the `def` keyword. The syntax is as follows:
```
@def identifier = value
```
Any leading or trailing spaces are handled. The value can be referenced using a `$` character:
```
@def body = "{"payload": $payload }"
@request $x
```
There are three ways to define variables among a _test-configuration_. First,
the user can configure global variables. Every 'feature' node contains an optional 'background' node. This node can be used
to define data that applies to every _test-run_ in the _test-configuration_. Therefore, every variable defined in this node
is always accessible. Second, the user may define data directly following to the 'scenario' node, making it usable for
every _test-step_ linked to the 'scenario' node. At last, some variables may be declared in a _test-step_ directly. Meeting the
requirements of BDD, they are allowed in a 'when' section only. Again, only the nodes linked to the 'step' node can access
the declarations. <br> <br>
Speaking of node-levels, some phrases are declared beforehand. Some node `A` is _lower_ than node `B`, when node `B` is a child of node 
`A` or a child of a sibling-node `C`. A node `C` is a sibling of node `A`, if node `C` can be accessed on the same level 
as node `A` and additionally have the same parent node. A parent-node is a node, that can be accessed by calling the 
upward linked model instance. Further, every parent of a parent node is declared a parent node, due to the PMT transitive 
characteristics. The child-node definition can be derived by the inverse definition of a parent. Further, a variable is declared as
v-node.<br>
Based on the node-level inside a PMT, a v-node belongs to a specific scope ('feature', 'scenario' , 'step'). 
Each scope is bound to the following conditions:
1. v-nodes reference only variables, registered as sibling-node or defined in a scope of a parent-node. 
2. a variable can be overloaded by a lower v-node. 
3. the dependency-graph, built to resolve all scope variables, is individually cyclic free.

During preprocessing a _test-configuration_ PIEKER traverses each scope relevant for a single _test-step_ in order to 
retrieve every defined v-node. Each scope is singularly processed based on the mentioned conditions. As a result every 
scope is correct and fully resolved. The latter means, that no variable-references occur in a v-node value. Therefore,
any scope of a child-node can use the parent-scope variables to resolve new declared variable-references. To guarantee
a fully resolved variables, PIEKER uses topological sorting on `VariableNode` objects. The main idea is to construct a
dependency graph with bidirectional edges. Beginning with v-nodes, having no incoming edges, each node, 
reachable by one step in the dependency graph, is refactored by a value injection of the current node. After a successful 
injection, the dependency relationship of the reworked node is removed. If such node is left with no dependency edges, 
the node is added to the processing queue.
Toposort is finished, when every node with zero dependency edges is processed. Toposort is _successfully_ finished, if there
are no edges with a dependency reference left. For further information see 
[VariableNode.java](./src/main/java/pieker/dsl/architecture/preprocessor/VariableNode.java).

### Values
Values represent the heart of a _test-configuration_. A value can be used to define input, proxy, as well as
output data. To allow broad possibilities of how a test can be specified PIEKER supports different ways of 
using values. The first and most obvious way is to set an atomic number or json directly into the DSL. However, values
such as request json can be large and complex, leading to decreased readability of a test configuration. Further, a test 
may require random data. To address these issues PIEKER uses PIEKER functions.  

#### Automatic Value Generation
To create a unique test-setup using a single DSL file, PIEKER allows a limited amount of automatic value generation. 
For instance, if the applicant wants to vary the traffic behaviour, input data or even the proxy target, different
PIEKER functions can be used to generate an individual scenario. PIEKER functions are:

- `:FLOATV(x,y)` - generating a float value in the interval of x to y. Caution: x,y have to be in float format
- `:INTV(x,y)` - generating an integer value in the interval of x to y. Caution: x,y have to be in int format
- `:RSTART(regex)REND` - generating a string matching the java-script regex as defined by [ECMAScript](https://262.ecma-international.org/#sec-patterns)

The string generation works with an opensource release called [regexp-gen](https://github.com/Cornutum/regexp-gen/tree/release-2.0.1?tab=readme-ov-file).
The automatic value generation of PIEKER does not allow variable references. Each value has to be atomic due to the 
special character possibilities of javascript regular expressions. Therefore, the Converter sets a value before any 
variable injection is performed.

Example
```gherkin
@def r1 = {"method": ":RSTART( (GET|POST|PUT|PATCH|DELETE) )REND", "body": ":RSTART( ^Regular expressions are ((odd|hard|stupid), )+but cool!$ )REND", "fooFloatA" : ":FLOATV( 0.0 , 5.0 )", "fooFloatB" : ":FLOATV( 10.0 , 15.0 )", "fooIntA" : ":INTV( 0 , 5)", "fooIntB" : ":INTV( 10 ,15 )"}
```

#### Load Value from File
API requests or SQL statements can vary in complexity. To improve readability PIEKER DSL provides a function loading
data from the provided resource directory (`--dir`). 
- `:FILE(path,index)` - loads file, located at the path and caches the content. The index argument indicates the 
contents position, iff the content is designed in a way, the tool can interpret the content as an array.

Example
```gherkin
@def r2 = :FILE(/r2.json)
@def r3 = :FILE(/db1_init.sql)
```
Example request.json
```json
[
  {
    "url": "/message",
    "method": "POST",
    "connectionTimeout": 0,
    "readTimeout": 0,
    "headers": {},
    "body": {}
  },
  {
    "url": "/counter",
    "method": "GET",
    "connectionTimeout": 0,
    "readTimeout": 0,
    "headers": {},
    "body": {}
  }
]
```

Example SQL file
```SQL
SELECT c.customer_id, c.customer_name, o.order_id, p.product_name, s.shipping_date, s.shipping_status
FROM customers c
JOIN orders o ON c.customer_id = o.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
JOIN products p ON oi.product_id = p.product_id
JOIN shipments s ON o.order_id = s.order_id
WHERE c.customer_status = 'active' AND s.shipping_status != 'delayed' AND o.order_date >= '2024-01-01';

SELECT e.employee_id, e.employee_name, e.salary, d.department_name, m.manager_name
FROM employees e
JOIN departments d ON e.department_id = d.department_id
JOIN managers m ON e.manager_id = m.manager_id
WHERE e.salary > (SELECT AVG(salary) FROM employees WHERE department_id = e.department_id) AND e.hire_date < '2020-01-01';

```

### Architecture-Engine
After creating a PIEKER-Model-Tree ([PMT](./src/main/java/pieker/dsl/model)), PIEKERs [Code-Engine](./src/main/java/pieker/dsl/architecture/Engine.java) starts 
operating on the provided structure. In the beginning, a [Converter](./src/main/java/pieker/dsl/architecture/preprocessor/Converter.java) 
traverses the PMT and translates variables as mentioned [here](#variables). The Converter creates a state of the PMT, 
that is readable for validation. The Validator checks for semantic pitfalls, that were not detected by the Parser beforehand. 
Details will be explained [here](#validator). After the [preprocessor](./src/main/java/pieker/dsl/architecture/preprocessor/) 
finished, the generator starts evaluating conditions of every _**test-step**_ in every _**test-scenario**_. 

Before going into any details of the Code-Engines algorithm, definitions of _test-step_ and _test-scenario_ are extended.
In [PIEKER DSL](#pieker---dsl) _test-step_ and _test-scenario_ are bound to syntactical properties of the domain-specific
language. Subsequently, a _test-step_ describes a single state of a software-test. A _test-step_ consists of an 
architectural description (`Given`), related but optional test-conditions (`When`) and monitored characteristics (`Then`). 
Conclusively, a _test-scenario_ describes multiple _test-steps_, which will be performed in a single run. Further,
a _**condition**_ is defined as a concatenation of a keyword with a finite amount of arguments. Each argument is separated 
by a delimiter (`request $requestIdent | $serviceIdent | $json`). Depending on the current BDD node (Given/When/Then) 
keywords vary, representing different architectural and code specific properties. Per definition of PIEKER a `Given` 
node contains information about an automatic-generated test-component. Possibilities are:
- Proxy (Service/Database):
  - `@service $identifier`
  - `@url $args` (IMPORTANT: `url` and `service` are exclusive iff they have the same service identifier)
  - `@database $identifier`
- Supervisor
  - `@request $args`
  - `@sql $args`
- Traffic Container:
  - `@passive REQUEST/SQL`
- Link Proxy
  - `@link $args`

Each architectural property is tagged with a unique identifier, to map any correlating
_**test-conditions**_. A _test-condition_ is located in a `When` node, describing a components' behavior.
Each condition is mapped to their template model, implementing a conditions' semantic, along with any defined meta-data from 
the PIEKER DSL. `Then` nodes are used to guide an evaluator module and specify information to monitor for any proxy component.

The Code-Engine is designed to take a PMT as an input and create output depending on the provided semantics. Detailed
(pre-)processing is managed by subclasses (Converter, Validator, ...) as mentioned before and patterns such as the 
[strategy pattern](./src/main/java/pieker/dsl/architecture/strategy) and cached in [template models](./src/main/java/pieker/dsl/architecture/template).
As an entrypoint, each PMT `step` child-node provides a method to evaluate _condition_ statements. It is to mention, the
difference of the expression '_condition_' and '_test-conditions_' in the context of PMT and PMT-Processing. 
The former is defined as before, whilst the latter describes only child-nodes of `When`. Each _condition_ has a 
[Keyword](./src/main/java/pieker/dsl/Keyword.java) prefix indicating the _conditions_' semantics. According to the
strategy design pattern, each Keyword uses a strategy class, which implements individual translation standards and
requirements. The sequence is structured as follows:

![Sequence Diagramm Code-Engine P1](./doc/images/component-diagramm-code-dsl.svg)

Every information specified in the DSL is stored in a PMT using DFS. The Engine preprocesses the PMT,
meaning validation of semantics, [variable injection](#variables) and value evaluation referenced by [PMT functions](#values).
The main goal of the Engine is to create a scenario object, providing data about architectural characteristics along with
a test-plan. A test-plan contains a list of actions, that are related to a certain _test-step_. As mentioned, the 
strategy package implements node-specific processing methods to create both architecture and condition instances. Due
to its tree-structural design the PMT is also traversed using DFS, allowing the Engine to generate a collection of Proxies
and Traffics, implied by the PMT nodes. At first, each node processing leads to a single proxy or traffic instance. 
However, some PMT nodes may reference the same component, only changing its characteristics throughout a _test-scenario_.
Finally, the Engine unifies each data, creating software components with step-individual behavior.

The dependencies of each package involved is stated in the following diagrams:

Class diagram of [pieker.dsl.architecture.component](./src/main/java/pieker/dsl/architecture/component)

![ClassDiagramm Component](./doc/images/classdiagram-component.svg)


Class diagram of [pieker.dsl.architecture.template](./src/main/java/pieker/dsl/architecture/template)

![ClassDiagramm Template](./doc/images/classdiagramm-template.svg)


Class diagram of [pieker.dsl.architecture.strategy](./src/main/java/pieker/dsl/architecture/strategy)

![ClassDiagramm Strategy](./doc/images/classdiagram-strategy.svg)


### Validator
The validator is designed to analyzes unbound PIEKER nodes containing significant information for the tools' behavior. 
A node is classified as an unbound PIEKER node, if it is not specifically matched by the Gherkin grammar and therefore 
its content not accessible by tree-walking methods. For instance, a list of arguments of a _condition_ is 
interpreted as an unbound PIEKER node. 

The main task of the validator component is to verify the amount of arguments provided for a _condition_ statement.
Each _condition_ is designed to contain or reference a test-component identifier. Therefore, the validator guarantees
the unique characteristics of such identifiers among any `Given` _condition_ and the exist characteristic among `When`
_conditions_.

Throughout the development process the validator will be extended by features PIEKER classifies as 'before-processing'
requirements.

### Test-Properties in Then
A key block of the PIEKER DSL represents the `Then` node. Here multiple Assertions and Logs can be specified, to define
required software characteristics. A _test-step_ 'passed' if all the required software characteristics are met. Regarding
a Multi-Service-Software, different components and their correlating content can be tested. Therefore, PIEKER 
differentiates between 'Traffic' and 'Database' characteristics. Depending on the specification, the test procedure
varies. The main idea is displayed in the following graphic:

![Structure Of a Then Block of a certain step](./doc/images/then-block-processing.svg)

To allow PIEKER an evaluation of the traffic performed during a _test-step_, each TestComponent has to make their 
traffic accessible. Therefore, every proxy produces logs in a standardized format. These logs should contain the 
traffic identifier related, as well as the required fields PIEKER has to check for. Additionally, every log should 
contain a timestamp.

Next to Traffic, databases contain a certain software state. As a result, assertions, testing specific data, are applied
on a database state during some test step. To reduce complexity of a database scheme and the possibilities of SQL request,
a `databaseBlock` uses a `Table` specification. The spec is used to enter a SQL statement for a specific table, in which 
assertions shall be performed. Consequently, simpler SQL statements can be generated to retrieve a single or a list of 
values. The simpler statements only contain a single column or column with an aggregator function as well as a where condition 
to retrieve desired data. An assertion 'passes' if each value of the column passes the assertion characteristics. It is 
to mention, to use SQL syntax and semantics to create the right dataset. PIEKER itself, only generates a basic structure, 
to reduce code generating complexity.

## Development Notes

processing Given/When vs Then: 
 - Given/When include random information about architectural changes and correlating manipulations -> processing translates such information
 - Then is structured depending on Traffic/Database scope. Each test property can be translated to an AssertionType -> no further processing required.

### Challenges
tbd.

### ADF

Trigger ADF via API URL. A challenge will be to authenticate with managed identity or else. The code snippet below is
an AI generated snippet, which has not been tested yet.


```java
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class TriggerAndCancelPipeline {
    public static void main(String[] args) {
        try {
            String accessToken = getManagedIdentityToken();
            if (accessToken == null) {
                System.out.println("Failed to retrieve access token.");
                return;
            }

            // Trigger the pipeline with parameters
            String triggerUrl = "https://management.azure.com/subscriptions/{subscriptionId}/resourceGroups/{resourceGroup}/providers/Microsoft.DataFactory/factories/{factoryName}/pipelines/{pipelineName}/createRun?api-version=2018-06-01";
            String parameters = "{\"parameters\": {\"param1\": \"value1\", \"param2\": \"value2\"}}";
            String runId = triggerPipeline(triggerUrl, accessToken, parameters);
            
            if (runId == null) {
                System.out.println("Failed to trigger pipeline.");
                return;
            }
            System.out.println("Triggered pipeline. Run ID: " + runId);

            // Wait for 30 seconds before stopping
            TimeUnit.SECONDS.sleep(30);

            // Stop the pipeline run
            String cancelUrl = "https://management.azure.com/subscriptions/{subscriptionId}/resourceGroups/{resourceGroup}/providers/Microsoft.DataFactory/factories/{factoryName}/pipelines/{pipelineName}/cancel?api-version=2018-06-01&runId=" + runId;
            stopPipeline(cancelUrl, accessToken);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String triggerPipeline(String url, String accessToken, String parameters) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(parameters.getBytes("utf-8"));
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().reduce("", String::concat);
            return response.replaceAll(".*\\"runId\\":\\"(.*?)\\".*", "$1");
        }
    }

    private static void stopPipeline(String url, String accessToken) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        int responseCode = connection.getResponseCode();
        System.out.println("Stop Pipeline Response Code: " + responseCode);
        System.out.println(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_ACCEPTED
            ? "Pipeline run stopped successfully."
            : "Failed to stop pipeline run.");
    }

    private static String getManagedIdentityToken() {
        try {
            URL url = new URL("http://169.254.169.254/metadata/identity/oauth2/token?resource=https://management.azure.com&api-version=2018-02-01");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Metadata", "true");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String response = reader.lines().reduce("", String::concat);
                return response.replaceAll(".*\\"access_token\\":\\"(.*?)\\".*", "$1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
```


    
