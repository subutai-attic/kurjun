package ai.subut.kurjun.repo.util;


import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import ai.subut.kurjun.repo.service.PackagesIndexBuilder;


/**
 * Guice module to initialize bindings for apt indice builders like release index builder and package index file
 * builder.
 *
 */
public class AptIndiceBuilderModule extends AbstractModule
{


    @Override
    protected void configure()
    {
        install( new FactoryModuleBuilder()
                .implement( PackagesIndexBuilder.class, PackagesIndexBuilderImpl.class )
                .implement( ReleaseIndexBuilder.class, ReleaseIndexBuilder.class )
                .build( AptIndexBuilderFactory.class ) );
    }

}

