## Elasticsearch challenges backend service
This is a backend which allows leetcode-like challenges but for elasticsearch.

## How to make changes into challenges schema
We're utilizing two aliases - read and update to seamlessly change the schema with zero-downtime. 
Usually they point to the same index and may point to different ones only during a deployment of a new schema version.
1. All the inserts and updates are going to both aliases: update and read even if they're pointing to the same index.  Write operations are quite rare so the performance degradation is not really a problem here, but it will allow users to see the newly created challenges right away, give us a unique ID and is pretty easy to implement the consistent result without data loss. 
2. All the reads are going to the read alias

### The order of actions:
1. create a new challenges index. Use challenges-{major-migration-version} naming convention
2. Repoint update alias to this new index
3. Run /_reindex operation from the old index to the new one
4. Repoint read alias to the newly created index
5. Delete an old index

See: **resources/es/migration/V4** as an example of such migration