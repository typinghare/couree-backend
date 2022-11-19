package com.couree.luckycat.exception.business;

import com.couree.luckycat.glacier.app.error.annotation.Entry;
import com.couree.luckycat.glacier.app.error.annotation.ExceptionConfiguration;
import com.couree.luckycat.glacier.app.error.stereo.BusinessLogicException;
import org.springframework.http.HttpStatus;

@ExceptionConfiguration(exceptionCode = 10)
public class UserServiceException extends BusinessLogicException {
    @Entry(code = 1, message = "User does not exist.", status = HttpStatus.NOT_FOUND)
    public static UserServiceException USER_NOT_FOUND;

    @Entry(code = 2, message = "You cannot send verification code frequently.", status = HttpStatus.FORBIDDEN)
    public static UserServiceException SEND_VERIFICATION_CODE_FREQUENTLY;

    @Entry(code = 3, message = "Fail to send verification code to user's phone.", status = HttpStatus.FORBIDDEN)
    public static UserServiceException FAIL_TO_SEND_VERIFICATION_CODE;

    @Entry(code = 4, message = "The verification code has expired.", status = HttpStatus.FORBIDDEN)
    public static UserServiceException VERIFICATION_CODE_HAS_EXPIRED;

    @Entry(code = 5, message = "The verification code is incorrect.", status = HttpStatus.FORBIDDEN)
    public static UserServiceException INCORRECT_VERIFICATION_CODE;

    @Entry(code = 6, message = "The password is incorrect.", status = HttpStatus.FORBIDDEN)
    public static UserServiceException INCORRECT_PASSWORD;
}
