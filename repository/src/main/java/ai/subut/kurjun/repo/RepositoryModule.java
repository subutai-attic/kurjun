package ai.subut.kurjun.repo;


import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.model.repository.PackageType;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.util.AptIndiceBuilderModule;


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
                .implement( LocalRepository.class, Names.named( PackageType.SUBUTAI ), LocalTemplateRepository.class )
                // non-local repositries
                .implement( NonLocalRepository.class, Names.named( PackageType.DEB ), NonLocalAptRepository.class )
                .implement( NonLocalRepository.class, Names.named( PackageType.SNAP ), NonLocalSnapRepository.class )
                .implement( NonLocalRepository.class, Names.named( PackageType.SUBUTAI ), NonLocalTemplateRepository.class )
                // unified repositories
                .implement( UnifiedRepository.class, UnifiedRepositoryImpl.class )
                // finally build the module
                .build( RepositoryFactory.class );

        install( module );
        install( new AptIndiceBuilderModule() );
    }

}

