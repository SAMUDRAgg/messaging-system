package com.SAMUDRA.messaging_system.DTO;

import java.util.List;

public class CreateGroupChatRequest {

    private Long creatorId;
    private List<Long> participantIds;
    private String groupName;
    private String groupProfilePicUrl;

    // constructors
    public CreateGroupChatRequest() {}

    public CreateGroupChatRequest(
            Long creatorId,
            List<Long> participantIds,
            String groupName,
            String groupProfilePicUrl
    ) {
        this.creatorId = creatorId;
        this.participantIds = participantIds;
        this.groupName = groupName;
        this.groupProfilePicUrl = groupProfilePicUrl;
    }

    // getters & setters

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public List<Long> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupProfilePicUrl() {
        return groupProfilePicUrl;
    }

    public void setGroupProfilePicUrl(String groupProfilePicUrl) {
        this.groupProfilePicUrl = groupProfilePicUrl;
    }
}