package ai.subut.kurjun.storage.factory;


import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.storage.fs.FileSystemFileStoreFactory;
import ai.subut.kurjun.storage.s3.S3FileStoreFactory;


class FileStoreFactoryImpl implements FileStoreFactory
{

    private KurjunProperties properties;
    private FileSystemFileStoreFactory fileSystemFileStoreFactory;
    private S3FileStoreFactory s3FileStoreFactory;


    @Inject
    public void setProperties( KurjunProperties properties )
    {
        this.properties = properties;
    }


    @Inject
    public void setFileSystemFileStoreFactory( FileSystemFileStoreFactory fileSystemFileStoreFactory )
    {
        this.fileSystemFileStoreFactory = fileSystemFileStoreFactory;
    }


    @Inject
    public void setS3FileStoreFactory( S3FileStoreFactory s3FileStoreFactory )
    {
        this.s3FileStoreFactory = s3FileStoreFactory;
    }


    @Override
    public FileStore create( KurjunContext context )
    {
        Properties prop = properties.getContextProperties( context.getName() );
        String type = prop.getProperty( TYPE );

        if ( FILE_SYSTEM.equals( type ) )
        {
            return fileSystemFileStoreFactory.create( context );
        }

        if ( S3.equals( type ) )
        {
            return s3FileStoreFactory.create( context );
        }

        throw new ProvisionException( "Invalid context properties" );
    }

}

