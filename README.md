# Object Pool

An object pool implementation in Java which tried to overcome the shortcomings of existing pools:

* ability to control how objects are reused (last used, first used)
* provide statistics about pool operations
* provide information about pooled objects

The pool aims to be fast but at the same time provide a clean and flexible API. It borrows ideas, terminology and concepts from existing
object pools but at the same it tries to provide what's missing from existing pool implementations.

One of the major shortcoming of existing pools (in the opinion of the authors) is
the strategy around _optimal number of pooled objects_. Most pools implement
a _minimum idle objects_ concept, which is supposed to help in terms of
performance since there will be always a number of objects available to be
borrowed but also never keep more idle objects that _minimum idle objects_. However, this 
strategy creates in many cases an over provisioning situation when the total number
of objects across a given number of services is too high and services will not be able 
to create new objects.

The main goal of the pool is to provide objects as fast as possible but without creating more 
than a maximum number of objects. Desired hints should be provided to the pool
but the most efficient way to provide these objects without wasting resources should
be provided.