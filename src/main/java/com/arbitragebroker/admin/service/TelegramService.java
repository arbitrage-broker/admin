package com.arbitragebroker.admin.service;

public interface TelegramService {
    void save(String chatId, String phone);
    void sendMessage(String chatId, String text);
    void sendToRole(String role, String text);
}
