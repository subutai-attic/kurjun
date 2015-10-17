package ai.subut.kurjun.common.service;


import java.util.Map;
import java.util.Properties;

import ai.subut.kurjun.common.KurjunContext;


/**
 * Interface for Kurjun properties.
 *
 */
public interface KurjunProperties
{
    /**
     * Gets a property value for supplied key.
     *
     * @param key property key
     * @return value for supplied key if found; null otherwise
     */
    String get( String key );


    /**
     * Gets a property value for supplied key.
     *
     * @param key property key
     * @param defaultValue default value to return when no value found for the key
     * @return value for supplied key if found; default value otherwise
     */
    String getWithDefault( String key, String defaultValue );


    /**
     * Gets an integer property value for supplied key.
     *
     * @param key property key
     * @return value for supplied key if found and valid value exists; null otherwise
     */
    Integer getInteger( String key );


    /**
     * Gets an integer property value for supplied key.
     *
     * @param key property key
     * @param defaultValue default value to return when no value found for the key
     * @return value for supplied key if found and valid value exists; default value otherwise
     */
    Integer getIntegerWithDefault( String key, Integer defaultValue );


    /**
     * Gets a boolean property value for supplied key.
     *
     * @param key property key
     * @return value for supplied key if found and valid value exists; null otherwise
     */
    Boolean getBoolean( String key );


    /**
     * Gets a boolean property value for supplied key.
     *
     * @param key property key
     * @param defaultValue default value to return when no value found for the key
     * @return value for supplied key if found and valid value exists; default value otherwise
     */
    Boolean getBooleanWithDefault( String key, Boolean defaultValue );


    /**
     * Gets a map of all property keys and their values. This is a snapshot of properties available when this method is
     * called. Adding entries into returned map does not reflect to this Kurjun properties.
     *
     * @return map of properties keys and values
     */
    Map<String, Object> propertyMap();


    /**
     * Gets properties for the supplied context. Contextual properties are a set of properties grouped in some context,
     * usage of contextual properties has no specific limits and can be used for any aims.
     * <p>
     * If no properties exist for the context then a new empty properties is created. In subsequent calls of this method
     * with the same context value, that properties will be returned.
     *
     * @param context context
     * @return properties for the context, never {@code null}
     */
    Properties getContextProperties( KurjunContext context );


    /**
     * This is a convenience method to
     * {@link KurjunProperties#getContextProperties(ai.subut.kurjun.common.KurjunContext)} method to get context
     * properties by supplying context name.
     *
     * @param contextName context name
     * @return properties for the context; never {@code null}
     */
    Properties getContextProperties( String contextName );
}

