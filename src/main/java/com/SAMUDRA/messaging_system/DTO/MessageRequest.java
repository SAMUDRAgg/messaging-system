package com.SAMUDRA.messaging_system.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MessageRequest {

    @NotBlank(message = "Message content cannot be empty")
    @Size(max = 5000, message = "Message too long")
    private String content;

    // Optional (future use)
    private String attachmentUrl;

    private String attachmentType;
    // IMAGE, VIDEO, FILE, etc

    public MessageRequest() {}

    public MessageRequest(String content, String attachmentUrl, String attachmentType) {
        this.content = content;
        this.attachmentUrl = attachmentUrl;
        this.attachmentType = attachmentType;
    }

    public String getContent() {
        return content;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public String getAttachmentType() {
        return attachmentType;
    }
}