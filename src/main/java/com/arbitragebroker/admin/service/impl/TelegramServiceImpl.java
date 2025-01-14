package com.arbitragebroker.admin.service.impl;

import com.arbitragebroker.admin.entity.TelegramEntity;
import com.arbitragebroker.admin.enums.RoleType;
import com.arbitragebroker.admin.repository.TelegramRepository;
import com.arbitragebroker.admin.service.ParameterService;
import com.arbitragebroker.admin.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TelegramServiceImpl implements TelegramService {
    private final TelegramRepository repository;
    private final RestTemplate restTemplate;
    private final String apiUrl;
    public TelegramServiceImpl(TelegramRepository repository, RestTemplate restTemplate, ParameterService parameterService) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        String botToken = parameterService.findByCode("TELEGRAM_BOT_TOKEN").getValue();
        this.apiUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";
    }

    @Override
    public void save(String chatId, String phone) {
        if(repository.existsByChatId(chatId))
            return;
        var entity = new TelegramEntity();
        entity.setChatId(chatId);
        entity.setPhone(phone);
        entity.setRole(RoleType.SUPER_WISER);
        repository.save(entity);
    }

    @Override
    public void sendMessage(String chatId, String text) {
        Map<String, String> params = new HashMap<>();
        params.put("chat_id", chatId);
        params.put("text", text);
        params.put("parse_mode", "Markdown");

        try {
            restTemplate.postForObject(apiUrl, params, String.class);
        } catch (Exception e) {
            log.error("Failed to send message.", e);
        }
    }
    @Override
    public void sendToRole(String role, String text) {
        List<TelegramEntity> list = repository.findAllByRole(role);
        if(!CollectionUtils.isEmpty(list)) {
            for (TelegramEntity entity : list) {
                sendMessage(entity.getChatId(),text);
            }
        }
    }
}
