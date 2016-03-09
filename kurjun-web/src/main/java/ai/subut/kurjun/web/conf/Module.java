package ai.subut.kurjun.web.conf;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.cfparser.ControlFileParserModule;
import ai.subut.kurjun.common.KurjunPropertiesImpl;
import ai.subut.kurjun.common.context.GlobalArtifactContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.index.PackagesIndexParserModule;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreModule;
import ai.subut.kurjun.model.context.ArtifactContext;
import ai.subut.kurjun.repo.RepositoryModule;
import ai.subut.kurjun.riparser.ReleaseIndexParserModule;
import ai.subut.kurjun.snap.SnapMetadataParserModule;
import ai.subut.kurjun.storage.factory.FileStoreFactory;
import ai.subut.kurjun.storage.factory.FileStoreFactoryImpl;
import ai.subut.kurjun.storage.factory.FileStoreModule;
import ai.subut.kurjun.subutai.SubutaiTemplateParserModule;
import ai.subut.kurjun.web.service.TemplateManagerService;
import ai.subut.kurjun.web.service.impl.TemplateManagerServiceImpl;


public class Module extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new ControlFileParserModule() );
        install( new ReleaseIndexParserModule() );
        install( new PackagesIndexParserModule() );
        install( new SubutaiTemplateParserModule() );

        install( new FileStoreModule() );
        install( new PackageMetadataStoreModule() );
        install( new SnapMetadataParserModule() );

        install( new RepositoryModule() );

        bind( ArtifactContext.class).to( GlobalArtifactContext.getInstance().getClass() );

        bind( KurjunProperties.class).to( KurjunPropertiesImpl.class );

        bind( FileStoreFactory.class ).to(FileStoreFactoryImpl.class );

        bind( TemplateManagerService.class ).to( TemplateManagerServiceImpl.class );

    }
}
