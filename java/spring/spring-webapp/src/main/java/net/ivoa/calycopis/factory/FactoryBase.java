package net.ivoa.calycopis.factory;

import java.util.UUID;

public interface FactoryBase
    {

    /**
     * Get this factory's identifier.
     *
     */
    public UUID getUuid();

    }