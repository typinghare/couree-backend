package com.couree.luckycat.app.hibernate;

import com.couree.luckycat.glacier.annotation.ApplicationConfiguration;
import com.couree.luckycat.glacier.annotation.Metadata;
import com.couree.luckycat.glacier.annotation.RegistryKey;
import com.couree.luckycat.glacier.constant.RegistryType;
import org.springframework.beans.factory.annotation.Value;

@ApplicationConfiguration(name = "Hibernate", metadata = {
    @Metadata(key = "Url"),
    @Metadata(key = "Username"),
    @Metadata(key = "Password"),
    @Metadata(key = "Driver"),
    @Metadata(key = "Dialect"),
    @Metadata(key = "ConnectionProvider"),
    @Metadata(key = "C3p0AcquireIncrement", type = RegistryType.NUMBER),
    @Metadata(key = "C3p0IdleTestPeriod", type = RegistryType.NUMBER),
    @Metadata(key = "C3p0Timeout", type = RegistryType.NUMBER),
    @Metadata(key = "C3p0MaxSize", type = RegistryType.NUMBER),
    @Metadata(key = "C3p0MinSize", type = RegistryType.NUMBER),
    @Metadata(key = "C3p0MaxStatements", type = RegistryType.NUMBER),
    @Metadata(key = "PoolSize", type = RegistryType.NUMBER),
    @Metadata(key = "CurrentSessionContextClass"),
    @Metadata(key = "ShowSql", type = RegistryType.BOOLEAN),
    @Metadata(key = "FormatSql", type = RegistryType.BOOLEAN),
})
public class HibernateConfiguration {
    @Value("${hibernate.url}")
    @RegistryKey("Hibernate.Url")
    protected String url;

    @Value("${hibernate.username}")
    @RegistryKey("Hibernate.Username")
    protected String username;

    @Value("${hibernate.password}")
    @RegistryKey("Hibernate.Password")
    protected String password;

    @RegistryKey("Hibernate.Driver")
    protected String driver = "com.mysql.cj.jdbc.Driver";

    @Value("org.hibernate.dialect.MySQL8Dialect")
    @RegistryKey("Hibernate.Dialect")
    protected String dialect;

    @Value("org.hibernate.connection.C3P0ConnectionProvider")
    @RegistryKey("Hibernate.ConnectionProvider")
    protected String connectionProvider;

    @Value("10")
    @RegistryKey("Hibernate.C3p0AcquireIncrement")
    protected Integer c3p0AcquireIncrement;

    @Value("10000")
    @RegistryKey("Hibernate.C3p0IdleTestPeriod")
    protected Integer c3p0IdleTestPeriod;

    @Value("5000")
    @RegistryKey("Hibernate.C3p0Timeout")
    protected Integer c3p0timeout;

    @Value("30")
    @RegistryKey("Hibernate.C3p0MaxSize")
    protected Integer c3p0MaxSize;

    @Value("5")
    @RegistryKey("Hibernate.C3p0MinSize")
    protected Integer c3p0MinSize;

    @Value("10")
    @RegistryKey("Hibernate.C3p0MaxStatements")
    protected Integer c3p0MaxStatements;

    @Value("1")
    @RegistryKey("Hibernate.PoolSize")
    protected Integer poolSize;

    @Value("thread")
    @RegistryKey("Hibernate.CurrentSessionContextClass")
    protected String currentSessionContextClass;

    @Value("false")
    @RegistryKey("Hibernate.ShowSql")
    protected Boolean showSql;

    @Value("false")
    @RegistryKey("Hibernate.FormatSql")
    protected Boolean formatSql;
}
