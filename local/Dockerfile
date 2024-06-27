FROM alpine/curl:latest
WORKDIR /usr/local/bin
COPY set-up-es.sh .
COPY challenges-index.json .
COPY challenges-timestamp-ingest-pipeline.json .
CMD "CHMOD +X /usr/local/bin/set-up-es.sh"
ENTRYPOINT ["sh", "/usr/local/bin/set-up-es.sh"]