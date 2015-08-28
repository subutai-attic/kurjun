package ai.subut.kurjun.common;


import org.apache.commons.configuration.ConfigurationException;

import com.google.inject.AbstractModule;

import ai.subut.kurjun.common.service.KurjunProperties;


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

}

