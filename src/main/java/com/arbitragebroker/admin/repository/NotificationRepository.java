package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends BaseRepository<NotificationEntity, Long> {
    Page<NotificationEntity> findAllByRecipientIdAndReadIsFalse(Long recipientId, Pageable pageable);
}
