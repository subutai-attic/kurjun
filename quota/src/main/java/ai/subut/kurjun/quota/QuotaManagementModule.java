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
import ai.subut.kurjun.quota.disk.DiskQuotaManager;
import ai.subut.kurjun.quota.transfer.TransferQuotaManager;


public class QuotaManagementModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        Module module = new FactoryModuleBuilder()
                .implement( DiskQuotaManager.class, DiskQuotaManager.class )
                .implement( TransferQuotaManager.class, TransferQuotaManager.class )
                .build( QuotaManagerFactory.class );

        install( module );
    }


    /**
     * File db provider for quota info store. {@link FileDb} instances annotated with {@link Quota} are produced by this
     * method.
     *
     * @param kurjunProperties Kurjun properties that is used to get file db path for the quota info store
     * @return
     *
     * @see KurjunConstants#QUOTA_FILEDB_PATH
     */
    @Provides
    @Quota
    public FileDb getFileDb( KurjunProperties kurjunProperties )
    {
        String file = kurjunProperties.getWithDefault( KurjunConstants.QUOTA_FILEDB_PATH, "./quota/" );
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

