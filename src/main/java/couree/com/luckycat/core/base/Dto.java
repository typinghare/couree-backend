package couree.com.luckycat.core.base;

import couree.com.luckycat.core.common.JBeans;

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
