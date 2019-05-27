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


### TO DO
Review [general notes on pub/sub architectures](https://github.com/hugoferreira/asso-pipes-and-stuff-v19#general-notes-on-pubsub-architectures), and assess if all concerns were considered.

_e.g.:_
- [ ] **Data integrity**: return unacknowledged messages to a subscriber's message queue after a given delay;
- [ ] **Queue _fanout_ / message delivery**: allow _fanout_ and _round-robin_ message deliveries for different purposes (_fanout_ for high troughput, _round-robin_ for ensuring pipeline integrity when needed).
