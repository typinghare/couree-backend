package com.couree.luckycat.glacier;

import com.couree.luckycat.glacier.annotation.ApplicationConfiguration;
import com.couree.luckycat.glacier.annotation.Metadata;
import com.couree.luckycat.glacier.annotation.RegistryKey;
import com.couree.luckycat.glacier.constant.EnvironmentType;
import com.couree.luckycat.glacier.constant.RegistryType;
import org.springframework.beans.factory.annotation.Value;

@ApplicationConfiguration(name = Application.FRAMEWORK_NAME, metadata = {
    @Metadata(key = "EnvironmentType", type = RegistryType.ENUM)
})
public class Configuration {
    @Value("${glacier.environment}")
    @RegistryKey(Application.FRAMEWORK_NAME + ".EnvironmentType")
    public EnvironmentType environmentType;

    private Configuration() {
    }
}
