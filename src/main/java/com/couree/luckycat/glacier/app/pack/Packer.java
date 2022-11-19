package com.couree.luckycat.glacier.app.pack;

import org.springframework.stereotype.Component;

/**
 * Base Packer. The mission of a packer is to render the http servlet response to generate an appropriate HTTP
 * response.
 * @author James Chan
 */
@Component
public class Packer {
    /**
     * To pack an object. The packed object will be written to the HTTP body directly.
     * @param request request
     */
    public Object pack(IntegratedRequest request) {
        return request.getException() == null ?
            request.getHandleValue().toString() :
            request.getException().getMessage();
    }
}
