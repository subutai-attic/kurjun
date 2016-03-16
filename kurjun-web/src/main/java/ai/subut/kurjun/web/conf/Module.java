package ai.subut.kurjun.web.conf;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.cfparser.ControlFileParserModule;
import ai.subut.kurjun.common.KurjunPropertiesModule;
import ai.subut.kurjun.index.PackagesIndexParserModule;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreModule;
import ai.subut.kurjun.repo.RepositoryModule;
import ai.subut.kurjun.riparser.ReleaseIndexParserModule;
import ai.subut.kurjun.snap.SnapMetadataParserModule;
import ai.subut.kurjun.storage.factory.FileStoreModule;
import ai.subut.kurjun.subutai.SubutaiTemplateParserModule;
import ai.subut.kurjun.web.context.ArtifactContext;
import ai.subut.kurjun.web.context.GlobalArtifactContext;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.init.KurjunInitializer;
import ai.subut.kurjun.web.service.RawManagerService;
import ai.subut.kurjun.web.service.TemplateManagerService;
import ai.subut.kurjun.web.service.UserRepoContextStore;
import ai.subut.kurjun.web.service.impl.RawManagerServiceImpl;
import ai.subut.kurjun.web.service.impl.TemplateManagerServiceImpl;
import ai.subut.kurjun.web.service.impl.UserRepoContextStoreImpl;
import ninja.uploads.FileItemProvider;


public class Module extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new KurjunPropertiesModule() );
        install( new ControlFileParserModule() );
        install( new ReleaseIndexParserModule() );
        install( new PackagesIndexParserModule() );
        install( new SubutaiTemplateParserModule() );

        install( new FileStoreModule() );
        install( new PackageMetadataStoreModule() );
        install( new SnapMetadataParserModule() );

        install( new RepositoryModule() );

        bind( ArtifactContext.class ).to( GlobalArtifactContext.class );

        bind( KurjunInitializer.class );

        bind( UserRepoContextStore.class ).to( UserRepoContextStoreImpl.class );

        bind( TemplateManagerService.class ).to( TemplateManagerServiceImpl.class );

        bind( RawManagerService.class ).to( RawManagerServiceImpl.class );

        bind( FileItemProvider.class ).to( SubutaiFileHandler.class );
    }
}
