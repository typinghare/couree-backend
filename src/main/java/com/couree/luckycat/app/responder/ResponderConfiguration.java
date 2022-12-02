package com.couree.luckycat.app.responder;

import com.couree.luckycat.glacier.annotation.ApplicationConfiguration;
import com.couree.luckycat.glacier.annotation.Metadata;
import com.couree.luckycat.glacier.annotation.RegistryKey;
import com.couree.luckycat.glacier.constant.RegistryType;
import org.springframework.beans.factory.annotation.Value;

@ApplicationConfiguration(
    name = "Responder",
    dependency = {"Pack", "Error"},
    metadata = {
        @Metadata(key = "DefaultPacker", type = RegistryType.CLASS, description = "Default packer class.")
    }
)
public class ResponderConfiguration {
    @Value("com.couree.luckycat.app.responder.JSendPacker")
    @RegistryKey("Responder.DefaultPacker")
    protected Class<?> defaultPacker;
}
