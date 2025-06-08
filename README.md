# Flow
Flow is a codename for @jeremyalv's final university project. 

The project is a research which aims to investigate the extent to which Pulsar is able to provide better throughput and publish latency performance compared to Kafka for real-time analytics use cases. 

Specifically, we want to evaluate the streaming platform's performance using their defaults and when they are tuned to similar durability guarantees. In addition, it also aims to serve as a guide for organizations considering a streaming platform for real-time analytics use cases.

## Structure
The project will be located in a monorepo, with each components located in its own repository. We utilize a layer-driven design for structuring the repository (i.e. application layer, streaming layer, analytics layer)

1. `loadtest`
Contains source code relating to the load testing part of the research.

2. `ecommerce`
Contains e-commerce app source code in Java to expose service endpoints.

3. `streaming`
Contains stream broker deployment and configuration code

4. `analytics`
Contains Pinot deployment and configuration code
