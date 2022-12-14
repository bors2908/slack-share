package com.slack.api.methods.request.conversations;

import com.slack.api.methods.request.PaginatedRequest;
import lombok.Builder;

public class ConversationsMembersRequestPaginated extends ConversationsMembersRequest implements PaginatedRequest {
    @Builder
    ConversationsMembersRequestPaginated(String token, String channel, String cursor, Integer limit) {
        super(token, channel, cursor, limit);
    }
}
