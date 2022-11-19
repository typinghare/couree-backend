package com.couree.luckycat.service;

import com.couree.luckycat.app.hibernate.Hibernate;
import com.couree.luckycat.app.phone.PhoneNumber;
import com.couree.luckycat.app.phone.PhoneService;
import com.couree.luckycat.app.redis.Redis;
import com.couree.luckycat.constant.RedisKeyType;
import com.couree.luckycat.dto.UserDto;
import com.couree.luckycat.dto.UserSignUpDto;
import com.couree.luckycat.exception.business.UserServiceException;
import com.couree.luckycat.glacier.app.security.Encoder;
import com.couree.luckycat.glacier.common.JBeans;
import com.couree.luckycat.glacier.common.Throws;
import com.couree.luckycat.glacier.common.Verifications;
import com.couree.luckycat.model.User;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class UserService {
    private final Hibernate hibernate;
    private final RedisTemplate<String, String> redisTemplate;
    private final PhoneService phoneService;
    private final Encoder encoder;
    @Value("${Luckycat.Verification.SignUpByPhoneCodeBufferTime}")
    private int signUpByPhoneCodeLength;
    @Value("${Luckycat.Verification.SignUpByPhoneCodeBufferTime}")
    private int signUpByPhoneCodeBufferTime;
    @Value("${Luckycat.Verification.SignUpByPhoneCodeExpiration}")
    private int signUpByPhoneCodeExpiration;
    @Value("${Luckycat.Security.PasswordSecret}")
    private String passwordSecret;


    @Autowired
    private UserService(
        Hibernate hibernate,
        RedisTemplate<String, String> redisTemplate,
        PhoneService phoneService,
        Encoder encoder
    ) {
        this.hibernate = hibernate;
        this.redisTemplate = redisTemplate;
        this.phoneService = phoneService;
        this.encoder = encoder;
    }

    /**
     * Get a user by a specified user id.
     * @return user model
     */
    public User getUser(Long userId) {
        return hibernate.fetch(session -> session.get(User.class, userId));
    }

    /**
     * Gets a user model by user's phone number.
     * @param phoneNumberString user's phone number
     * @return user model; null if user does not exist
     */
    public User getUserByPhone(String phoneNumberString) {
        return hibernate.fetch(session -> session
            .createQuery("from User where phoneNumber = :phoneNumber", User.class)
            .setParameter("phoneNumber", phoneNumberString)
            .getSingleResult()
        );
    }

    /**
     * Sends a verification code to user's phone.
     * @param phoneNumberString user's phone number
     * @param redisKeyType      redis key to distinguish different uses
     */
    public void sendCodeToPhone(String phoneNumberString, RedisKeyType redisKeyType) {
        // check if the verification code has been sent within a buffer time
        // throw exception if the key is found
        final Redis.Key createdKey = Redis.key(redisKeyType, phoneNumberString);
        final Boolean createdKeyExists = redisTemplate.hasKey(createdKey.toString());
        Throws.ifTrue(createdKeyExists, UserServiceException.SEND_VERIFICATION_CODE_FREQUENTLY);

        // generate a verification code and send it to the user's phone
        final PhoneNumber phoneNumber = PhoneNumber.of(phoneNumberString);
        final String verificationCode = Verifications.generateCode(signUpByPhoneCodeLength);
        final boolean isSuccessfullySendCode
            = phoneService.sendVerificationCode(phoneNumber, verificationCode, signUpByPhoneCodeExpiration);
        Throws.ifFalse(isSuccessfullySendCode, UserServiceException.FAIL_TO_SEND_VERIFICATION_CODE);

        // set created-key and store the verification code
        final Redis.Key verificationCodeKey = Redis.key(redisKeyType, phoneNumberString);
        redisTemplate.opsForValue()
            .set(verificationCodeKey.toString(), verificationCode, Duration.ofSeconds(signUpByPhoneCodeExpiration));
        redisTemplate.opsForValue()
            .set(createdKey.toString(), Strings.EMPTY, Duration.ofSeconds(signUpByPhoneCodeBufferTime));
    }

    /**
     * Get a user by specified user id
     * @return user dto
     */
    public UserDto getUserDto(Long userId) {
        final User user = getUser(userId);
        Throws.ifNull(user, UserServiceException.USER_NOT_FOUND);

        return JBeans.convert(user, UserDto.class);
    }

    /**
     * Sends verification code to user's phone. This method is for signing up.
     * @param phoneNumberString phone number string
     */
    public void signUpSendCodeToPhone(String phoneNumberString) {
    }

    /**
     * User signs up by phone number.
     * @return user dto
     */
    public UserDto signUpByPhone(UserSignUpDto userSignUpDto) {
        final String phoneNumberString = userSignUpDto.getPhoneNumber();
        final String verificationCodeKey = Redis.key(RedisKeyType.SIGN_UP_BY_PHONE_VERIFICATION_CODE, phoneNumberString).toString();
        final String verificationCode = redisTemplate.opsForValue().get(verificationCodeKey);

        // if the verification code does not found,
        Throws.ifNull(verificationCode, UserServiceException.VERIFICATION_CODE_HAS_EXPIRED);

        if (!verificationCode.equals(userSignUpDto.getVerificationCode())) {
            throw UserServiceException.INCORRECT_VERIFICATION_CODE;
        }

        // remove verification code from redis
        redisTemplate.delete(verificationCodeKey);

        // insert the user into database
        final User user = new User();
        user.setUsername(generateNewUsername());
        user.setPhoneNumber(phoneNumberString);
        user.setAuthString(encoder.encode(userSignUpDto.getPassword(), passwordSecret));
        user.setEmail(userSignUpDto.getEmail());
        hibernate.insert(user);

        return JBeans.convert(user, UserDto.class);
    }

    /**
     * User signs in with phone number and password.
     * @param phoneNumberString phone number
     * @param password          password
     * @return session id
     */
    public String signInByPhone(String phoneNumberString, String password) {
        final User user = getUserByPhone(phoneNumberString);
        Throws.ifNull(user, UserServiceException.USER_NOT_FOUND);

        if (!encoder.matches(password, user.getAuthString(), passwordSecret)) {
            throw UserServiceException.INCORRECT_PASSWORD;
        }

        return getUserSessionId(user.getId());
    }

    /**
     * Generates and returns a new username.
     * @return a new username
     */
    private String generateNewUsername() {
        final Long maxId = hibernate.fetch(session -> (Long) session
            .createQuery("select max(id) from User")
            .getSingleResult()
        );
        return "couree_" + (maxId + 1);
    }

    /**
     * Returns a session id. The user id will be saved to the Redis.
     * @param userId user id
     * @return a session id
     */
    private String getUserSessionId(Long userId) {
        String userSessionId = null;
        while (true) {
            userSessionId = generateUserSessionId(userId);
            final String key = Redis.key(RedisKeyType.USER_SESSION_KEY, userSessionId).toString();

            if (redisTemplate.opsForValue().get(key) == null) {
                redisTemplate.opsForValue().set(key, userId.toString());
                break;
            }
        }

        return userSessionId;
    }

    /**
     * Returns a user session id generated by UUID.
     * @param userId user id
     * @return a user session id generated by UUID
     */
    private String generateUserSessionId(Long userId) {
        return UUID.randomUUID().toString();
    }
}
