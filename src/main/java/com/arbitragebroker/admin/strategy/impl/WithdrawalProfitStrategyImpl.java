package com.arbitragebroker.admin.strategy.impl;

import com.arbitragebroker.admin.enums.CurrencyType;
import com.arbitragebroker.admin.enums.EntityStatusType;
import com.arbitragebroker.admin.exception.InsufficentBalanceException;
import com.arbitragebroker.admin.exception.NotAcceptableException;
import com.arbitragebroker.admin.exception.NotFoundException;
import com.arbitragebroker.admin.model.SubscriptionModel;
import com.arbitragebroker.admin.model.WalletModel;
import com.arbitragebroker.admin.repository.WalletRepository;
import com.arbitragebroker.admin.service.*;
import com.arbitragebroker.admin.service.impl.BaseMailService;
import com.arbitragebroker.admin.strategy.NetworkStrategyFactory;
import com.arbitragebroker.admin.strategy.TransactionStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class WithdrawalProfitStrategyImpl implements TransactionStrategy {
    private final WalletRepository walletRepository;
    private final NetworkStrategyFactory networkStrategyFactory;
    private final SubscriptionService subscriptionService;
    private final SubscriptionPackageService subscriptionPackageService;
    private final String minWithdrawAmount;
    private final String subUserPercentage;
    private final String userPercentage;
    private final NotificationService notificationService;
    private final BaseMailService mailService;
    private final TelegramService telegramService;
    private final UserService userService;

    public WithdrawalProfitStrategyImpl(WalletRepository walletRepository, NetworkStrategyFactory networkStrategyFactory, SubscriptionService subscriptionService, SubscriptionPackageService subscriptionPackageService, ParameterService parameterService, NotificationService notificationService, BaseMailService mailService, TelegramService telegramService, UserService userService) {
        this.walletRepository = walletRepository;
        this.networkStrategyFactory = networkStrategyFactory;
        this.subscriptionService = subscriptionService;
        this.subscriptionPackageService = subscriptionPackageService;
        this.minWithdrawAmount = parameterService.findByCode("MIN_WITHDRAW").getValue();
        this.subUserPercentage = parameterService.findByCode("SUB_USER_PERCENTAGE").getValue();
        this.userPercentage = parameterService.findByCode("USER_PERCENTAGE").getValue();
        this.notificationService = notificationService;
        this.mailService = mailService;
        this.telegramService = telegramService;
        this.userService = userService;
    }

    @Override
    public void beforeSave(WalletModel model) {
        model.setActualAmount(model.getAmount());
        model.setCurrency(CurrencyType.USDT);
        var totalProfit = walletRepository.totalProfitByUserId(model.getUser().getId());
        if (totalProfit.compareTo(model.getAmount()) < 0)
            throw new InsufficentBalanceException();

        if (model.getAmount().compareTo(new BigDecimal(minWithdrawAmount)) < 0)
            throw new InsufficentBalanceException(String.format("Your requested amount %s should greater than %s", model.getAmount().toString(), minWithdrawAmount));

        var entity = walletRepository.findById(model.getId()).orElseThrow(()-> new NotFoundException("Transaction not found"));
        if(model.getStatus().equals(EntityStatusType.Active) && !entity.getStatus().equals(EntityStatusType.Active)) {
            synchronized (model.getUser().getId().toString().intern()) {
                BigDecimal totalDepositOfSubUsersPercentage = walletRepository.totalBalanceOfSubUsers(model.getUser().getId()).multiply(new BigDecimal(subUserPercentage));
                BigDecimal totalDepositOfMinePercentage = walletRepository.totalBalanceByUserId(model.getUser().getId()).multiply(new BigDecimal(userPercentage));
                BigDecimal allowedWithdrawal = totalDepositOfMinePercentage.add(totalDepositOfSubUsersPercentage);
                allowedWithdrawal = allowedWithdrawal.subtract(walletRepository.totalWithdrawalProfitByUserId(model.getUser().getId()));
                if (allowedWithdrawal.compareTo(BigDecimal.ZERO) < 0)
                    allowedWithdrawal = BigDecimal.ZERO;
                else if (allowedWithdrawal.compareTo(totalProfit) > 0)
                    allowedWithdrawal = totalProfit;

                if (allowedWithdrawal.compareTo(model.getAmount()) < 0) {
                    throw new NotAcceptableException("""
                            You can withdraw your profit amount up to : <strong>%d USD</strong>.<br/>
                            To increase your withdrawal amount, please bring more referrals!"""
                            .formatted(allowedWithdrawal.longValue()));
                }
            }
        }
    }

    @Override
    public void afterSave(WalletModel newModedl,WalletModel oldModel) {
        if(newModedl.getStatus().equals(EntityStatusType.Active) && !oldModel.getStatus().equals(EntityStatusType.Active)) {
            var balance = walletRepository.calculateUserBalance(newModedl.getUser().getId());
            var currentSubscription = subscriptionService.findByUserAndActivePackage(newModedl.getUser().getId());
            var nextSubscriptionPackage = subscriptionPackageService.findMatchedPackageByAmount(balance);
            if(nextSubscriptionPackage == null)
                subscriptionService.logicalDeleteById(currentSubscription.getId());
            else if (currentSubscription == null || !currentSubscription.getSubscriptionPackage().getId().equals(nextSubscriptionPackage.getId())) {
                subscriptionService.create(new SubscriptionModel().setSubscriptionPackage(nextSubscriptionPackage).setUser(newModedl.getUser()).setStatus(EntityStatusType.Active));
            }
            notificationService.sendTransactionNotification(newModedl);
            try {
                mailService.sendTransactionMail(newModedl);
            } catch (Exception ignored){}

            var user = userService.findById(newModedl.getUser().getId());
            telegramService.sendToRole(user.getRole(), """
                *Activated Transaction*\n
                Date : %s\n
                User : %s\n
                Type : Withdrawal Profit\n
                Amount : %s""".formatted(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), user.getSelectTitle(), newModedl.getAmount().toString()));
        }
    }
}
