package com.slack.api.methods.request;

public interface PaginatedRequest {
    void setCursor(String cursor);

    void setLimit(Integer limit);
}
