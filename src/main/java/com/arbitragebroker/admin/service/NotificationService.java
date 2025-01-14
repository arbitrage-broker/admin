package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.filter.NotificationFilter;
import com.arbitragebroker.admin.model.NotificationModel;
import com.arbitragebroker.admin.model.WalletModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService extends BaseService<NotificationFilter, NotificationModel, Long>, LogicalDeletedService<Long>{
    Page<NotificationModel> findAllUnreadByRecipientId(Long recipientId, Pageable pageable);
    void sendWelcomeNotification(UUID recipientId);
    void sendTransactionNotification(WalletModel walletModel);
}
