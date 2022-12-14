package com.slack.api.methods.response;

import com.slack.api.methods.SlackApiTextResponse;
import java.util.Collection;

public abstract class PaginatedExtractor<T extends SlackApiTextResponse, R> {
    private T response;

    public PaginatedExtractor(T response) {
        this.response = response;
    }

    public T getResponse() {
        return response;
    }

    public abstract String getNextCursor();

    public abstract Collection<R> getCollection();
}
