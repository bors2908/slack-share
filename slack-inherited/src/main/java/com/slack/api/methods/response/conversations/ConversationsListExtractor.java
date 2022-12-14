package com.slack.api.methods.response.conversations;

import com.slack.api.methods.response.PaginatedExtractor;
import com.slack.api.model.Conversation;
import java.util.Collection;

public class ConversationsListExtractor extends PaginatedExtractor<ConversationsListResponse, Conversation> {
    public ConversationsListExtractor(ConversationsListResponse response) {
        super(response);
    }

    @Override
    public String getNextCursor() {
        return getResponse().getResponseMetadata().getNextCursor();
    }

    @Override
    public Collection<Conversation> getCollection() {
        return getResponse().getChannels();
    }
}
