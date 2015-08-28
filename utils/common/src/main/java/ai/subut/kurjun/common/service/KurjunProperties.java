package ai.subut.kurjun.common.service;


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

}

