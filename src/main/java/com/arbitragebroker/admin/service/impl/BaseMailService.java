package com.arbitragebroker.admin.service.impl;

import com.arbitragebroker.admin.config.MessageConfig;
import com.arbitragebroker.admin.model.WalletModel;
import com.arbitragebroker.admin.service.OneTimePasswordService;
import com.arbitragebroker.admin.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

import static com.arbitragebroker.admin.util.MapperHelper.getOrDefault;

public abstract class BaseMailService {
    private final UserService userService;
    private final MessageConfig messages;
    private final OneTimePasswordService oneTimePasswordService;
    private final ResourceLoader resourceLoader;
    @Value("${site.url}")
    String siteUrl;
    @Value("${site.name}")
    String siteName;
    @Value("${mailjet.token}")
    String token;
    @Value("${mailjet.url}")
    String url;

    public BaseMailService(UserService userService, MessageConfig messages, OneTimePasswordService oneTimePasswordService, ResourceLoader resourceLoader) {
        this.userService = userService;
        this.messages = messages;
        this.oneTimePasswordService = oneTimePasswordService;
        this.resourceLoader = resourceLoader;
    }

    public abstract void send(String to, String subject, String body);

    @SneakyThrows
    public void sendOTP(String to, String subject) {
        var entity = userService.findByEmail(to);
        var otp = oneTimePasswordService.create(entity.getId());
        String appName = messages.getMessage("siteName");

        // Load the email template as a stream
        Resource emailTemplateResource = resourceLoader.getResource("classpath:templates/otp-email.html");
        String emailContent;
        try (InputStream inputStream = emailTemplateResource.getInputStream();
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            emailContent = scanner.useDelimiter("\\A").next(); // Read the entire file into a String
        }

        emailContent = emailContent.replace("[user_first_name]", entity.getFirstName());
        emailContent = emailContent.replace("[YourAppName]", appName);
        emailContent = emailContent.replace("[otp_code]", otp);

        send(to, subject, emailContent);
    }

    @SneakyThrows
    public void sendVerification(String to, String subject) {
        var user = userService.findByEmail(to);
        var otp = oneTimePasswordService.create(user.getId());
        String appName = messages.getMessage("siteName");
        String link = String.format("https://%s/api/v1/user/verify-email/%s/%s",siteUrl, user.getId().toString(), otp);

        // Load the email template as a stream
        Resource emailTemplateResource = resourceLoader.getResource("classpath:templates/verification-email.html");
        String emailContent;
        try (InputStream inputStream = emailTemplateResource.getInputStream();
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            emailContent = scanner.useDelimiter("\\A").next(); // Read the entire file into a String
        }

        emailContent = emailContent.replace("[user_first_name]", user.getFirstName());
        emailContent = emailContent.replace("[YourAppName]", appName);
        emailContent = emailContent.replace("[verification_code]", link);
        emailContent = emailContent.replace("[verification_link]", link);

        send(to, subject, emailContent);
    }

    @SneakyThrows
    public void sendTransactionMail(WalletModel model) {
        var recipient = userService.findById(model.getUser().getId());
        String siteName = messages.getMessage("siteName");
        String siteUrl = messages.getMessage("siteUrl");

        // Load the email template as a stream
        Resource emailTemplateResource = resourceLoader.getResource("classpath:templates/transaction-email.html");
        String emailContent;
        try (InputStream inputStream = emailTemplateResource.getInputStream();
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            emailContent = scanner.useDelimiter("\\A").next(); // Read the entire file into a String
        }

        // Replace placeholders with actual values
        emailContent = emailContent.replace("[user_first_name]", recipient.getSelectTitle())
                .replace("[transaction_type]", model.getTransactionType().getTitle())
                .replace("[amount]", NumberFormat.getCurrencyInstance(Locale.US).format(model.getAmount()))
                .replace("[transaction_date]", getOrDefault(()-> model.getCreatedDate().toString(),"---"))
                .replace("[wallet_address]", getOrDefault(()->model.getAddress(),"---"))
                .replace("[YourAppName]", siteName)
                .replace("[YourSiteUrl]", siteUrl);

        send(recipient.getEmail(), "Transaction Activity", emailContent);
    }
}
