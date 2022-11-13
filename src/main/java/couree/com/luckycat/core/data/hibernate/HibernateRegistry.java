package couree.com.luckycat.core.data.hibernate;

import couree.com.luckycat.core.annotation.Registry;
import couree.com.luckycat.core.annotation.RegistryEntry;
import org.springframework.beans.factory.annotation.Value;

@Registry
public class HibernateRegistry {
    @Value("${hibernate.url}")
    @RegistryEntry(key = "Hibernate.Url")
    public String url;

    @Value("${hibernate.username}")
    @RegistryEntry(key = "Hibernate.Username")
    public String username;

    @Value("${hibernate.password}")
    @RegistryEntry(key = "Hibernate.Password")
    public String password;

    @RegistryEntry(key = "Hibernate.Driver")
    public final String driver = "com.mysql.cj.jdbc.Driver";

    @RegistryEntry(key = "Hibernate.Dialect")
    public final String dialect = "org.hibernate.dialect.MySQL8Dialect";

    @RegistryEntry(key = "Hibernate.ConnectionProvider")
    public final String connectionProvider = "org.hibernate.connection.C3P0ConnectionProvider";

    @RegistryEntry(key = "Hibernate.C3p0AcquireIncrement")
    public final Integer c3p0AcquireIncrement = 10;

    @RegistryEntry(key = "Hibernate.C3p0IdleTestPeriod")
    public final Integer c3p0IdleTestPeriod = 10000;

    @RegistryEntry(key = "Hibernate.C3p0Timeout")
    public final Integer c3p0timeout = 5000;

    @RegistryEntry(key = "Hibernate.C3p0MaxSize")
    public final Integer c3p0MaxSize = 30;

    @RegistryEntry(key = "Hibernate.C3p0MinSize")
    public final Integer c3p0MinSize = 5;

    @RegistryEntry(key = "Hibernate.C3p0MaxStatements")
    public final Integer c3p0MaxStatements = 10;

    @RegistryEntry(key = "Hibernate.PoolSize")
    public final Integer poolSize = 1;

    @RegistryEntry(key = "Hibernate.CurrentSessionContextClass")
    public final String currentSessionContextClass = "thread";

    @RegistryEntry(key = "Hibernate.ShowSql")
    public final Boolean showSql = true;

    @RegistryEntry(key = "Hibernate.FormatSql")
    public final Boolean formatSql = true;
}
