# Persistence

To persist information, we decided to use MongoDB. Per the requirements, it had to be a NoSQL database, and among the available options, it seemed the most appropriate.

Mongo provides atomicity and concurrency guarantees for basic operations ($push, $pull, $inc) on a single document, which allows us to ensure that the number of registered users never exceeds the limit.

## Sharding

The shard key chosen for users is their ID. This makes sense since most queries are performed by ID; however, for the Login use case, the search is executed by the entered username. Consequently, the database must query all shards individually to locate the shard where the user resides.

This problem cannot be resolved within the current schema (we must shard either by ID or by username). The alternatives would be storing usernames and passwords in a separate collection, implementing another database (a key-value store like DynamoDB would be efficient for this case), or utilizing an external authentication service.

## Registration Limit

Documents in Mongo have a hard limit of 16MB. Currently, registered and waitlisted users are embedded within the main event document, meaning we would eventually hit this limit, and all subsequent registration requests would fail.

While this would not happen until reaching approximately ~400k registered users, it is still an architectural limitation that must be taken into account.

## Relations

Mongo is non-relational. We decided not to store embedded relationships between users and events, opting instead to store their corresponding ObjectIds. Although Mongo provides ways to aggregate information during a query using pipelines, we felt this adds unnecessary complexity and preferred to execute separate queries instead. We did, however, avoid performing N+1 queries for operations such as fetching all organizers for a list of events.

Furthermore, these relationships point to "static" objects (neither the username nor the event description should change frequently), so a robust caching layer should significantly speed up this process.
