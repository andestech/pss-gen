
/**
 * An interface for collections.
 */
public interface PSSICollection {

    /**
     * Returns {@code true} if this collection is indexable.
     *
     * @return {@code true} if this collection is indexable
     */
    public boolean isIndexable();

    /**
     * Returns an iterator for this collection.
     *
     * @return an iterator for this collection
     */
    public PSSIIterator iterator();

}
