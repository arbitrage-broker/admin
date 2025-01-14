package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.model.WalletModel;

import javax.validation.constraints.Email;

public interface MailService {
    void send(String to, String subject, String body);
    void sendOTP(String to, String subject);
    void sendVerification(@Email String email, String emailVerificationLink);
    void sendTransactionMail(WalletModel model);
}
