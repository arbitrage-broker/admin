package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.dto.UserDetailDto;
import com.arbitragebroker.admin.filter.UserFilter;
import com.arbitragebroker.admin.model.UserModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserService extends BaseService<UserFilter, UserModel, UUID> {
    UserDetailDto findByUserNameOrEmail(String login);
    UserModel findByUserName(String userName);
    UserModel register(UserModel model);
    UserModel findByEmail(String email);
    long countAllActiveChild(UUID id);
    List<UserModel> findAllSilentUsers(LocalDateTime date);
    void deactivate(UUID id);
}
