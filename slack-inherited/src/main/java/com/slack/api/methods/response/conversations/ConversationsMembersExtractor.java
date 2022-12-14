package com.slack.api.methods.response.conversations;

import com.slack.api.methods.response.PaginatedExtractor;
import java.util.Collection;

public class ConversationsMembersExtractor extends PaginatedExtractor<ConversationsMembersResponse, String> {
    public ConversationsMembersExtractor(ConversationsMembersResponse response) {
        super(response);
    }

    @Override
    public String getNextCursor() {
        return getResponse().getResponseMetadata().getNextCursor();
    }

    @Override
    public Collection<String> getCollection() {
        return getResponse().getMembers();
    }
}
