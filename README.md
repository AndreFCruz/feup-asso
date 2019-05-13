# FEUP-ASSO

* Generate permutations of test scenarios (given we have threads, it'll be difficult to check all possible combinations, so just generate scenarios)
  * Classes of Publishers/Subscribers that missbehave (_e.g._ with sleeps)
  * Check that Broker never blocks
  * e.g. [Mockito](https://site.mockito.org)
* Implement InfoSecCooker Entities
  * Source -> Publisher
  * Handler -> Publisher & Subscriber
  * Sink -> Subscriber


* Publisher publishes a message with a timeToLeave, when surprassed Broker may throw it away;
  * Or timeToLeave per Queue
