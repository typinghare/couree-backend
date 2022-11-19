package couree.com.luckycat.core;

import couree.com.luckycat.core.annotation.Registry;
import couree.com.luckycat.core.annotation.RegistryEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;

/**
 * Default registry.
 * @author James Chan
 * @see Config
 */
@Registry(Ordered.HIGHEST_PRECEDENCE)
@RegistryEntry(key = "RequestException.RequestExceptionCode", value = "3")
@RegistryEntry(key = "Packer.DefaultClass", value = "couree.com.luckycat.core.pack.class")
public class CoreRegistry {
    @Value("${system.environment}")
    @RegistryEntry(key = "System.Environment")
    public String systemEnvironment;

    @Value("${encrypt.secret.user-password}")
    @RegistryEntry(key = "Encrypt.Secret.UserPassword")
    public String encryptSecretUserPassword;
}
