package com.couree.luckycat.glacier.app.responder;

import com.couree.luckycat.glacier.common.JBeans;

import java.io.Serializable;

/**
 * Base DTO (Data Transfer Object).
 * @author James Chan.
 */
public abstract class Dto implements Serializable {
    @Override
    public String toString() {
        return JBeans.convertToString(this);
    }
}
