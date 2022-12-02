package com.couree.luckycat.app.responder;

import com.couree.luckycat.app.pack.IntegratedRequest;
import com.couree.luckycat.app.pack.Packer;
import com.couree.luckycat.app.pack.PackerFactory;
import com.couree.luckycat.app.responder.annotation.PackedBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author James Chan
 */
@Component
public class HandleValueProcessor {
    private final PackerFactory packerFactory;
    @Value("${Responder.DefaultPacker}")
    private Class<? extends Packer> defaultPackerClass;

    @Autowired
    private HandleValueProcessor(PackerFactory packerFactory) {
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
        final Packer packer = packerFactory.getPacker(packedBy == null ? defaultPackerClass : packedBy.value());

        // pack by packer
        final Object object = packer.pack(integratedRequest);

        try {
            final PrintWriter writer = integratedRequest.getHttpServletResponse().getWriter();
            writer.print(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
