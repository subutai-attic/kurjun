package ai.subut.kurjun.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.google.inject.Binder;
import com.google.inject.name.Names;

import ai.subut.kurjun.common.service.KurjunProperties;


/**
 * Implementation of {@link KurjunProperties} that reads properties from file specified by
 * {@link KurjunPropertiesImpl#CONF_FILE_PATH}.
 *
 */
class KurjunPropertiesImpl implements KurjunProperties
{

    public static final String CONF_FILE_PATH = "kurjun.properties";

    private static final Logger LOGGER = LoggerFactory.getLogger( KurjunPropertiesImpl.class );

    private PropertiesConfiguration conf;


    public KurjunPropertiesImpl() throws ConfigurationException
    {
        conf = new PropertiesConfiguration( CONF_FILE_PATH );
        conf.setThrowExceptionOnMissing( false );
    }


    @Override
    public String get( String key )
    {
        return conf.getString( key );
    }


    @Override
    public String getWithDefault( String key, String defaultValue )
    {
        return conf.getString( key, defaultValue );
    }


    @Override
    public Integer getInteger( String key )
    {
        try
        {
            return conf.getInteger( key, null );
        }
        catch ( ConversionException ex )
        {
            LOGGER.debug( "Invalid value", ex );
            return null;
        }
    }


    @Override
    public Integer getIntegerWithDefault( String key, Integer defaultValue )
    {
        try
        {
            return conf.getInteger( key, defaultValue );
        }
        catch ( ConversionException ex )
        {
            LOGGER.debug( "Invalid value", ex );
            return defaultValue;
        }
    }


    @Override
    public Boolean getBoolean( String key )
    {
        try
        {
            return conf.getBoolean( key, null );
        }
        catch ( ConversionException ex )
        {
            LOGGER.debug( "Invalid value", ex );
            return null;
        }
    }


    @Override
    public Boolean getBooleanWithDefault( String key, Boolean defaultValue )
    {
        try
        {
            return conf.getBoolean( key, defaultValue );
        }
        catch ( ConversionException ex )
        {
            LOGGER.debug( "Invalid value", ex );
            return defaultValue;
        }
    }


    /**
     * Creates a constant binding to @Named(key) for each property.
     *
     * @param binder Guice binder instance
     */
    void bindProperties( Binder binder )
    {
        Names.bindProperties( binder, ConfigurationConverter.getProperties( conf ) );
    }
}

