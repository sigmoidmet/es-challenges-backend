package net.burndmg.eschallenges.repository.run;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import reactor.core.publisher.MonoSink;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class MonoAsyncSearchAdapter implements ResponseListener {

    private final MonoSink<String> sink;

    @Override
    public void onSuccess(Response response) {
        try {
            // Read the response and send it to the Mono sink
            String responseBody = new String(response.getEntity()
                                                     .getContent()
                                                     .readAllBytes(), StandardCharsets.UTF_8);
            sink.success(responseBody);
        } catch (IOException e) {
            sink.error(e);
        }
    }

    @Override
    public void onFailure(Exception exception) {
        sink.error(exception);
    }
}
