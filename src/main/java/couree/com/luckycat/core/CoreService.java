package couree.com.luckycat.core;

import couree.com.luckycat.core.dto.RegistryEntryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

@Service
public class CoreService {
    private final Logger logger = LoggerFactory.getLogger(CoreService.class);

    private Registry getRegistry() {
        try {
            final Class<Config> configCLass = Config.class;
            final Field registryField = configCLass.getDeclaredField("registry");
            registryField.setAccessible(true);
            return (Registry) registryField.get(Config.instance());
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public RegistryEntryDto getRegistryEntry(String key) {
        final String value = Config.instance().getRegistryValue(key);
        final Registry registry = getRegistry();
        assert registry != null;

        final RegistryEntryDto registryEntryDto = new RegistryEntryDto();
        registryEntryDto.setKey(key);
        registryEntryDto.setValue(value);
        registryEntryDto.setEntryRecordList(registry.getEntryRecordList(key));

        return registryEntryDto;
    }

    /**
     * Returns a set of keys of registry.
     * @return a set of keys of registry
     */
    @SuppressWarnings("unchecked")
    public Set<String> getRegistryKeys() {
        try {
            final Registry registry = getRegistry();
            assert registry != null;

            final Field registryEntryMapField = registry.getClass().getDeclaredField("keyEntryMap");
            registryEntryMapField.setAccessible(true);

            return ((Map<String, Registry.Entry>) registryEntryMapField.get(registry)).keySet();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }
}
