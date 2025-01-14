package com.arbitragebroker.admin.controller.impl;

import com.arbitragebroker.admin.controller.LogicalDeletedRestController;
import com.arbitragebroker.admin.filter.NotificationFilter;
import com.arbitragebroker.admin.model.NotificationModel;
import com.arbitragebroker.admin.service.LogicalDeletedService;
import com.arbitragebroker.admin.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Notification Rest Service v1")
@RequestMapping(value = "/api/v1/notification")
public class NotificationRestController extends BaseRestControllerImpl<NotificationFilter, NotificationModel, Long> implements LogicalDeletedRestController<Long> {

    private NotificationService notificationService;

    public NotificationRestController(NotificationService service) {
        super(service);
        this.notificationService = service;
    }
    @Override
    public LogicalDeletedService<Long> getService() {
        return notificationService;
    }

    @GetMapping("findAll-unread-by-recipientId/{recipientId}")
    public ResponseEntity<Page<NotificationModel>> findAllUnreadByRecipientId(@PathVariable Long recipientId, @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(notificationService.findAllUnreadByRecipientId(recipientId,pageable));
    }
}
