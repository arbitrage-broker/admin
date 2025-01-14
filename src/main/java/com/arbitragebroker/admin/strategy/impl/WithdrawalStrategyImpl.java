package com.arbitragebroker.admin.strategy.impl;

import com.arbitragebroker.admin.enums.CurrencyType;
import com.arbitragebroker.admin.enums.EntityStatusType;
import com.arbitragebroker.admin.model.SubscriptionModel;
import com.arbitragebroker.admin.model.WalletModel;
import com.arbitragebroker.admin.repository.WalletRepository;
import com.arbitragebroker.admin.service.*;
import com.arbitragebroker.admin.service.*;
import com.arbitragebroker.admin.strategy.NetworkStrategyFactory;
import com.arbitragebroker.admin.strategy.TransactionStrategy;
import com.arbitragebroker.admin.exception.InsufficentBalanceException;
import com.arbitragebroker.admin.exception.NotAcceptableException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class WithdrawalStrategyImpl implements TransactionStrategy {
    private final WalletRepository walletRepository;
    private final SubscriptionService subscriptionService;
    private final SubscriptionPackageService subscriptionPackageService;
    private final NetworkStrategyFactory networkStrategyFactory;
    private final String minWithdrawAmount;
    private final String subUserPercentage;
    private final String userPercentage;
    private final NotificationService notificationService;
    private final MailService mailService;
    private final UserService userService;
    private final TelegramService telegramService;

    public WithdrawalStrategyImpl(WalletRepository walletRepository, NetworkStrategyFactory networkStrategyFactory, SubscriptionService subscriptionService, SubscriptionPackageService subscriptionPackageService, ParameterService parameterService, NotificationService notificationService, MailService mailService, UserService userService, TelegramService telegramService) {
        this.walletRepository = walletRepository;
        this.networkStrategyFactory = networkStrategyFactory;
        this.subscriptionService = subscriptionService;
        this.subscriptionPackageService = subscriptionPackageService;
        this.minWithdrawAmount = parameterService.findByCode("MIN_WITHDRAW").getValue();
        this.subUserPercentage = parameterService.findByCode("SUB_USER_PERCENTAGE").getValue();
        this.userPercentage = parameterService.findByCode("USER_PERCENTAGE").getValue();
        this.notificationService = notificationService;
        this.mailService = mailService;
        this.userService = userService;
        this.telegramService = telegramService;
    }

    @Override
    public void beforeSave(WalletModel model) {
        model.setActualAmount(model.getAmount());
        model.setCurrency(CurrencyType.USDT);
        var totalBalance = walletRepository.totalBalanceByUserId(model.getUser().getId());
        if(totalBalance.compareTo(model.getAmount()) < 0)
            throw new InsufficentBalanceException();

        if(model.getStatus().equals(EntityStatusType.Active)) {
            synchronized (model.getUser().getId().toString().intern()) {
                BigDecimal totalDepositOfSubUsersPercentage = walletRepository.totalBalanceOfSubUsers(model.getUser().getId()).multiply(new BigDecimal(subUserPercentage));
                BigDecimal totalDepositOfMinePercentage = totalBalance.multiply(new BigDecimal(userPercentage));
                BigDecimal allowedWithdrawal = totalDepositOfMinePercentage.add(totalDepositOfSubUsersPercentage);
                allowedWithdrawal = allowedWithdrawal.subtract(walletRepository.totalWithdrawalProfitByUserId(model.getUser().getId()));
                if (allowedWithdrawal.compareTo(BigDecimal.ZERO) < 0)
                    allowedWithdrawal = BigDecimal.ZERO;
                else if (allowedWithdrawal.compareTo(totalBalance) > 0)
                    allowedWithdrawal = totalBalance;

                if (allowedWithdrawal.compareTo(model.getAmount()) < 0) {
                    throw new NotAcceptableException("""
                            You can withdraw your deposited amount up to : <strong>%d USD</strong>.<br/>
                            To increase your withdrawal amount, please bring more referrals!"""
                            .formatted(allowedWithdrawal.longValue()));
                }
            }
//            var currentSubscription = subscriptionService.findByUserAndActivePackage(model.getUser().getId());
//            if (currentSubscription.getRemainingWithdrawalPerDay() > 0)
//                throw new NotAcceptableException(String.format("Withdrawal is allowed only after %d days", currentSubscription.getRemainingWithdrawalPerDay()));
//            if (userService.countAllActiveChild(model.getUser().getId()) < currentSubscription.getSubscriptionPackage().getOrderCount())
//                throw new NotAcceptableException(String.format("To withdraw your funds you need to have at least %d referrals.", currentSubscription.getSubscriptionPackage().getOrderCount()));
        }
    }

    @Override
    public void afterSave(WalletModel model) {
        if(model.getStatus().equals(EntityStatusType.Active)) {
            var balance = walletRepository.calculateUserBalance(model.getUser().getId());
            var currentSubscription = subscriptionService.findByUserAndActivePackage(model.getUser().getId());
            var nextSubscriptionPackage = subscriptionPackageService.findMatchedPackageByAmount(balance);
            if(nextSubscriptionPackage == null)
                subscriptionService.logicalDeleteById(currentSubscription.getId());
            else if(currentSubscription == null || !currentSubscription.getSubscriptionPackage().getId().equals(nextSubscriptionPackage.getId())) {
                subscriptionService.create(new SubscriptionModel().setSubscriptionPackage(nextSubscriptionPackage).setUser(model.getUser()).setStatus(EntityStatusType.Active));
            }
            notificationService.sendTransactionNotification(model);
            try {
                mailService.sendTransactionMail(model);
            } catch (Exception ignored){}

            var user = userService.findById(model.getUser().getId());
            telegramService.sendToRole(user.getRole(), """
                *Activated Transaction*\n
                Date : %s\n
                User : %s\n
                Type : Withdrawal\n
                Amount : %s""".formatted(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), user.getSelectTitle(), model.getAmount().toString()));
        }
    }
}