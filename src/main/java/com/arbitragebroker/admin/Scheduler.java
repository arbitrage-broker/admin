package com.arbitragebroker.admin;

import com.arbitragebroker.admin.entity.UserEntity;
import com.arbitragebroker.admin.entity.WalletEntity;
import com.arbitragebroker.admin.enums.EntityStatusType;
import com.arbitragebroker.admin.enums.TransactionType;
import com.arbitragebroker.admin.mapping.WalletMapper;
import com.arbitragebroker.admin.model.SubscriptionModel;
import com.arbitragebroker.admin.model.WalletModel;
import com.arbitragebroker.admin.repository.WalletRepository;
import com.arbitragebroker.admin.service.*;
import com.arbitragebroker.admin.strategy.NetworkStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import static com.arbitragebroker.admin.util.MapperHelper.get;

@Component
@Slf4j
public class Scheduler {
    private final WalletRepository walletRepository;
    private final SubscriptionService subscriptionService;
    private final SubscriptionPackageService subscriptionPackageService;
    private final NotificationService notificationService;
    private final WalletMapper walletMapper;
    private final NetworkStrategyFactory networkStrategyFactory;
    private final UserService userService;
    private final TelegramService telegramService;
    private final MailService mailService;
    private final String selfFreeBonusAmount;
    private final String silentUserPeriod;

    public Scheduler(WalletRepository walletRepository, SubscriptionService subscriptionService,
                     SubscriptionPackageService subscriptionPackageService, NotificationService notificationService,
                     WalletMapper walletMapper, NetworkStrategyFactory networkStrategyFactory, UserService userService,
                     TelegramService telegramService, MailService mailService, ParameterService parameterService) {
        this.walletRepository = walletRepository;
        this.subscriptionService = subscriptionService;
        this.subscriptionPackageService = subscriptionPackageService;
        this.notificationService = notificationService;
        this.walletMapper = walletMapper;
        this.networkStrategyFactory = networkStrategyFactory;
        this.userService = userService;
        this.telegramService = telegramService;
        this.mailService = mailService;
        this.selfFreeBonusAmount = parameterService.findByCode("SELF_FREE_BONUS_AMOUNT").getValue();
        this.silentUserPeriod = parameterService.findByCode("SILENT_USER_PERIOD").getValue();
    }

    @Scheduled(cron = "0 */3 * * * *")
    public void validateTransactions() {
        log.info("Wallet Scheduler has started!");
        walletRepository.findAllByStatusAndTransactionHashIsNotNullAndTransactionType(EntityStatusType.Pending, TransactionType.DEPOSIT).forEach(we -> {
            WalletModel model = walletMapper.toModel(we);
            var network = networkStrategyFactory.get(model.getNetwork());
            boolean transactionIsValid = network.validate(model);
            if (transactionIsValid) {
                model.setStatus(EntityStatusType.Active);
                we.setStatus(EntityStatusType.Active);
                var balance = walletRepository.calculateUserBalance(model.getUser().getId());
                var currentSubscription = subscriptionService.findByUserAndActivePackage(model.getUser().getId());

                var subscriptionPackage = subscriptionPackageService.findMatchedPackageByAmount(balance);
                if (subscriptionPackage != null && (currentSubscription == null || !currentSubscription.getSubscriptionPackage().getId().equals(subscriptionPackage.getId()))) {
                    currentSubscription = subscriptionService.create(new SubscriptionModel().setSubscriptionPackage(subscriptionPackage).setUser(model.getUser()).setStatus(EntityStatusType.Active));
                }

                if (walletRepository.countByUserIdAndTransactionTypeAndStatus(model.getUser().getId(), TransactionType.DEPOSIT, EntityStatusType.Active) == 1) {
                    var user = we.getUser();
                    if (get(() -> user.getParent()) != null) {
                        var bonus1 = new WalletEntity();
                        bonus1.setStatus(EntityStatusType.Active);
                        bonus1.setUser(new UserEntity().setUserId(user.getParent().getId()));
                        bonus1.setAmount(BigDecimal.ONE);
                        bonus1.setActualAmount(BigDecimal.ONE);
                        bonus1.setTransactionType(TransactionType.BONUS);
                        bonus1.setRole(user.getRole());
                        walletRepository.save(bonus1);
                    }
                }
                notificationService.sendTransactionNotification(walletMapper.toModel(we));
                try {
                    mailService.sendTransactionMail(model);
                } catch (Exception ignored){}
                var user = userService.findById(model.getUser().getId());
                telegramService.sendToRole(user.getRole(), """
                **Activated Transaction**\n
                Date : %s\n
                User : %s\n
                Type : Depost\n
                Amount : %s""".formatted(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), user.getSelectTitle(), model.getAmount().toString()));
            } else {
                we.setStatus(EntityStatusType.Invalid);
            }
            walletRepository.save(we);
        });
    }

    @Scheduled(cron = "0 0 0 * * ?") // Cron expression for midnight
    public void deactivateSilentUsers() {
        subscriptionService.findAllExpiredFreeSubscriptions().forEach(sm -> {
            var withdrawalProfit = new WalletEntity();
            withdrawalProfit.setStatus(EntityStatusType.Active);
            withdrawalProfit.setUser(new UserEntity().setUserId(sm.getUser().getId()));
            withdrawalProfit.setAmount(new BigDecimal(selfFreeBonusAmount));
            withdrawalProfit.setActualAmount(new BigDecimal(selfFreeBonusAmount));
            withdrawalProfit.setTransactionType(TransactionType.WITHDRAWAL_PROFIT);
            withdrawalProfit.setRole(sm.getUser().getRole());
            walletRepository.save(withdrawalProfit);
        });
        userService.findAllSilentUsers(LocalDateTime.now().minusDays(30L)).forEach(user->{
            if (userService.countAllActiveChild(user.getId()) == 0) {
                userService.deactivate(user.getId());
            }
        });
    }
}
