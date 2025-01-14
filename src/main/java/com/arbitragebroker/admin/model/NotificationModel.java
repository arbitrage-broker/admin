package com.arbitragebroker.admin.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class NotificationModel extends BaseModel<Long> {
    private UserModel sender;
    private UserModel recipient;
    private List<UUID> recipients;
    private boolean allRecipients;
    private String subject;
    private String body;
    private String role;
    private boolean read = false;
}
