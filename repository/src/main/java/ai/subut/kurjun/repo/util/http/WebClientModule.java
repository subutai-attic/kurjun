package ai.subut.kurjun.repo.util.http;


import com.google.inject.AbstractModule;
import com.google.inject.multibindings.OptionalBinder;


/**
 * Guice module to initialize default binding for web client factory. Users that want their own implementations shall
 * bind by using {@link OptionalBinder#setBinding()}.
 *
 * @see WebClientFactory
 */
public class WebClientModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        OptionalBinder.newOptionalBinder( binder(), WebClientFactory.class )
                .setDefault().to( DefaultWebClientFactory.class );
    }

}

