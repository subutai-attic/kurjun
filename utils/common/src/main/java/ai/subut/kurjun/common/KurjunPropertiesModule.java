package ai.subut.kurjun.common;


import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;

import com.google.inject.AbstractModule;

import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.common.utils.PropertyUtils;


/**
 * Guice module to initialize Kurjun property bindings.
 *
 */
public class KurjunPropertiesModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        KurjunPropertiesImpl properties;
        try
        {
            properties = new KurjunPropertiesImpl();
        }
        catch ( ConfigurationException ex )
        {
            throw new IllegalStateException( "Failed to initialize properties", ex );
        }

        // bind properties in Guice injector
        properties.bindProperties( binder() );

        bind( KurjunProperties.class ).toInstance( properties );
    }


    /**
     * Creates an instance of {@link KurjunProperties} that holds properties from supplied properties instance. Using
     * {@link PropertyUtils#makeKurjunProperties(java.util.Properties)} method is preferred to this method.
     *
     * @param properties properties to include in Kurjun properties instance
     * @return Kurjun properties instance
     */
    public static KurjunProperties create( Properties properties )
    {
        return new KurjunPropertiesImpl( properties );
    }

}

