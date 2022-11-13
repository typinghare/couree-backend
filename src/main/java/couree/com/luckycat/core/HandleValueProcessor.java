package couree.com.luckycat.core;

import couree.com.luckycat.core.annotation.PackedBy;
import couree.com.luckycat.core.pack.Packer;
import couree.com.luckycat.core.pack.PackerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author James Chan
 * @see CoreRegistry
 */
@Component
public class HandleValueProcessor {
    private final Config config;

    private final PackerFactory packerFactory;

    @Autowired
    private HandleValueProcessor(Config config, PackerFactory packerFactory) {
        this.config = config;
        this.packerFactory = packerFactory;
    }

    /**
     * Process an integrated request.
     * @param integratedRequest integrated request
     */
    public void process(IntegratedRequest integratedRequest) {
        // retrieve packer
        final HandlerMethod handlerMethod = integratedRequest.getHandlerMethod();
        final PackedBy packedBy = handlerMethod.getMethodAnnotation(PackedBy.class);
        final Packer packer = packerFactory.getPacker(packedBy == null ? getDefaultPacker() : packedBy.value());

        // pack by packer
        final Object object = packer.pack(integratedRequest);

        try {
            final PrintWriter writer = integratedRequest.getHttpServletResponse().getWriter();
            writer.print(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the default packer. Default packer is recorded in registry.
     * @return default packer
     */
    private Class<? extends Packer> getDefaultPacker() {
        final String defaultPackerClassName = config.getRegistryValue("Packer.DefaultClass");

        try {
            @SuppressWarnings("unchecked") final Class<? extends Packer> defaultPackerClass =
                    (Class<? extends Packer>) Class.forName(defaultPackerClassName);
            return defaultPackerClass;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
