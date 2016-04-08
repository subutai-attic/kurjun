package ai.subut.kurjun.web.conf;


import java.util.Properties;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import ai.subut.kurjun.cfparser.ControlFileParserModule;
import ai.subut.kurjun.common.KurjunPropertiesModule;
import ai.subut.kurjun.core.dao.KurjunDAOModule;
import ai.subut.kurjun.core.dao.service.identity.IdentityDataService;
import ai.subut.kurjun.core.dao.service.identity.IdentityDataServiceImpl;
import ai.subut.kurjun.core.dao.service.identity.RelationDataService;
import ai.subut.kurjun.core.dao.service.identity.RelationDataServiceImpl;
import ai.subut.kurjun.identity.KurjunIdentityModule;
import ai.subut.kurjun.index.PackagesIndexParserModule;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreModule;
import ai.subut.kurjun.repo.RepositoryManagerImpl;
import ai.subut.kurjun.repo.RepositoryModule;
import ai.subut.kurjun.repo.service.RepositoryManager;
import ai.subut.kurjun.riparser.ReleaseIndexParserModule;
import ai.subut.kurjun.snap.SnapMetadataParserModule;
import ai.subut.kurjun.storage.factory.FileStoreModule;
import ai.subut.kurjun.subutai.SubutaiTemplateParserModule;
import ai.subut.kurjun.web.context.ArtifactContext;
import ai.subut.kurjun.web.context.GlobalArtifactContext;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.init.KurjunInitializer;
import ai.subut.kurjun.web.service.AptManagerService;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RawManagerService;
import ai.subut.kurjun.web.service.RelationManagerService;
import ai.subut.kurjun.web.service.RepositoryService;
import ai.subut.kurjun.web.service.TemplateManagerService;
import ai.subut.kurjun.web.service.UserRepoContextStore;
import ai.subut.kurjun.web.service.impl.AptManagerServiceImpl;
import ai.subut.kurjun.web.service.impl.IdentityManagerServiceImpl;
import ai.subut.kurjun.web.service.impl.RawManagerServiceImpl;
import ai.subut.kurjun.web.service.impl.RelationManagerServiceImpl;
import ai.subut.kurjun.web.service.impl.RepositoryServiceImpl;
import ai.subut.kurjun.web.service.impl.TemplateManagerServiceImpl;
import ai.subut.kurjun.web.service.impl.UserRepoContextStoreImpl;
import ninja.uploads.FileItemProvider;
import ninja.utils.NinjaProperties;


public class Module extends AbstractModule
{
    @Override
    protected void configure()
    {

        install( new KurjunPropertiesModule() );
        install( new KurjunDAOModule() );
        install( new KurjunIdentityModule() );
        install( new ControlFileParserModule() );
        install( new ReleaseIndexParserModule() );
        install( new PackagesIndexParserModule() );
        install( new SubutaiTemplateParserModule() );

        install( new FileStoreModule() );
        install( new PackageMetadataStoreModule() );
        install( new SnapMetadataParserModule() );

        this.bind(RepositoryManager.class ).to(RepositoryManagerImpl.class );

        install( new RepositoryModule() );

        bind( IdentityDataService.class ).to( IdentityDataServiceImpl.class );

        bind( RelationDataService.class ).to( RelationDataServiceImpl.class );


        bind( ArtifactContext.class ).to( GlobalArtifactContext.class );

        bind( UserRepoContextStore.class ).to( UserRepoContextStoreImpl.class );

        bind( KurjunInitializer.class );

        bind( TemplateManagerService.class ).to( TemplateManagerServiceImpl.class );

        bind( RawManagerService.class ).to( RawManagerServiceImpl.class );

        bind( AptManagerService.class ).to( AptManagerServiceImpl.class );

        bind( FileItemProvider.class ).to( SubutaiFileHandler.class );

        bind( RepositoryService.class ).to( RepositoryServiceImpl.class );

        bind( IdentityManagerService.class ).to( IdentityManagerServiceImpl.class );

        bind( RelationManagerService.class ).to( RelationManagerServiceImpl.class );

    }


    /**
     * The application config goes as properties into the service module.
     */
    @Provides
    @Singleton
    public Properties provideProperties( NinjaProperties ninjaProperties )
    {
        Properties props = new Properties( ninjaProperties.getAllCurrentNinjaProperties() );

        if ( ninjaProperties.isProd() )
        {
            props.setProperty( "prod", "true" );
        }

        return props;
    }
}
