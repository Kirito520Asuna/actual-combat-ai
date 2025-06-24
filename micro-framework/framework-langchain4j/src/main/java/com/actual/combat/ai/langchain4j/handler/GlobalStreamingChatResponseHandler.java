package com.actual.combat.ai.langchain4j.handler;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;


/**
 * @Author yan
 * @Date 2025/3/15 21:27:00
 * @Description
 */
@Slf4j
@SuperBuilder
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class GlobalStreamingChatResponseHandler implements StreamingChatResponseHandler {
    private FluxSink fluxSink;

    //public GlobalStreamingChatResponseHandler() {
    //    if (this.fluxSink == null) {
    //        Flux.create(sink -> this.fluxSink = sink);
    //    }
    //}

    @Override
    public void onPartialResponse(String partialResponse) {
        log.debug("[Global]-[Stream]-[onPartialResponse]::[{}]", partialResponse);
        if (fluxSink != null) {
            fluxSink.next(partialResponse);
        }
    }

    @Override
    public void onCompleteResponse(ChatResponse chatResponse) {
        log.debug("[Global]-[Stream]-[onCompleteResponse]::[{}]", chatResponse);
        if (fluxSink != null) {
            fluxSink.next(chatResponse);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (fluxSink != null) {
            fluxSink.error(throwable);
        }
    }
}
