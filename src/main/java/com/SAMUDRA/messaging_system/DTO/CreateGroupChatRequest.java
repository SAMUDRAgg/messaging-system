package com.SAMUDRA.messaging_system.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateGroupChatRequest {

    @NotBlank(message = "Group name is required")
    @Size(max = 100, message = "Group name must not exceed 100 characters")
    private String groupName;

    @NotEmpty(message = "At least one participant is required")
    private List<Long> participantIds;

    @Size(max = 500, message = "Profile picture URL too long")
    private String groupProfilePicUrl;

    public CreateGroupChatRequest() {}

    public CreateGroupChatRequest(
            List<Long> participantIds,
            String groupName,
            String groupProfilePicUrl
    ) {
        this.participantIds = participantIds;
        this.groupName = groupName;
        this.groupProfilePicUrl = groupProfilePicUrl;
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