package couree.com.luckycat.core.data.hibernate;

import couree.com.luckycat.core.Config;
import couree.com.luckycat.core.annotation.Initializer;
import couree.com.luckycat.core.base.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

@Component
public class Hibernate {
    /**
     * Log4j logger.
     */
    private static final Logger logger = LogManager.getLogger(Hibernate.class);

    /**
     * Hibernate configuration.
     */
    private Configuration configuration;

    /**
     * Hibernate session factory.
     */
    private SessionFactory sessionFactory;

    @Initializer
    private void init() {
        // initialize Hibernate configuration
        final Config config = Config.instance();
        final HibernateRegistry hibernateRegistry = Config.getApplicationContext().getBean(HibernateRegistry.class);
        final Properties hibernateConfig = new Properties();

        hibernateConfig.put(Environment.URL, hibernateRegistry.url);
        hibernateConfig.put(Environment.USER, hibernateRegistry.username);
        hibernateConfig.put(Environment.PASS, hibernateRegistry.password);
        hibernateConfig.put(Environment.DRIVER, hibernateRegistry.driver);
        hibernateConfig.put(Environment.DIALECT, hibernateRegistry.dialect);
        hibernateConfig.put(Environment.CONNECTION_PROVIDER, hibernateRegistry.connectionProvider);
        hibernateConfig.put(Environment.C3P0_ACQUIRE_INCREMENT, hibernateRegistry.c3p0AcquireIncrement);
        hibernateConfig.put(Environment.C3P0_IDLE_TEST_PERIOD, hibernateRegistry.c3p0IdleTestPeriod);
        hibernateConfig.put(Environment.C3P0_TIMEOUT, hibernateRegistry.c3p0timeout);
        hibernateConfig.put(Environment.C3P0_MAX_SIZE, hibernateRegistry.c3p0MaxSize);
        hibernateConfig.put(Environment.C3P0_MIN_SIZE, hibernateRegistry.c3p0MinSize);
        hibernateConfig.put(Environment.C3P0_MAX_STATEMENTS, hibernateRegistry.c3p0MaxStatements);
        hibernateConfig.put(Environment.POOL_SIZE, hibernateRegistry.poolSize);
        hibernateConfig.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, hibernateRegistry.currentSessionContextClass);
        hibernateConfig.put(Environment.SHOW_SQL, hibernateRegistry.showSql);
        hibernateConfig.put(Environment.FORMAT_SQL, hibernateRegistry.formatSql);

        configuration = new Configuration().setProperties(hibernateConfig);

        // register models
        final Map<String, Model> modelMap = Config.getApplicationContext().getBeansOfType(Model.class);
        System.out.println("modelmap entryset size:" + modelMap.entrySet().size());
        modelMap.forEach((name, modelInstance) -> {
            configuration.addAnnotatedClass(modelInstance.getClass());
            logger.info(String.format("Model registered: [%s].", name));
        });

        sessionFactory = configuration.buildSessionFactory();
    }

    /**
     * Returns session factory.
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Executes a session immediately.
     * @param session session to execute
     */
    public void execute(Session session) {
        session.beginTransaction().commit();
    }

    /**
     * Executes a closure.
     */
    public <T> T execute(Function<Session, T> closure) {
        try (final Session session = sessionFactory.getCurrentSession()) {
            T value = closure.apply(session);
            execute(session);
            return value;
        }
    }

    /**
     * Fetches values.
     */
    public <T> T fetch(Function<Session, T> closure) {
        try (final Session session = sessionFactory.openSession()) {
            return closure.apply(session);
        }
    }

    /**
     * Inserts a model to the database.
     * @param model model to insert.
     * @return model
     */
    public <T extends Model> T insert(T model) {
        model.setId(null);
        return update(model);
    }

    /**
     * Updates a model.
     * @param model model to update
     * @return model
     */
    public <T extends Model> T update(T model) {
        execute(session -> session.save(model));
        return model;
    }
}
