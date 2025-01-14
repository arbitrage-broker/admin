package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.TelegramEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelegramRepository extends BaseRepository<TelegramEntity, Long> {
    boolean existsByChatId(String chatId);
    boolean existsByPhone(String phone);
    List<TelegramEntity> findAllByRole(String role);
}
