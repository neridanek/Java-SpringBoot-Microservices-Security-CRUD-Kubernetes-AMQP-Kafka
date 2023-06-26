package com.example.clients.notification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
    public Integer toCustomerId;
    public String toCustomerName;
    public String message;
}
