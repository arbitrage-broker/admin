package com.arbitragebroker.admin.service.impl;

import com.arbitragebroker.admin.entity.QSubscriptionEntity;
import com.arbitragebroker.admin.entity.SubscriptionEntity;
import com.arbitragebroker.admin.entity.UserEntity;
import com.arbitragebroker.admin.entity.WalletEntity;
import com.arbitragebroker.admin.enums.CurrencyType;
import com.arbitragebroker.admin.enums.EntityStatusType;
import com.arbitragebroker.admin.enums.RoleType;
import com.arbitragebroker.admin.enums.TransactionType;
import com.arbitragebroker.admin.filter.SubscriptionFilter;
import com.arbitragebroker.admin.mapping.SubscriptionMapper;
import com.arbitragebroker.admin.model.SubscriptionModel;
import com.arbitragebroker.admin.repository.SubscriptionPackageRepository;
import com.arbitragebroker.admin.repository.SubscriptionRepository;
import com.arbitragebroker.admin.repository.WalletRepository;
import com.arbitragebroker.admin.service.ParameterService;
import com.arbitragebroker.admin.service.SubscriptionService;
import com.arbitragebroker.admin.service.UserService;
import com.arbitragebroker.admin.util.DateUtil;
import com.arbitragebroker.admin.util.SessionHolder;
import com.arbitragebroker.admin.exception.BadRequestException;
import com.arbitragebroker.admin.exception.InsufficentBalanceException;
import com.arbitragebroker.admin.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.arbitragebroker.admin.util.MapperHelper.get;

@Service
public class SubscriptionServiceImpl extends BaseServiceImpl<SubscriptionFilter,SubscriptionModel, SubscriptionEntity, Long> implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPackageRepository subscriptionPackageRepository;
    private final UserService userService;
    private final WalletRepository walletRepository;
    private final SessionHolder sessionHolder;
    private final String selfFreeBonusAmount;

    public SubscriptionServiceImpl(SubscriptionRepository repository, SubscriptionMapper mapper, SubscriptionPackageRepository subscriptionPackageRepository, UserService userService, WalletRepository walletRepository, ParameterService parameterService, SessionHolder sessionHolder) {
        super(repository, mapper);
        this.subscriptionRepository = repository;
        this.subscriptionPackageRepository = subscriptionPackageRepository;
        this.userService = userService;
        this.walletRepository = walletRepository;
        this.sessionHolder = sessionHolder;
        this.selfFreeBonusAmount = parameterService.findByCode("SELF_FREE_BONUS_AMOUNT").getValue();
    }
    @Override
    public JpaRepository<SubscriptionEntity,Long> getRepository() {
        return subscriptionRepository;
    }

    @Override
    public Predicate queryBuilder(SubscriptionFilter filter) {
        BooleanBuilder builder = new BooleanBuilder();
        QSubscriptionEntity path = QSubscriptionEntity.subscriptionEntity;

        if(!RoleType.hasRole(RoleType.ADMIN)) {
            builder.and(path.user.roles.any().role.ne(RoleType.ADMIN));
            builder.and(path.role.eq(RoleType.firstRole()));
        }
        filter.getId().ifPresent(v -> builder.and(path.id.eq(v)));
        filter.getUserId().ifPresent(v -> builder.and(path.user.id.eq(v)));
        filter.getSubscriptionPackageId().ifPresent(v -> builder.and(path.subscriptionPackage.id.eq(v)));
        filter.getExpireDateFrom().ifPresent(v -> builder.and(path.expireDate.goe(DateUtil.toLocalDateTime(v))));
        filter.getExpireDateTo().ifPresent(v -> builder.and(path.expireDate.loe(DateUtil.toLocalDateTime(v))));
        filter.getDiscountPercentage().ifPresent(v -> builder.and(path.discountPercentage.eq(v)));
        filter.getFinalPriceFrom().ifPresent(v -> builder.and(path.finalPrice.goe(v)));
        filter.getFinalPriceTo().ifPresent(v -> builder.and(path.finalPrice.loe(v)));
        filter.getStatus().ifPresent(v -> builder.and(path.status.eq(v)));

        return builder;
    }

    @Override
    @Transactional
    public SubscriptionModel create(SubscriptionModel model){
        var entity = mapper.toEntity(model);
        var subscriptionPackage = subscriptionPackageRepository.findById(model.getSubscriptionPackage().getId()).orElseThrow(()-> new NotFoundException("No such subscriptionPackage with " + model.getSubscriptionPackage().getId()));

        entity.setRole(sessionHolder.getCurrentUser().getRole());
        entity.setStatus(EntityStatusType.Pending);
        if(subscriptionPackage.getDuration() <= 0)
            entity.setExpireDate(DateUtil.toLocalDateTime(4102444800000L));
        else {
            entity.setExpireDate(LocalDateTime.now().plusDays(subscriptionPackage.getDuration()));
        }
        entity.setFinalPrice(calculatePrice(subscriptionPackage.getPrice(), model.getDiscountPercentage()));
        if(model.getStatus().equals(EntityStatusType.Active)) {
            var balance = walletRepository.calculateUserBalance(entity.getUser().getId());
            if(balance.compareTo(BigDecimal.ZERO) <= 0)
                throw new InsufficentBalanceException();
            if(entity.getFinalPrice().compareTo(balance) > 0)
                throw new InsufficentBalanceException();
            deactivateOldActive(entity.getUser().getId());
            entity.setStatus(EntityStatusType.Active);
            addBonus(entity);
        }
        return mapper.toModel(subscriptionRepository.save(entity));
    }
    @Override
    @Transactional
    public SubscriptionModel update(SubscriptionModel model){
        var entity = subscriptionRepository.findById(model.getId()).orElseThrow(()-> new NotFoundException("No such subscription with " + model.getId()));
        if(entity.getStatus().equals(EntityStatusType.Active))
            throw new BadRequestException("subscription with Active status could not be update");

        if(model.getDiscountPercentage() != null)
            entity.setDiscountPercentage(model.getDiscountPercentage());
        if (get(()->model.getUser().getId()) != null)
            entity.setUser(new UserEntity().setUserId(model.getUser().getId()));

        if(!get(()-> model.getSubscriptionPackage().getId()).equals(entity.getSubscriptionPackage().getId())) {//subscription package changed
            var subscriptionPackage = subscriptionPackageRepository.findById(model.getSubscriptionPackage().getId()).orElseThrow(()-> new NotFoundException("No such subscriptionPackage with " + model.getSubscriptionPackage().getId()));
            entity.setSubscriptionPackage(subscriptionPackage);
            if(entity.getSubscriptionPackage().getDuration() <= 0)
                entity.setExpireDate(DateUtil.toLocalDateTime(4102444800000L));
            else {
                LocalDate.now().plusDays(entity.getSubscriptionPackage().getDuration());//The remaining days of previous subscription will be burned
            }
            BigDecimal finalPrice = calculatePrice(entity.getSubscriptionPackage().getPrice(), model.getDiscountPercentage());
            entity.setFinalPrice(finalPrice);
            entity.setStatus(EntityStatusType.Passive);
        }
        if(model.getStatus().equals(EntityStatusType.Active)) {
            var balance = walletRepository.calculateUserBalance(entity.getUser().getId());
            if(balance.compareTo(BigDecimal.ZERO) <= 0)
                throw new InsufficentBalanceException();
            if(entity.getFinalPrice().compareTo(balance) > 0)
                throw new InsufficentBalanceException();
            deactivateOldActive(entity.getUser().getId());
            entity.setStatus(EntityStatusType.Active);
            addBonus(entity);
        }
        return mapper.toModel(subscriptionRepository.save(entity));
    }
    private BigDecimal calculatePrice(BigDecimal originalPrice, Integer discountPercentage) {
        if(discountPercentage == null)
            return originalPrice;
        var discountAmount = originalPrice.multiply(new BigDecimal(discountPercentage).divide(new BigDecimal("100")));
        return originalPrice.subtract(discountAmount);
    }
    private void deactivateOldActive(UUID userId) {
        var userModel = userService.findById(userId);
        var oldActive = subscriptionRepository.findByUserIdAndStatus(userModel.getId(), EntityStatusType.Active);
        if(oldActive != null) {
            oldActive.setStatus(EntityStatusType.Passive);
            subscriptionRepository.saveAndFlush(oldActive);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionModel findByUserAndActivePackage(UUID userId) {
        return mapper.toModel(subscriptionRepository.findByUserIdAndStatus(userId, EntityStatusType.Active));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionModel> findAllExpiredFreeSubscriptions() {
        return mapper.toModel(subscriptionRepository.findAllBySubscriptionPackageIdAndStatusAndExpireDateBefore(6L,EntityStatusType.Active, LocalDateTime.now()));
    }

    private void addBonus(SubscriptionEntity entity) {
        var subscriptionPackage = subscriptionPackageRepository.findById(entity.getSubscriptionPackage().getId()).orElseThrow(()->new NotFoundException("SubscriptionPackage not found " + entity.getSubscriptionPackage().getId()));
        entity.setSubscriptionPackage(subscriptionPackage);
        if(entity.getSubscriptionPackage().getName().equals("Free")) {
            if(walletRepository.countAllByUserIdAndTransactionTypeAndStatus(entity.getUser().getId(),TransactionType.BONUS,EntityStatusType.Active) == 0L) {
                var bonusAmount = new BigDecimal(selfFreeBonusAmount);
                WalletEntity selfWallet = new WalletEntity();
                selfWallet.setStatus(EntityStatusType.Active);
                selfWallet.setAmount(bonusAmount);
                selfWallet.setActualAmount(bonusAmount);
                selfWallet.setUser(entity.getUser());
                selfWallet.setCurrency(CurrencyType.USDT);
                selfWallet.setTransactionType(TransactionType.REWARD);
                selfWallet.setRole(entity.getRole());
                walletRepository.save(selfWallet);
            }
        } else if(entity.getUser().getParent() != null) {
            var parentReferralBonus = entity.getSubscriptionPackage().getParentReferralBonus();
            WalletEntity parentWallet = new WalletEntity();
            parentWallet.setStatus(EntityStatusType.Active);
            parentWallet.setAmount(new BigDecimal(parentReferralBonus));
            parentWallet.setActualAmount(new BigDecimal(parentReferralBonus));
            parentWallet.setUser(entity.getUser().getParent());
            parentWallet.setCurrency(entity.getSubscriptionPackage().getCurrency());
            parentWallet.setTransactionType(TransactionType.BONUS);
            parentWallet.setRole(entity.getRole());
            walletRepository.save(parentWallet);
        }
    }
}
