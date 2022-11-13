package couree.com.luckycat.core;

import couree.com.luckycat.core.dto.RegistryEntryDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
public class CoreServiceTest {
    @Autowired
    private CoreService coreService;

    @Test
    public void getKeys() {
        final Set<String> keys = coreService.getRegistryKeys();
        for (final String key : keys) {
            System.out.println(key);
        }
    }

    @Test
    public void getValue() {
        final RegistryEntryDto registryEntryDto1 = coreService.getRegistryEntry("System.Environment");
        final RegistryEntryDto registryEntryDto2 = coreService.getRegistryEntry("Packer.DefaultClass");

        System.out.println(registryEntryDto1);
        System.out.println(registryEntryDto2);
    }
}
