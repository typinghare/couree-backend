package couree.com.luckycat.core.data.redis;

import couree.com.luckycat.core.annotation.Registry;
import couree.com.luckycat.core.annotation.RegistryEntry;

@Registry
@RegistryEntry(key = "Redis.Hostname", value = "${redis.hostname}")
@RegistryEntry(key = "Redis.Port", value = "${redis.port}")
public class RedisRegistry {
}
