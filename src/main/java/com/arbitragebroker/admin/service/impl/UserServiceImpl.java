package com.arbitragebroker.admin.service.impl;

import com.arbitragebroker.admin.dto.UserDetailDto;
import com.arbitragebroker.admin.entity.CountryEntity;
import com.arbitragebroker.admin.model.PageModel;
import com.arbitragebroker.admin.model.UserModel;
import com.arbitragebroker.admin.entity.QUserEntity;
import com.arbitragebroker.admin.entity.RoleEntity;
import com.arbitragebroker.admin.entity.UserEntity;
import com.arbitragebroker.admin.enums.RoleType;
import com.arbitragebroker.admin.filter.UserFilter;
import com.arbitragebroker.admin.mapping.UserMapper;
import com.arbitragebroker.admin.model.*;
import com.arbitragebroker.admin.repository.RoleRepository;
import com.arbitragebroker.admin.repository.UserRepository;
import com.arbitragebroker.admin.repository.WalletRepository;
import com.arbitragebroker.admin.service.NotificationService;
import com.arbitragebroker.admin.service.UserService;
import com.arbitragebroker.admin.util.ReferralCodeGenerator;
import com.arbitragebroker.admin.util.SessionHolder;
import com.arbitragebroker.admin.exception.BadRequestException;
import com.arbitragebroker.admin.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

import static com.arbitragebroker.admin.util.MapperHelper.get;


@Service
public class UserServiceImpl extends BaseServiceImpl<UserFilter, UserModel, UserEntity, UUID> implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final NotificationService notificationService;
    private final WalletRepository walletRepository;
    private final SessionHolder sessionHolder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder, NotificationService notificationService, WalletRepository walletRepository, SessionHolder sessionHolder) {
        super(userRepository, userMapper);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.notificationService = notificationService;
        this.walletRepository = walletRepository;
        this.sessionHolder = sessionHolder;
    }
    @Override
    @Cacheable(cacheNames = "${cache.prefix:app}", key = "'User:findByUserNameOrEmail:' + #login")
    public UserDetailDto findByUserNameOrEmail(String login) {
        var entity = userRepository.findByUserNameOrEmail(login, login).orElseThrow(() -> new NotFoundException("User not found with username/email: " + login));
        return new UserDetailDto(entity);
    }

    @Override
    public UserModel findByUserName(String userName) {
        return mapper.toModel(userRepository.findByUserName(userName).orElseThrow(() -> new NotFoundException("userName: " + userName)));
    }

    @Override
    public Page<UserModel> findAll(UserFilter filter, Pageable pageable) {
        return super.findAll(filter, pageable).map(m-> {
            m.setDeposit(walletRepository.totalDepositByUserId(m.getId()));
            m.setWithdrawal(walletRepository.totalWithdrawalOrProfitByUserId(m.getId()));
            m.setReward(walletRepository.totalProfitByUserId(m.getId()));
            return m;
        });
    }

    @Override
    public PageModel<UserModel> findAllTable(UserFilter filter, Pageable pageable) {
        var data = super.findAllTable(filter, pageable);
        if(!CollectionUtils.isEmpty(data.getData())) {
            for (UserModel m : data.getData()) {
                m.setDeposit(walletRepository.totalDepositByUserId(m.getId()));
                m.setWithdrawal(walletRepository.totalWithdrawalOrProfitByUserId(m.getId()));
                m.setReward(walletRepository.totalProfitByUserId(m.getId()));
            }
        }
        return data;
    }

    @Override
    @Transactional
    public UserModel register(UserModel model) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByUserName(model.getUserName());
        if (optionalUserEntity.isPresent())
            throw new BadRequestException("userName is already taken!");

        var entity = mapper.toEntity(model);
        if (StringUtils.hasLength(model.getPassword()))
            entity.setPassword(bCryptPasswordEncoder.encode(model.getPassword()));
        else
            entity.setPassword(bCryptPasswordEncoder.encode("12345"));
        entity.setActive(true);
        var role = roleRepository.findByRole(RoleType.USER).get();
        entity.setRoles(new HashSet<>(Arrays.asList(role)));
        if (StringUtils.hasLength(model.getReferralCode())) {
            userRepository.findByUid(model.getReferralCode()).ifPresent(p -> entity.setParent(p));
        }
        var parent = findById(model.getParent().getId());
        entity.setRole(parent.getRole());
        entity.setUid(getUid());
        var createdUser = mapper.toModel(repository.save(entity));
        notificationService.sendWelcomeNotification(createdUser.getId());
        return createdUser;
    }

    @Override
    public UserModel findByEmail(String email) {
        return mapper.toModel(userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("No such email: " + email)));
    }

    @Override
    public long countAllActiveChild(UUID id) {
        return userRepository.countActiveChildrenByUserId(id);
    }

    @Override
    public List<UserModel> findAllSilentUsers(LocalDateTime date) {
        return mapper.toModel(userRepository.findAllSilentUsers(date));
    }

    @Override
    public void deactivate(UUID id) {
        userRepository.findById(id).ifPresent(user->{
            user.setActive(false);
            repository.save(user);
        });
    }

    private String getUid() {
        var uid = ReferralCodeGenerator.generateReferralCode();
        if(!userRepository.existsByUid(uid)) {
            return uid;
        }
        return getUid();
    }

    @Override
    @Transactional
    public UserModel update(UserModel model) {
        UserEntity entity = repository.findById(model.getId()).orElseThrow(() -> new NotFoundException("id: " + model.getId()));

        if(StringUtils.hasLength(model.getEmail()) && !entity.getEmail().equals(model.getEmail())) {
            model.setEmailVerified(false);
            entity.setEmailVerified(false);
        }
        if (StringUtils.hasLength(model.getPassword())) {
            model.setPassword(bCryptPasswordEncoder.encode(model.getPassword()));
            entity.setPassword(model.getPassword());
        }
        mapper.updateEntity(model, entity);

        if(get(()->model.getCountry().getId())!=null)
            entity.setCountry(entityManager.getReference(CountryEntity.class, model.getCountry().getId()));
        if(get(()->model.getParent().getId())!=null)
            entity.setParent(entityManager.getReference(UserEntity.class, model.getParent().getId()));
        if(!CollectionUtils.isEmpty(model.getRoles())) {
            entity.getRoles().clear();
            model.getRoles().forEach(roleModel -> {
                entity.getRoles().add(entityManager.getReference(RoleEntity.class, roleModel.getId()));
            });
        }
        return mapper.toModel(repository.save(entity));
    }

    @Override
    @Transactional
    public UserModel create(UserModel model) {
        var entity = mapper.toEntity(model);

        if (StringUtils.hasLength(model.getPassword()))
            entity.setPassword(bCryptPasswordEncoder.encode(model.getPassword()));
        else
            entity.setPassword(bCryptPasswordEncoder.encode("12345"));

        entity.setUid(getUid());
        if(get(()->model.getParent().getId())!=null){
            var parent = findById(model.getParent().getId());
            entity.setRole(parent.getRole());
        } else {
            var role = roleRepository.findById(model.getRoles().stream().findFirst().get().getId()).get();
            entity.setRole(role.getRole());
        }
        var createdUser = mapper.toModel(repository.save(entity));
        notificationService.sendWelcomeNotification(createdUser.getId());
        return createdUser;
    }

    @Override
    public Predicate queryBuilder(UserFilter filter) {
        BooleanBuilder builder = new BooleanBuilder();
        QUserEntity path = QUserEntity.userEntity;

        if(!RoleType.hasRole(RoleType.ADMIN)) {
            builder.and(path.roles.any().role.ne(RoleType.ADMIN));
            builder.and(path.role.eq(RoleType.firstRole()));
        }
        filter.getId().ifPresent(v -> builder.and(path.id.eq(v)));
        filter.getTitle().ifPresent(v -> builder.and(path.firstName.toLowerCase().contains(v.toLowerCase())).or(path.lastName.toLowerCase().contains(v.toLowerCase())));
        filter.getActive().ifPresent(v -> builder.and(path.active.eq(v)));
        filter.getUserName().ifPresent(v -> builder.and(path.userName.eq(v)));
        filter.getEmail().ifPresent(v -> builder.and(path.email.toLowerCase().eq(v.toLowerCase())));
        filter.getUid().ifPresent(v -> builder.and(path.uid.eq(v)));
        filter.getParentId().ifPresent(v -> builder.and(path.parent.id.eq(v)));
        filter.getTreePath().ifPresent(v -> {
            if(v.contains("%"))
                builder.and(Expressions.booleanTemplate("tree_path like {0}", v));
            else builder.and(path.treePath.contains(v));
        });
        filter.getRoles().ifPresent(v -> builder.and(path.roles.any().id.in(v)));
        filter.getWalletAddress().ifPresent(v -> builder.and(path.walletAddress.eq(v)));
        filter.getFirstName().ifPresent(v -> builder.and(path.firstName.toLowerCase().contains(v.toLowerCase())));
        filter.getLastName().ifPresent(v -> builder.and(path.lastName.toLowerCase().contains(v.toLowerCase())));
        filter.getHasParent().ifPresent(v-> { if(v) builder.and(path.parent.isNotNull()); else builder.and(path.parent.isNull());});
        filter.getProfileImageUrl().ifPresent(v -> builder.and(path.profileImageUrl.eq(v)));
        filter.getCountryId().ifPresent(v -> builder.and(path.country.id.eq(v)));
        filter.getEmailVerified().ifPresent(v-> builder.and(path.emailVerified.eq(v)));

        return builder;
    }
}
