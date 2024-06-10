package com.morintd.newsletter.auth.dto;

import java.util.Objects;

public class RefreshDTO {
    private String userId;
    private String refreshId;

    public RefreshDTO() {

    }

    public RefreshDTO(String userId, String refreshId) {
        this.userId = userId;
        this.refreshId = refreshId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRefreshId() {
        return refreshId;
    }

    public void setRefreshId(String refreshId) {
        this.refreshId = refreshId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshDTO that = (RefreshDTO) o;
        return Objects.equals(userId, that.userId) && Objects.equals(refreshId, that.refreshId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, refreshId);
    }
}
