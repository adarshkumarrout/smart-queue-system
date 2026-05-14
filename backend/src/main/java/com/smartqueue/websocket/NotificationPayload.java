package com.smartqueue.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationPayload {
    private String type;
    private String message;
    private String tokenNumber;
    private Integer position;
}
