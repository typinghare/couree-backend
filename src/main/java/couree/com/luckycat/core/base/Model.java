package couree.com.luckycat.core.base;

import java.io.Serializable;

/**
 * General abstract model.
 * @author James Chan
 */
public abstract class Model implements Serializable {
    public abstract Long getId();

    public abstract void setId(Long id);
}
