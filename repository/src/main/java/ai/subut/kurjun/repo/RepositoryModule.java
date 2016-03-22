package ai.subut.kurjun.repo;


import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.RemoteRepository;
import ai.subut.kurjun.model.repository.PackageType;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.util.AptIndiceBuilderModule;
import ai.subut.kurjun.repo.util.http.WebClientModule;


/**
 * Guice module to initialize repository type bindings.
 *
 */
public class RepositoryModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        Module module = new FactoryModuleBuilder()
                .implement( LocalRepository.class, Names.named( "APT_WRAPPER" ), LocalAptRepositoryWrapper.class )
                .implement( LocalRepository.class, Names.named( PackageType.DEB ), LocalAptRepository.class )
                .implement( LocalRepository.class, Names.named( PackageType.SNAP ), LocalSnapRepository.class )
                .implement( LocalRepository.class, Names.named( PackageType.RAW ), LocalRawRepository.class )
                .implement( LocalRepository.class, Names.named( PackageType.SUBUTAI ), LocalTemplateRepository.class )
                // non-local repositries
                .implement( RemoteRepository.class, Names.named( PackageType.DEB ), RemoteAptRepository.class )
                .implement( RemoteRepository.class, Names.named( PackageType.SNAP ), RemoteSnapRepository.class )
                .implement( RemoteRepository.class, Names.named( PackageType.RAW ), RemoteRawRepository.class )
                .implement( RemoteRepository.class, Names.named( PackageType.SUBUTAI ), RemoteTemplateRepository.class )
                // unified repositories
                .implement( UnifiedRepository.class, UnifiedRepositoryImpl.class )
                // finally build the module
                .build( RepositoryFactory.class );

        install( module );
        install( new AptIndiceBuilderModule() );
        install( new WebClientModule() );
    }

}

