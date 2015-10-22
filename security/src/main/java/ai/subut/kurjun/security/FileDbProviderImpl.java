package ai.subut.kurjun.security;


import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;

import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.common.service.KurjunPropertyKey;
import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.security.service.FileDbProvider;


class FileDbProviderImpl implements FileDbProvider
{

    private String file;


    @Inject
    public FileDbProviderImpl( KurjunProperties kurjunProperties )
    {
        this.file = kurjunProperties.getWithDefault( KurjunPropertyKey.SECURITY_FILEDB_PATH,
                                                     "./securty.db" );
    }


    public FileDbProviderImpl( String file )
    {
        this.file = file;
    }


    @Override
    public FileDb get()
    {
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

