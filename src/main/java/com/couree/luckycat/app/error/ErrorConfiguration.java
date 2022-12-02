package com.couree.luckycat.app.error;

import com.couree.luckycat.glacier.annotation.ApplicationConfiguration;
import com.couree.luckycat.glacier.annotation.Metadata;
import com.couree.luckycat.glacier.annotation.RegistryKey;
import com.couree.luckycat.glacier.constant.RegistryType;
import org.springframework.beans.factory.annotation.Value;

/**
 * Dependency: none.
 */
@ApplicationConfiguration(name = "Error", metadata = {
    @Metadata(key = "RequestExceptionCodeLength", type = RegistryType.NUMBER),
    @Metadata(key = "DefaultSuccessMessage", description = "Default error message."),
    @Metadata(key = "DefaultErrorCode", description = "Default error code."),
    @Metadata(key = "DefaultErrorMessage", description = "Default error message.")
})
public class ErrorConfiguration {
    @Value("3")
    @RegistryKey("Error.RequestExceptionCodeLength")
    protected Long requestExceptionCodeLength;

    @Value("Success.")
    @RegistryKey("Error.DefaultSuccessMessage")
    protected String defaultSuccessMessage;

    @Value("1000")
    @RegistryKey("Error.DefaultErrorCode")
    protected String defaultErrorCode;

    @Value("Unknown Server Internal Error. Please contact maintainers.")
    @RegistryKey("Error.DefaultErrorMessage")
    protected String defaultErrorMessage;
}
