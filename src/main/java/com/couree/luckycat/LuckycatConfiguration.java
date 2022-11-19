package com.couree.luckycat;

import com.couree.luckycat.glacier.annotation.ApplicationConfiguration;
import com.couree.luckycat.glacier.annotation.Metadata;
import com.couree.luckycat.glacier.annotation.RegistryKey;
import com.couree.luckycat.glacier.constant.RegistryType;
import org.springframework.beans.factory.annotation.Value;

@ApplicationConfiguration(
    name = "Luckycat",
    dependency = {"Responder", "Phone", "Security"},
    metadata = {
        @Metadata(key = "Security.PasswordSecret", updatable = false),
        @Metadata(key = "Verification.SignUpByPhoneCodeLength", type = RegistryType.NUMBER, updatable = false),
        @Metadata(key = "Verification.SignUpByPhoneCodeBufferTime", type = RegistryType.NUMBER, updatable = false),
        @Metadata(key = "Verification.SignUpByPhoneCodeExpiration", type = RegistryType.NUMBER, updatable = false),
    }
)
public class LuckycatConfiguration {
    @Value("${security.user-password-secret}")
    @RegistryKey("Luckycat.Security.PasswordSecret")
    protected String passwordSecret;

    @Value("3")
    @RegistryKey("Luckycat.Verification.SignUpByPhoneCodeLength")
    protected Long signUpByPhoneCodeLength;

    @Value("60")
    @RegistryKey("Luckycat.Verification.SignUpByPhoneCodeBufferTime")
    protected Long SignUpByPhoneCodeBufferTime;

    @Value("300")
    @RegistryKey("Luckycat.Verification.SignUpByPhoneCodeExpiration")
    protected Long signUpByPhoneCodeExpiration;
}
