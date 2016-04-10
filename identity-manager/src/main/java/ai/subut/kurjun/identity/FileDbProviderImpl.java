package ai.subut.kurjun.identity;


import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.identity.service.FileDbProvider;


public class FileDbProviderImpl implements FileDbProvider
{

    private String file;


    @Inject
    public FileDbProviderImpl( KurjunProperties kurjunProperties )
    {
        this.file = kurjunProperties.getWithDefault( KurjunConstants.SECURITY_FILEDB_PATH,"./security.db" );
    }


    public FileDbProviderImpl( String file )
    {
        this.file = file;
    }


    //*******************************
    @Override
    public FileDb get( boolean readOnly )
    {
        try
        {
            return new FileDb( file, readOnly );
        }
        catch ( IOException ex )
        {
            throw new ProvisionException( "Failed to init file db", ex );
        }
    }

}