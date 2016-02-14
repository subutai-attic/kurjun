package ai.subut.kurjun.guice;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.cfparser.ControlFileParserModule;
import ai.subut.kurjun.common.KurjunBootstrap;
import ai.subut.kurjun.common.KurjunPropertiesModule;
import ai.subut.kurjun.index.PackagesIndexParserModule;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreModule;
import ai.subut.kurjun.quota.QuotaManagementModule;
import ai.subut.kurjun.repo.RepositoryModule;
import ai.subut.kurjun.riparser.ReleaseIndexParserModule;
import ai.subut.kurjun.security.SecurityModule;
import ai.subut.kurjun.snap.SnapMetadataParserModule;
import ai.subut.kurjun.storage.factory.FileStoreModule;
import ai.subut.kurjun.subutai.SubutaiTemplateParserModule;


/**
 * Common Guice module that includes all independent Kurjun modules. This module is useful for environments where Guice
 * is already used; including this module in hosting environment's module would be sufficient to have all Kurjun
 * features. It is contrary to {@link KurjunBootstrap} which can accept modules and bootstrap Guice itself in
 * environments where Guice is not used.
 *
 */
public class KurjunGuiceModule extends AbstractModule
{


    @Override
    protected void configure()
    {
        install( new KurjunPropertiesModule() );

        install( new ReleaseIndexParserModule() );
        install( new ControlFileParserModule() );
        install( new PackagesIndexParserModule() );
        install( new SubutaiTemplateParserModule() );
        install( new SnapMetadataParserModule() );

        install( new FileStoreModule() );
        install( new PackageMetadataStoreModule() );

        install( new SecurityModule() );
        install( new RepositoryModule() );
        install( new QuotaManagementModule() );

    }

}

