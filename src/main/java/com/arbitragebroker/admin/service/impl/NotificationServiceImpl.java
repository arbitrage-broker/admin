package com.arbitragebroker.admin.service.impl;


import com.arbitragebroker.admin.config.MessageConfig;
import com.arbitragebroker.admin.entity.NotificationEntity;
import com.arbitragebroker.admin.entity.QNotificationEntity;
import com.arbitragebroker.admin.enums.RoleType;
import com.arbitragebroker.admin.filter.NotificationFilter;
import com.arbitragebroker.admin.mapping.NotificationMapper;
import com.arbitragebroker.admin.model.NotificationModel;
import com.arbitragebroker.admin.model.UserModel;
import com.arbitragebroker.admin.model.WalletModel;
import com.arbitragebroker.admin.repository.NotificationRepository;
import com.arbitragebroker.admin.repository.UserRepository;
import com.arbitragebroker.admin.service.NotificationService;
import com.arbitragebroker.admin.util.SessionHolder;
import com.arbitragebroker.admin.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

import static com.arbitragebroker.admin.util.MapperHelper.getOrDefault;

@Service
public class NotificationServiceImpl extends BaseServiceImpl<NotificationFilter, NotificationModel, NotificationEntity, Long> implements NotificationService {
    private final NotificationRepository repository;
    private final NotificationMapper mapper;
    private final SessionHolder sessionHolder;
    private final UserRepository userRepository;
    private final MessageConfig messages;
    private final ResourceLoader resourceLoader;

    public NotificationServiceImpl(NotificationRepository repository, NotificationMapper mapper, SessionHolder sessionHolder, UserRepository userRepository, MessageConfig messages, ResourceLoader resourceLoader) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
        this.sessionHolder = sessionHolder;
        this.userRepository = userRepository;
        this.messages = messages;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public JpaRepository<NotificationEntity, Long> getRepository() {
        return repository;
    }

    @Override
    public Page<NotificationModel> findAllUnreadByRecipientId(Long recipientId, Pageable pageable) {
        return repository.findAllByRecipientIdAndReadIsFalse(recipientId, pageable).map(mapper::toModel);
    }
    @Override
    public NotificationModel findById(Long id) {
        var entity = repository.findById(id).orElseThrow(() -> new NotFoundException("id: " + id));
        if(entity.getRecipient().getId().equals(sessionHolder.getCurrentUser().getId()))
            entity.setRead(true);
        repository.save(entity);
        return mapper.toModel(entity);
    }

    @Override
    public Predicate queryBuilder(NotificationFilter filter) {
        BooleanBuilder builder = new BooleanBuilder();
        QNotificationEntity path = QNotificationEntity.notificationEntity;
        if(!RoleType.hasRole(RoleType.ADMIN)) {
            builder.and(path.role.eq(RoleType.firstRole()));
        }

        filter.getId().ifPresent(v -> builder.and(path.id.eq(v)));
        filter.getSenderId().ifPresent(v -> builder.and(path.sender.id.eq(v)));
        filter.getRecipientId().ifPresent(v -> builder.and(path.recipient.id.eq(v)));
        filter.getRecipients().ifPresent(v -> builder.and(path.recipient.id.in(v)));
        filter.getSubject().ifPresent(v -> builder.and(path.subject.toLowerCase().contains(v.toLowerCase())));
        filter.getBody().ifPresent(v -> builder.and(path.body.toLowerCase().contains(v.toLowerCase())));
        filter.getRead().ifPresent(v -> builder.and(path.read.eq(v)));
        
        return builder;
    }

    @Override
    public NotificationModel create(NotificationModel model) {
        if (model.isAllRecipients()) {
            userRepository.findAll().forEach(u->{
                model.setRole(u.getRole());
                model.setRecipient(new UserModel().setUserId(u.getId()));
                super.create(model);
            });
        }
        else if(!CollectionUtils.isEmpty(model.getRecipients())) {
            model.getRecipients().forEach(recipientId -> {
                var recipient = userRepository.findById(recipientId).orElseThrow(()->new NotFoundException("User not found"));
                model.setRole(recipient.getRole());
                model.setRecipient(new UserModel().setUserId(recipientId));
                super.create(model);
            });
            return model;
        }
        var recipient = userRepository.findById(model.getRecipient().getId()).orElseThrow(()->new NotFoundException("User not found"));
        model.setRole(recipient.getRole());
        return super.create(model);
    }

    @Override
    public NotificationModel update(NotificationModel model) {
        if (model.isAllRecipients()) {
            userRepository.findAll().forEach(user->{
                model.setRecipient(new UserModel().setUserId(user.getId()));
                super.update(model);
            });
        }
        else if(!CollectionUtils.isEmpty(model.getRecipients())) {
            model.getRecipients().forEach(recipientId -> {
                model.setRecipient(new UserModel().setUserId(recipientId));
                super.update(model);
            });
            return model;
        }
        return super.update(model);
    }

    @SneakyThrows
    @Override
    public void sendWelcomeNotification(UUID recipientId) {
        var recipient = userRepository.findById(recipientId).orElseThrow(()->new NotFoundException("User not found with id: " + recipientId));
        String siteName = messages.getMessage("siteName");
        String siteUrl = messages.getMessage("siteUrl");
        Resource emailTemplateResource = resourceLoader.getResource("classpath:templates/welcome-email.html");
        String emailContent;
        try (InputStream inputStream = emailTemplateResource.getInputStream();
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            emailContent = scanner.useDelimiter("\\A").next(); // Read the entire file into a String
        }
        emailContent = emailContent.replace("[user_first_name]", recipient.getFirstName());
        emailContent = emailContent.replace("[YourAppName]", siteName);
        emailContent = emailContent.replace("[YourSiteUrl]", siteUrl);

        create(new NotificationModel()
                .setSubject(String.format("Welcome to %s!", siteName))
                .setBody(emailContent)
                .setSender(new UserModel().setUserId(UUID.fromString("6303b84a-04cf-49e1-8416-632ebd84495e")))
                .setRecipient(new UserModel().setUserId(recipientId)));
    }
    @SneakyThrows
    public void sendTransactionNotification(WalletModel model) {
        var recipient = userRepository.findById(model.getUser().getId()).orElseThrow(()->new NotFoundException("User not found with id: " + model.getUser().getId()));
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

        // Send the notification
        create(new NotificationModel()
                .setSubject("Transaction Activity")
                .setBody(emailContent)
                .setSender(new UserModel().setUserId(UUID.fromString("6303b84a-04cf-49e1-8416-632ebd84495e")))
                .setRecipient(new UserModel().setUserId(recipient.getId())));
    }
}
