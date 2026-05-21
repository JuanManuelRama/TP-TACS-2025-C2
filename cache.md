# Cache

For the caching layer, we decided to use Redis, taking advantage of its popularity, industry support, and ease of integration.

## Expiration

Since the core attributes of both User and Event are essentially static (usernames, descriptions, etc., do not change frequently), we opted not to assign an expiration time to their cache entries. Should it be necessary, an explicit function is available to invalidate keys manually.
Even so, Redis is configured with a memory limit, and upon reaching it, the LRU (Least Recently Used) policy is applied to evict entries automatically.

## Strategy

We adopted the Cache-Aside pattern due to its simplicity: the cache is used exclusively for performance optimization and does not participate in the core business logic.

For instance, although we could technically cache registration-related data (such as remaining capacity) by leveraging Redis's atomic operations, we decided against coordinating state across two separate sources (DB + cache). We determined that the added complexity does not justify the marginal benefit.
As a consequence, the system can continue to operate normally even if Redis goes down.

## Storage

Given that the entities are simple, they are serialized as plain text JSON within Redis. In a higher-scale scenario or one demanding greater efficiency, a more compact binary format or a more specialized schema could be evaluated.