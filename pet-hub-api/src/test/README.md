## Mockito strict stubbing

At the moment, while using `@MockBean` annotation to mock/stub beans or method invocations, strict stubbing is not
enabled by default.
That results in duplicate code (stubbing - verifying) at the moment. We always want to have verify of mocked bean,
because some `void` methods could be invoked and tests won't cover that automatically.

Reason why we are using whole infrastructure of `@MockBean` (which requires whole application context) is because of
underlying configuration necessary for starting application.
Isolated service which could be tested with unit tests should not start whole application context, rather just use
Mockito (which has strict stubbing enabled by default).

More info can be found here: https://github.com/spring-projects/spring-framework/issues/33318
