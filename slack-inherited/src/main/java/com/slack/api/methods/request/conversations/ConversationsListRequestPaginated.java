package com.slack.api.methods.request.conversations;

import com.slack.api.methods.request.PaginatedRequest;
import com.slack.api.model.ConversationType;
import java.util.List;
import lombok.Builder;

public class ConversationsListRequestPaginated extends ConversationsListRequest implements PaginatedRequest {
    @Builder
    ConversationsListRequestPaginated(
        String token,
        String cursor,
        boolean excludeArchived,
        Integer limit,
        List<ConversationType> types,
        String teamId
    ) {
        super(token, cursor, excludeArchived, limit, types, teamId);
    }
}
