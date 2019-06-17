# INFO.SEC.COOK3R
Project aimed at supporting the creation and execution of arbitrary Information Security Recipes.

![screenshot of main interface](./docs/screenshot.png)

## Guidelines
The base unit of computation is a **Task**.
Tasks are comprised of a set of a set of **inputs**, a set of **outputs**, and an inherent **behavior**.
**Recipes** can be composed by arbitrarily connecting Tasks’ inputs/outputs (as long as the respective types are compatible).
The application should be **extensible**, and support new Tasks without changing the base code.
Recipes can be **saved** and **reused** as Tasks on other recipes (composition).

## Technologies
Backend: Java, chosen for its excellent standard library (data-structures, multi-threading, ...);
Frontend: JavaScript + React, chosen for its ease of use and extensive use cases online.

## Architecture and Implementation
We chose to employ the **Pipes-and-Filters** architecture (_architectural pattern_), as this snugly fits our requirements (in fact, the pattern’s goals are perfectly aligned with this project’s goals). This architecture allows us to decompose a task into a series of separate and reusable elements, with the added advantage of ensuring separate elements can operate in isolation and scale independently (only communicating through the use of message _pipes_).
This architecture consists of the following basic components:
_Sources_, which act as data sources, and thus take no inputs;
_Sinks_, which act as data targets, and thus have no outputs;
_Filters_ (or _Handlers_), which transform or _filter_ the data they receive via the _pipes_ (perform operations on said data);
_Pipes_, which act as connectors that pass data between all the other components; thus, _pipes_ are a directional stream of data that (in our implementation) are implemented by a data buffer (a limited-capacity queue), in order to ensure flexibility and leeway between different components.
This network of interconnected components form a _Graph_, with the _Sources_/_Sinks_/_Filters_ acting as Nodes of the Graph, and the _Pipes_ acting as its edges. This Graph represents our _Recipe_.
**Related classes:** Node, Source, Sink, Handler.

In a lower-level of abstraction, we employ the **Publish/Subscribe** pattern for exchanging messages between Nodes of the Graph.

**Related classes:** Publisher and Subscriber interfaces, which are implemented by Source, Sink, and Handler.

(...) (...) (...)

### Architecture Patterns 
This application is based on the following architectures:
Pipes and filters
Register
Broker
Fanout messages
Event-driven

### Design Patterns
Factory
Strategy
Observer

### Architectural Choices
