package com.arbitragebroker.admin.enums;

import com.arbitragebroker.admin.config.MessageConfig;
import com.arbitragebroker.admin.model.Select2Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public enum CurrencyType {
    USDT("USDT"),
    TRX("trx"),
    BNB("BNBUSDT"),
    BUSD("BUSD"),
//    SOL("SOL"),
    ;

    CurrencyType(String title) {
        this.title = title;
    }
    private MessageConfig messages;
    private String title;
    public MessageConfig getMessages() {
        return messages;
    }

    public void setMessages(MessageConfig messages) {
        this.messages = messages;
    }

    public String getTitle() {
        return messages.getMessage(title);
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public static List<Select2Model> getAll() {
        return Arrays.stream(values()).map(m -> new Select2Model(m.name(), m.getTitle())).collect(Collectors.toList());
    }

    @Component
    public static class EnumValuesInjectionService {

        @Autowired
        private MessageConfig messages;

        //Inject into bean through static inner class and assign value to enum.
        @PostConstruct
        public void postConstruct() {

            for (CurrencyType type : EnumSet.allOf(CurrencyType.class)) {
                type.setMessages(messages);
            }
        }
    }
}
