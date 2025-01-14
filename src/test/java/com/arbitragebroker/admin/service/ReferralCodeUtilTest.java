package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.util.ReferralCodeGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ReferralCodeUtilTest {

    @Test
    public void referralCode_shoudRetun_8character() {
        var code = ReferralCodeGenerator.generateReferralCode();
        assertThat(code).isNotEmpty();
        assertThat(code.length()).isEqualTo(8);
    }
}
