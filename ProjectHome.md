Restlib is a Java library for implementing restful web services. It is in very early development and is mostly a hobby project being worked on in my spare time. The API is built around immutable objects and encourages extensive use of the decorator and builder patterns in implementation.

A key design tennet of the library is to separate IO from core request/response processing. This allows the library to be used with a variety of both thread and event based servers. Currently the goal is to support two main HTTP backends, the Java servlet API and Netty.

[Java Docs](http://docs.java-restlib.googlecode.com/git/index.html)