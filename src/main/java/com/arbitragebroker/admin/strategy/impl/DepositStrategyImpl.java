package com.arbitragebroker.admin.strategy.impl;


import com.arbitragebroker.admin.entity.WalletEntity;
import com.arbitragebroker.admin.enums.EntityStatusType;
import com.arbitragebroker.admin.enums.TransactionType;
import com.arbitragebroker.admin.model.SubscriptionModel;
import com.arbitragebroker.admin.model.WalletModel;
import com.arbitragebroker.admin.repository.UserRepository;
import com.arbitragebroker.admin.repository.WalletRepository;
import com.arbitragebroker.admin.service.*;
import com.arbitragebroker.admin.service.*;
import com.arbitragebroker.admin.service.impl.BaseMailService;
import com.arbitragebroker.admin.strategy.NetworkStrategyFactory;
import com.arbitragebroker.admin.strategy.TransactionStrategy;
import com.arbitragebroker.admin.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.arbitragebroker.admin.util.MapperHelper.get;

@Service
@RequiredArgsConstructor
public class DepositStrategyImpl implements TransactionStrategy {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final SubscriptionPackageService subscriptionPackageService;
    private final NetworkStrategyFactory networkStrategyFactory;
    private final NotificationService notificationService;
    private final BaseMailService mailService;
    private final TelegramService telegramService;

    @Override
    public void beforeSave(WalletModel model) {

    }

    @Override
    public void afterSave(WalletModel model) {
        var entity = walletRepository.findById(model.getId()).orElseThrow(()-> new NotFoundException("Transaction not found"));
        if(model.getStatus().equals(EntityStatusType.Active) && !entity.getStatus().equals(EntityStatusType.Active)) {
            var balance = walletRepository.calculateUserBalance(model.getUser().getId());
            var currentSubscription = subscriptionService.findByUserAndActivePackage(model.getUser().getId());

            var nextSubscriptionPackage = subscriptionPackageService.findMatchedPackageByAmount(balance);
            if (nextSubscriptionPackage != null && (currentSubscription == null || !currentSubscription.getSubscriptionPackage().getId().equals(nextSubscriptionPackage.getId()))) {
                subscriptionService.create(new SubscriptionModel().setSubscriptionPackage(nextSubscriptionPackage).setUser(model.getUser()).setStatus(EntityStatusType.Active));
            }
            var user = userRepository.findById(model.getUser().getId()).orElseThrow(()->new NotFoundException("user not found"));
            if (walletRepository.countByUserIdAndTransactionTypeAndStatus(model.getUser().getId(), TransactionType.DEPOSIT, EntityStatusType.Active) == 1) {
                if (get(() -> user.getParent()) != null) {
                    WalletEntity bonus1 = new WalletEntity();
                    bonus1.setStatus(EntityStatusType.Active);
                    bonus1.setUser(user.getParent());
                    bonus1.setAmount(referralDepositBonus(model.getAmount()));
                    bonus1.setActualAmount(referralDepositBonus(model.getAmount()));
                    bonus1.setTransactionType(TransactionType.BONUS);
                    bonus1.setRole(user.getRole());
                    walletRepository.save(bonus1);
                }
            }
            notificationService.sendTransactionNotification(model);
            try {
                mailService.sendTransactionMail(model);
            } catch (Exception ignored){}

            telegramService.sendToRole(user.getRole(), """
                *Activated Transaction*\n
                Date : %s\n
                User : %s\n
                Type : Deposit\n
                Amount : %s""".formatted(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), user.getSelectTitle(), model.getAmount().toString()));
        }
    }
    public BigDecimal referralDepositBonus(BigDecimal amount) {
//        List<ParameterModel> parameters = parameterService.findAllByParameterGroupCode("REFERRAL_DEPOSIT_BONUS");
//        for (ParameterModel parameter : parameters) {
//            // Check if the amount falls within the range
//            var split = parameter.getTitle().split("~");
//            BigDecimal lowerLimit = new BigDecimal(split[0]);
//            BigDecimal upperLimit = new BigDecimal(split[1]);
//            if (amount.compareTo(lowerLimit) >= 0 && amount.compareTo(upperLimit) <= 0) {
//                return new BigDecimal(parameter.getValue());
//            }
//        }
        return BigDecimal.ONE;
    }
}
