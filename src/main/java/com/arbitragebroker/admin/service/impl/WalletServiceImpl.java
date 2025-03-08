package com.arbitragebroker.admin.service.impl;

import com.arbitragebroker.admin.service.RoleService;
import com.arbitragebroker.admin.service.SubscriptionService;
import com.arbitragebroker.admin.service.UserService;
import com.arbitragebroker.admin.service.WalletService;
import com.arbitragebroker.admin.entity.QWalletEntity;
import com.arbitragebroker.admin.entity.WalletEntity;
import com.arbitragebroker.admin.enums.EntityStatusType;
import com.arbitragebroker.admin.enums.RoleType;
import com.arbitragebroker.admin.enums.TransactionType;
import com.arbitragebroker.admin.filter.WalletFilter;
import com.arbitragebroker.admin.mapping.WalletMapper;
import com.arbitragebroker.admin.model.WalletModel;
import com.arbitragebroker.admin.repository.WalletRepository;
import com.arbitragebroker.admin.strategy.TransactionStrategyFactory;
import com.arbitragebroker.admin.util.DateUtil;
import com.arbitragebroker.admin.exception.InsufficentBalanceException;
import com.arbitragebroker.admin.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static com.arbitragebroker.admin.util.DateUtil.*;
import static com.arbitragebroker.admin.util.MapperHelper.get;
import static com.arbitragebroker.admin.util.MapperHelper.getOrDefault;

@Service
@Slf4j
public class WalletServiceImpl extends BaseServiceImpl<WalletFilter,WalletModel, WalletEntity, Long> implements WalletService {

    private final WalletRepository walletRepository;
    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final JPAQueryFactory queryFactory;
    private final RoleService roleService;
    private final TransactionStrategyFactory transactionStrategyFactory;

    public WalletServiceImpl(WalletRepository repository, WalletMapper mapper, SubscriptionService subscriptionService, UserService userService, JPAQueryFactory queryFactory, RoleService roleService, TransactionStrategyFactory transactionStrategyFactory) {
        super(repository, mapper);
        this.walletRepository = repository;
        this.subscriptionService = subscriptionService;
        this.userService = userService;
        this.queryFactory = queryFactory;
        this.roleService = roleService;
        this.transactionStrategyFactory = transactionStrategyFactory;
    }

    @Override
    public Predicate queryBuilder(WalletFilter filter) {
        BooleanBuilder builder = new BooleanBuilder();
        QWalletEntity path = QWalletEntity.walletEntity;

        if(!RoleType.hasRole(RoleType.ADMIN)) {
            builder.and(path.user.roles.any().role.ne(RoleType.ADMIN));
            builder.and(path.role.eq(RoleType.firstRole()));
        }
        filter.getId().ifPresent(value -> builder.and(path.id.eq(value)));
        filter.getAmount().ifPresent(value -> builder.and(path.amount.eq(value)));
        filter.getAmountFrom().ifPresent(value -> builder.and(path.amount.goe(value)));
        filter.getAmountTo().ifPresent(value -> builder.and(path.amount.loe(value)));
        filter.getActualAmount().ifPresent(value -> builder.and(path.actualAmount.eq(value)));
        filter.getActualAmountFrom().ifPresent(value -> builder.and(path.actualAmount.goe(value)));
        filter.getActualAmountTo().ifPresent(value -> builder.and(path.actualAmount.loe(value)));
        filter.getCurrency().ifPresent(value -> builder.and(path.currency.eq(value)));
        filter.getNetwork().ifPresent(value -> builder.and(path.network.eq(value)));
        filter.getTransactionType().ifPresent(value -> builder.and(path.transactionType.eq(value)));
        filter.getTransactionHash().ifPresent(value -> builder.and(path.transactionHash.eq(value)));
        filter.getUserId().ifPresent(value -> builder.and(path.user.id.eq(value)));
        filter.getStatus().ifPresent(value -> builder.and(path.status.eq(value)));
        filter.getAddress().ifPresent(value -> builder.and(path.address.eq(value)));
        filter.getRoleId().ifPresent(v->{
            var role = roleService.findById(v);
            builder.and(path.role.eq(role.getRole()));
        });

        return builder;
    }
    @Override
    @Transactional
    public WalletModel createFromAdmin(WalletModel model) {
        var balance = walletRepository.totalBalance(model.getRole());
        if(model.getTransactionType().equals(TransactionType.WITHDRAWAL)) {
            if(balance.compareTo(model.getAmount()) < 0)
                throw new InsufficentBalanceException();
        }
        model.setStatus(EntityStatusType.Active);
        if(model.getRoleId()!=null && !StringUtils.hasLength(model.getRole())) {
            var role = roleService.findById(model.getRoleId());
            model.setRole(role.getRole());
        }
        return super.create(model);
    }

    @Override
    @Transactional
    public WalletModel create(WalletModel model) {
        var user = userService.findById(model.getUser().getId());
        model.setRole(user.getRole());
        var transactionStrategy = transactionStrategyFactory.get(model.getTransactionType());
        transactionStrategy.beforeSave(model);
        var result = super.create(model);
        transactionStrategy.afterSave(model, new WalletModel());
        return result;
    }

    @Override
    @Transactional
    public WalletModel update(WalletModel model) {
        var entity = repository.findById(model.getId()).orElseThrow(() -> new NotFoundException(String.format("%s not found by id %d", model.getClass().getName(), model.getId().toString())));
        if(get(()->model.getUser().getId())!=null)
            entity.setUser(entityManager.getReference(entity.getUser().getClass(), model.getUser().getId()));
        var transactionStrategy = transactionStrategyFactory.get(model.getTransactionType());
        transactionStrategy.beforeSave(model);
        var oldModel = findById(model.getId());
        var result = mapper.toModel(repository.save(mapper.updateEntity(model, entity)));
        transactionStrategy.afterSave(result, oldModel);
        return result;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        WalletEntity entity = repository.findById(id).orElseThrow(() -> new NotFoundException("id: " + id));

        var balance = walletRepository.calculateUserBalance(entity.getUser().getId());
        var subscriptionModel = subscriptionService.findByUserAndActivePackage(entity.getUser().getId());
        if(subscriptionModel.getSubscriptionPackage().getPrice().compareTo(balance) > 0) {
            subscriptionService.logicalDeleteById(subscriptionModel.getId());
        }
        repository.delete(entity);
    }
    @Override
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

    @Override
    public BigDecimal totalBalance() {
        String role = null;
        if(!RoleType.hasRole(RoleType.ADMIN))
            role = RoleType.firstRole();
        return walletRepository.totalBalance(role);
    }
    @Override
    public BigDecimal totalDeposit() {
        String role = null;
        if(!RoleType.hasRole(RoleType.ADMIN))
            role = RoleType.firstRole();
        return walletRepository.totalDeposit(role);
    }
    @Override
    public BigDecimal totalWithdrawal() {
        String role = null;
        if(!RoleType.hasRole(RoleType.ADMIN))
            role = RoleType.firstRole();
        return walletRepository.totalWithdrawal(role);
    }
    @Override
    public BigDecimal totalBonus() {
        String role = null;
        if(!RoleType.hasRole(RoleType.ADMIN))
            role = RoleType.firstRole();
        return walletRepository.totalBonus(role);
    }
    @Override
    public BigDecimal totalReward() {
        String role = null;
        if(!RoleType.hasRole(RoleType.ADMIN))
            role = RoleType.firstRole();
        return walletRepository.totalReward(role);
    }

//    @Override
//    public BalanceModel totalProfit(UUID userId) {
//        var result = walletRepository.totalProfit(userId);
//        return result.stream()
//                .map(obj -> new BalanceModel(CurrencyType.valueOf((String) obj[0]),(BigDecimal) obj[1]))
//                .collect(Collectors.toList());
//    }

    @Override
    public Map<Long, BigDecimal> findAllWithinDateRange(long startDate, long endDate, TransactionType transactionType) {
        QWalletEntity path = QWalletEntity.walletEntity;
        DateTemplate<LocalDateTime> truncatedDate = Expressions.dateTemplate(LocalDateTime.class, "date_trunc('day', {0})", path.createdDate);
        var results = queryFactory.select(truncatedDate, path.amount.sum())
                .from(path)
                .where(truncatedDate.between(toLocalDateTime(startDate),toLocalDateTime(endDate)))
                .where(path.transactionType.eq(transactionType))
                .where(path.user.roles.any().id.eq(2L))
                .groupBy(truncatedDate)
                .orderBy(truncatedDate.asc())
                .fetch();
        Map<Long, BigDecimal> map = results.stream()
                .collect(Collectors.toMap(tuple -> toEpoch(tuple.get(truncatedDate)), tuple -> tuple.get(path.amount.sum())));

        var allDates = toLocalDate(startDate).datesUntil(toLocalDate(endDate).plusDays(1)).map(DateUtil::toEpoch);

        return allDates.collect(Collectors.toMap(epoch -> epoch, epoch -> map.getOrDefault(epoch, BigDecimal.ZERO)));
    }
}
