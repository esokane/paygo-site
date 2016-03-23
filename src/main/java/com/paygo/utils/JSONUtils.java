package com.paygo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.paygo.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * JSON operations utils
 */
public class JSONUtils {
    private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    public String processObject2Json(Object jsonList) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(jsonList);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse object to json ({})", jsonList, e);
            throw e;
        }
    }


    public <T> T processJSON2Object(InputStream inputStream, Class<T> object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        T result = null;
        try {
            result = mapper.readValue(inputStream, object);
        } catch (IOException e) {
            logger.error("Failed to parse json to object  ({})", object, e);
            throw e;
        }
        return result;
    }

    public List processJSON2List(InputStream inputStream, CollectionType type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List result;
        try {
            result = mapper.readValue(inputStream, type);
        } catch (IOException e) {
            logger.error("Failed to parse json to list ({})", type, e);
            throw e;
        }
        return result;
    }

    public String error2Json(Result result) {
        return "{\"error\": \"" + result.getMsg() + "\"}\n";
    }

    public String error2Json(String msg) {
        return "{\"error\": \"" + msg + "\"}\n";
    }

}
