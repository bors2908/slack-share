package com.slack.api.methods.request.users;

import com.slack.api.methods.request.PaginatedRequest;
import lombok.Builder;

public class UsersListRequestPaginated extends UsersListRequest implements PaginatedRequest {
    @Builder
    UsersListRequestPaginated(String token, String cursor, Integer limit, boolean includeLocale, boolean presence, String teamId) {
        super(token, cursor, limit, includeLocale, presence, teamId);
    }
}