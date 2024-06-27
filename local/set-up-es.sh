curl -X PUT -H 'Content-Type:application/json' --data @challenges-timestamp-ingest-pipeline.json http://elasticsearch:9200/_ingest/pipeline/add-timestamp

curl -X PUT -H 'Content-Type:application/json' --data @challenges-index.json http://elasticsearch:9200/challenges/_settings