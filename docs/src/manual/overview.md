Haruspex    {#haruspex_index}
===========

# Overview {#haruspex_overview}

Haruspex is a comprehensive tracing framework designed to scale. The major elements composing The framwework are:

- an API for instrumenting programs written in various languages in order to generate trace data.
  The API allows to deliver trace data to files, to a messaging system such as Apache Kafka, or to anything
  a user may require to integrate with.

- a service abstracting trace-storage

- a service for batch analysis and extraction of features from traces

- a tool for interactive trace visualization, inspection and analysis

# How is Haruspex different from other open source tracing frameworks?
These are some of the characteristics that set Haruspex apart from other frameworks:

### Comprehensive Framework
Haruspex provides all the components required in the lifecycle of traces. In particular the framework covers:
  * generation of traces
  * storage of traces
  * batch analysis
  * interactive visualization and analysis

### Designed for Scalability
Haruspex can be used to instrument large distributed systems, with traces generated concurrently by
multiple machines delivered to the storage component over a messaging framework such as Apache Kafka.
The storage component can be an abstraction layer on top of a distributed database such as HBase.
A distributed batch Analysis service can extract features on filtered subsets of traces passing
through the messaging framework, and store those in a database for aggregated analysis and
visualization. An interactive tool can fetch traces from the distributed storage for visualization
and analysis.

In a different scenario Haruspex can be used to instrument a single program and store traces in files on disk,
and such traces can be visualized and analyzed in the interactive tool, withouth the need to run any server
or even an Internet connection.

So, scalability for us means both scaling up on the number of machines in a data center, as well as scaling down
to single-user, single process systems and no Internet connection. Also, Haruspex scales well on the size of 
traces, which can go from a few bytes to various gigabytes.


### Simple but powerful model
Haruspex trace model is based on the concepts of <i>trace</i>, <i>entity</i>, <i>block</i>, <i>point</i> and <i>interactions</i>.
- A trace contains entities, which represents concurrecy units such as threads
- An entity contains blocks, which are similar to the concept of Span seen in other frameworks
- A block contains points. Points are timestamped moments in time. 
- A pair of points can be associated by an interaction. An interaction represents a cause-effect
  relationship between two points. 

This model is more flexible than the one used by most other frameworks, where only a parent-child
relationship between blocks is supported, as it allows modeling of different type of interactions.

For example, an initiation of an asynchronous operation and its completion can be represented by
an interaction between the two corresponding points, which might belong to the same or different
blocks in a single entity.

Another example consists in capturing a scenario where a thread makes two RPC calls consecutively to 
different services. In a model where only block parent-child relationship are supported, it is
not possible to preserve the information about the ordering of the two calls, unless each 
call is captured in a separate block, which may not be the natural thing to do.

### Support for crash debugging
The Haruspex tracing API generates exactly one event for each API call and the event, which contains 
the information associated to the operation, is delivered to a sink component immediately. Depending
on configuration, the sink component may deliver the event immediately for storage or to a message
bus. Once this operation is completed a crash in the instrumented process does not prevent
the event from being delivered and contribute to the trace model that can be analyzed. Some other
frameworks deliver information to the equivalent of a sink only after a certain amount of local
buffering, which may prevent using the trace to debug a crash.

To be fair, other frameworks do this in an attempt to reduce the number of inter-process messages
produced. However Haruspex takes a different stance on this issue, and attempts to reduce 
dependencies on networking d
 



# Why "Haruspex"?

From Wikipedia:

> In the religion of Ancient Rome, a haruspex was a person trained to practice a form of divination called haruspicy (haruspicina) the inspection of the entrails..."

where

> Divination is the attempt to gain insight into a question or situation by way of an occultic, standardized process or ritual...

Tracing is a technique for "divinging" what is going on in program by instrumenting its guts, hence the name. 
