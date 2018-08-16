package com.noori.olivot.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Factory class that can be used to create new JSON object mapper instances
 */
public final class ObjectMapperFactory {
    /**
     * Initializes a new instance of {@link ObjectMapperFactory}
     */
    private ObjectMapperFactory() {

    }

    /**
     * Creates a new object mapper
     * @return  An instance of {@link ObjectMapper}
     */
    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }
}
