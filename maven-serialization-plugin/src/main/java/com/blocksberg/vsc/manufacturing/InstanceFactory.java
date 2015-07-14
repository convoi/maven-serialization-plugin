package com.blocksberg.vsc.manufacturing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.RandomDataProviderStrategyImpl;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Factory for fully populated instances.
 */
public class InstanceFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceFactory.class);
    private final PodamFactory podamFactory;
    private final boolean strict;

    public InstanceFactory() {
        this(false);
    }

    /**
     * @param strict if true, an additional check will run to ensure that there are no null fields.
     */
    public InstanceFactory(boolean strict) {
        final DataProviderStrategy dataProviderStrategy = RandomDataProviderStrategyImpl.getInstance(1);
        podamFactory = new PodamFactoryImpl(dataProviderStrategy);

        this.strict = strict;
    }

    /**
     *
     * @param classToSerialize
     * @param <T>
     * @return
     */
    public <T extends Serializable> T createInstance(Class<T> classToSerialize) {
        final T pojoWithFullData = podamFactory.manufacturePojoWithFullData(classToSerialize);
        if (strict) {
            ensureAllFieldsFilled(pojoWithFullData);
        }
        return pojoWithFullData;
    }

    private <T extends Serializable> void ensureAllFieldsFilled(T pojoWithFullData) {
        for (Field field : pojoWithFullData.getClass().getDeclaredFields()) {
            Object o;
            try {
                o = field.get(pojoWithFullData);
            } catch (IllegalAccessException e) {
                try {
                    field.setAccessible(true);
                    o = field.get(pojoWithFullData);
                } catch (IllegalAccessException e2) {
                    o = null;
                    LOGGER.warn("could not make field {} accessible", field.getName());
                }
            }
            if (o == null) {
                throw new IllegalStateException("field " + field.getName() + " was not set or cannot be accessed");
            }

        }
    }
}