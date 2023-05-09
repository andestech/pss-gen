
import java.util.Map;

/**
 * This is an interface for iterators.
 */
public interface PSSIIterator {

    /**
     * Returns {@code true} if there is next element.
     *
     * @return {@code true} if there is next element
     */
    public boolean hasNext();

    /**
     * Returns the next element
     *
     * @return the next element
     */
    public Map.Entry<PSSVal, PSSVal> next();

}
