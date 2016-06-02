#Overview

You're provided with an interface for a fictional InetAddress cache. The cache has a "Last-In-First-Out" (LIFO) retrieval policy and a "First-In-First-Out" (FIFO) eviction policy. Methods such as peek(), remove() and take() retrieve the most recently added element and an internal cleanup task that in periodic intervals removes the oldest elements from the cache.


#Instructions

* The goal is to write the most beautiful and functionally correct code possible
* Create an Implementation for AddressCache
* Document the Asymptotic complexity of each method
* A cleanup task runs every 5 seconds to evict expired address's
* The caching time of an element must be configurable by the user
* Include any tests that you have written to check the correctness
* You should design for optimal asymptotic performance
* You're free to use any Java Technology including Java 8
* Please email your solution in a zip file with the format: AddressCache_YourName.zip
