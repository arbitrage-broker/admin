package com.arbitragebroker.admin.service.impl;

import com.arbitragebroker.admin.config.MessageConfig;
import com.arbitragebroker.admin.dto.MailJetRequest;
import com.arbitragebroker.admin.dto.MailJetResponse;
import com.arbitragebroker.admin.model.WalletModel;
import com.arbitragebroker.admin.service.MailService;
import com.arbitragebroker.admin.service.OneTimePasswordService;
import com.arbitragebroker.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static com.arbitragebroker.admin.util.MapperHelper.getOrDefault;
import static org.apache.logging.log4j.util.LambdaUtil.get;

@Service
@RequiredArgsConstructor
@Slf4j
@Primary
public class MailJetServiceImpl implements MailService {
    private final RestTemplate restTemplate;
    private final OneTimePasswordService oneTimePasswordService;
    private final UserService userService;
    private final MessageConfig messages;
    private final ResourceLoader resourceLoader;
    @Value("${site.url}")
    String siteUrl;
    @Value("${site.name}")
    String siteName;
    @Value("${mailjet.token}")
    String token;
    @Value("${mailjet.url}")
    String url;

    @Override
    public void send(String to, String subject, String body) {
        try {
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Basic " + token);
            // Create HTTP entity
            var emailMessage = new MailJetRequest().setMessages(List.of(new MailJetRequest.Message()
                    .setSubject(subject)
                    .setHtmlPart(body)
                    .setFrom(new MailJetRequest.EmailAddress().setEmail("noreply@" + siteUrl).setName(siteName))
                    .setTo(List.of(new MailJetRequest.EmailAddress().setEmail(to).setName("Me")))
            ));
            HttpEntity<MailJetRequest> requestEntity = new HttpEntity<>(emailMessage, headers);


            var response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    MailJetResponse.class
            );
            if(!response.getStatusCode().is2xxSuccessful() ||
                    getOrDefault(()->response.getBody().getMessages().stream().noneMatch(m->m.getStatus().equals("success")),false)){
                log.error("failed to send email. {}", get(()->response.getBody()).toString());
                throw new IllegalStateException("failed to send email." + get(()->response.getBody().toString()));
            }
        } catch (Exception e) {
            log.error("failed to send email. {}", e.getMessage());
            throw new IllegalStateException("failed to send email." + e.getMessage());
        }
    }

    @SneakyThrows
    @Override
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

    @Override
    @SneakyThrows
    public void sendVerification(String to, String subject) {
        var user = userService.findByEmail(to);
        var otp = oneTimePasswordService.create(user.getId());
        String appName = messages.getMessage("siteName");
        String link = String.format("https://%s/api/v1/user/verify-email/%s",siteUrl, user.getId().toString(), otp);

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
    @Override
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
                .replace("[transaction_hash]", getOrDefault(()->model.getTransactionHash(),"---"))
                .replace("[wallet_address]", getOrDefault(()->model.getAddress(),"---"))
                .replace("[YourAppName]", siteName)
                .replace("[YourSiteUrl]", siteUrl);

        send(recipient.getEmail(), "Transaction Activity", emailContent);
    }
}
