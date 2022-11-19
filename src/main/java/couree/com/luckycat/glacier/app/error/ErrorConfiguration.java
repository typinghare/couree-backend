package couree.com.luckycat.glacier.app.error;

import couree.com.luckycat.glacier.annotation.ApplicationConfiguration;
import couree.com.luckycat.glacier.annotation.Metadata;
import couree.com.luckycat.glacier.constant.RegistryType;

@ApplicationConfiguration(name = "Error", metadata = {
        @Metadata(key = "RequestExceptionCodeLength", type = RegistryType.NUMBER),
        @Metadata(key = "DefaultErrorCode", description = "Default error code."),
        @Metadata(key = "DefaultMessage", description = "Default error message.")
})
public class ErrorConfiguration {
}
