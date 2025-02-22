package com.arbitragebroker.admin.service.impl;


import com.arbitragebroker.admin.entity.OneTimePasswordEntity;
import com.arbitragebroker.admin.entity.UserEntity;
import com.arbitragebroker.admin.repository.OneTimePasswordRepository;
import com.arbitragebroker.admin.service.OneTimePasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OneTimePasswordServiceImpl implements OneTimePasswordService {
    private final OneTimePasswordRepository repository;

    @Override
    public JpaRepository<OneTimePasswordEntity, Long> getRepository() {
        return repository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String create(UUID userId) {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);

        var entity = new OneTimePasswordEntity();
        entity.setPassword(String.valueOf(otp));
        entity.setUser(new UserEntity(){{setId(userId);}});
        repository.saveAndFlush(entity);

        return String.valueOf(otp);
    }

    @Override
    public boolean verify(UUID userId, String password) {
        var optional = repository.findByUserIdAndPasswordAndConsumedFalse(userId, password);
        var verify = optional.isPresent() && !optional.get().isExpired();
        if(verify) {
            OneTimePasswordEntity entity = optional.get();
            entity.setConsumed(true);
            repository.save(entity);
        }
        return verify;
    }
}
