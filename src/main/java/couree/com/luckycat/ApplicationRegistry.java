package couree.com.luckycat;

import couree.com.luckycat.core.annotation.Registry;
import couree.com.luckycat.core.annotation.RegistryEntry;
import org.springframework.beans.factory.annotation.Value;

@Registry
@RegistryEntry(key = "Packer.DefaultClass", value = "couree.com.luckycat.packer.JSendPacker")
@RegistryEntry(key = "User.Verification.SignUpByPhoneCodeLength", value = "6")
@RegistryEntry(key = "User.Verification.SignUpByPhoneCodeBufferTime", value = "60")
@RegistryEntry(key = "User.Verification.SignUpByPhoneCodeExpiration", value = "300")
public class ApplicationRegistry {
    @Value("${system.password-secret}")
    @RegistryEntry(key = "System.PasswordSecret")
    public String passwordSecret;
}
