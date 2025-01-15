package com.arbitragebroker.admin.service.impl;

import com.arbitragebroker.admin.config.MessageConfig;
import com.arbitragebroker.admin.service.OneTimePasswordService;
import com.arbitragebroker.admin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import static com.arbitragebroker.admin.util.MapperHelper.getOrDefault;
import static org.apache.logging.log4j.util.LambdaUtil.get;

@Service
@Slf4j
@Profile("dev")
public class FakeMailServiceImpl extends BaseMailService {

    public FakeMailServiceImpl(UserService userService, MessageConfig messages, OneTimePasswordService oneTimePasswordService, ResourceLoader resourceLoader) {
        super(userService, messages, oneTimePasswordService, resourceLoader);
    }

    @Override
    public void send(String to, String subject, String body) {
        ;
    }
}
