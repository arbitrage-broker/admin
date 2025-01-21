//package com.arbitragebroker.admin.config;
//
//
//import com.arbitragebroker.admin.service.ParameterService;
//import com.arbitragebroker.admin.service.TelegramService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.util.ArrayList;
//import java.util.List;
//@Component
//@Slf4j
//public class TelegramBot extends TelegramLongPollingBot {
//    private final TelegramService telegramService;
//    private final String botToken;
//    private final String botUsername;
//
//    public TelegramBot(TelegramService telegramService, ParameterService parameterService) {
//        this.telegramService = telegramService;
//        this.botToken = parameterService.findByCode("TELEGRAM_BOT_TOKEN").getValue();
//        this.botUsername = parameterService.findByCode("TELEGRAM_BOT_USERNAME").getValue();
//    }
//
//
//    @Override
//    public String getBotUsername() {
//        return botUsername;
//    }
//
//    @Override
//    public String getBotToken() {
//        return botToken;
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasMessage()) {
//            String chatId = update.getMessage().getChatId().toString();
//
//            if (update.getMessage().hasText()) {
//                String messageText = update.getMessage().getText();
//                if(messageText.equalsIgnoreCase("/start")) {
//                    // Respond with a message to ask for the phone number
//                    SendMessage message = new SendMessage();
//                    message.setChatId(chatId);
//                    message.setText("Please share your phone number to proceed.");
//                    message.setReplyMarkup(getPhoneRequestKeyboard());
//
//                    try {
//                        execute(message);
//                    } catch (TelegramApiException e) {
//                        log.error(e.getMessage());
//                    }
//                }
//            } else if (update.getMessage().hasContact()) {
//                // Save phone number and chat ID to the database
//                String phoneNumber = update.getMessage().getContact().getPhoneNumber();
//                telegramService.save(chatId, phoneNumber);
//
//                SendMessage reply = new SendMessage();
//                reply.setChatId(update.getMessage().getChatId().toString());
//                reply.setText("Thanks for sharing your phone number: " + phoneNumber);
//                try {
//                    execute(reply);
//                } catch (TelegramApiException e) {
//                    log.error(e.getMessage());
//                }
//            }
//        }
//    }
//
//    // Helper method to create a keyboard for requesting phone number
//    private ReplyKeyboardMarkup getPhoneRequestKeyboard() {
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        KeyboardButton button = new KeyboardButton("Share Phone Number");
//        button.setRequestContact(true);
//
//        KeyboardRow row = new KeyboardRow();
//        row.add(button);
//
//        List<KeyboardRow> keyboard = new ArrayList<>();
//        keyboard.add(row);
//        keyboardMarkup.setKeyboard(keyboard);
//        keyboardMarkup.setOneTimeKeyboard(true); // Optional: Keyboard disappears after one use
//        keyboardMarkup.setResizeKeyboard(true);  // Optional: Adjusts keyboard size
//        return keyboardMarkup;
//    }
//
//    public void sendMessage(String chatId, String text) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText(text);
//
//        try {
//            execute(message); // Sends the message
//        } catch (TelegramApiException e) {
//            log.error("Fail to send telegram message.", e);
//        }
//    }
//
//}
