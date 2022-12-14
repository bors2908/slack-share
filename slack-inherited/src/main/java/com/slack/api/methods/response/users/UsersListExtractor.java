package com.slack.api.methods.response.users;

import com.slack.api.methods.response.PaginatedExtractor;
import com.slack.api.model.User;
import java.util.Collection;

public class UsersListExtractor extends PaginatedExtractor<UsersListResponse, User> {
    public UsersListExtractor(UsersListResponse response) {
        super(response);
    }

    @Override
    public String getNextCursor() {
        return getResponse().getResponseMetadata().getNextCursor();
    }

    @Override
    public Collection<User> getCollection() {
        return getResponse().getMembers();
    }
}