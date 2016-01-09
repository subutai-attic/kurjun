package ai.subut.kurjun.quota;


import java.io.IOException;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.quota.disk.DiskQuotaController;


public class QuotaManagementModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        Module module = new FactoryModuleBuilder()
                .implement( DiskQuotaController.class, DiskQuotaController.class )
                .build( QuotaControllerFactory.class );

        install( module );
    }


    @Provides
    @Quota
    public FileDb getFileDb( KurjunProperties kurjunProperties )
    {
        String file = kurjunProperties.getWithDefault( KurjunConstants.QUOTA_FILEDB_PATH, "./quota.db" );
        try
        {
            return new FileDb( file );
        }
        catch ( IOException ex )
        {
            throw new ProvisionException( "Failed to init file db", ex );
        }
    }

}

