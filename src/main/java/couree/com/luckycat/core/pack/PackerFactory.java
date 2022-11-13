package couree.com.luckycat.core.pack;

import com.sun.istack.NotNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

/**
 * Packer Factory.
 * @author James Chan
 */
@Component
public class PackerFactory {
    /**
     * Returns a packer instance.
     * @param packerClass a packer class (template)
     * @return a packer instance
     */
    @NotNull
    public Packer getPacker(@NotNull Class<? extends Packer> packerClass) {
        try {
            return packerClass.getConstructor().newInstance();
        } catch (InvocationTargetException | InstantiationException e) {
            System.out.printf("Fail to create packer: [%s].%n", packerClass.getName());
            throw new RuntimeException(e);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            System.out.printf("Packer class [%s] has no available constructor method.%n", packerClass.getName());
            throw new RuntimeException(e);
        }
    }
}
