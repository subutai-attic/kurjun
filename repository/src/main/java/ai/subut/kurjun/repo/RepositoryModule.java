package ai.subut.kurjun.repo;


import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.repo.util.AptIndiceBuilderModule;


/**
 * Guice module to initialize repository type bindings.
 *
 */
public class RepositoryModule extends AbstractModule
{

    public static final String LOCAL_NONVIRTUAL = "local.nonvirtual";
    public static final String LOCAL_KURJUN = "local.kurjun";


    @Override
    protected void configure()
    {
        Module module = new FactoryModuleBuilder()
                .implement( LocalRepository.class, Names.named( LOCAL_NONVIRTUAL ), LocalAptRepositoryImpl.class )
                .implement( LocalRepository.class, Names.named( LOCAL_KURJUN ), KurjunLocalRepository.class )
                .build( RepositoryFactory.class );

        install( module );
        install( new AptIndiceBuilderModule() );
    }

}

