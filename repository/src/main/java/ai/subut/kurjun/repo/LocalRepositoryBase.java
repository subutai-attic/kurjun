package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;

import com.google.inject.Inject;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.ArtifactId;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.repo.service.RepositoryManager;


/**
 * Abstract base class for local repositories.
 */
abstract class LocalRepositoryBase extends RepositoryBase implements LocalRepository
{
    private static Logger LOGGER = LoggerFactory.getLogger( LocalRepositoryBase.class );

    @Inject
    RepositoryManager repositoryManager;


    @Override
    public SerializableMetadata getPackageInfo( ArtifactId id)
    {
        RepositoryData repoData = getRepositoryData( "" , 0, "public-user" );

        //ArtifactId id = repositoryManager.constructArtifactId( repoData, metadata );

        try
        {
            Object artifact = repositoryManager.getArtifact( repoData.getType(), id );

            if(artifact != null)
                return (SerializableMetadata) artifact;

        }
        catch ( Exception ex )
        {
            getLogger().error( "Failed to get package info", ex );
        }
        return null;
    }


    @Override
    public InputStream getPackageStream( ArtifactId id )
    {
        SerializableMetadata m = getPackageInfo( id );

        if ( m == null )
        {
            return null;
        }
        try
        {
            FileStore fileStore = getFileStore();
            if ( fileStore.contains( m.getMd5Sum() ) )
            {
                return fileStore.get( m.getMd5Sum() );
            }
            else
            {
                throw new IllegalStateException( "File not found for metadata" );
            }
        }
        catch ( IOException ex )
        {
            getLogger().error( "Failed to get package", ex );
        }
        return null;
    }


    @Override
    public List<SerializableMetadata> listPackages()
    {
        RepositoryData repoData = getRepositoryData( "", 0 , "");
        List<SerializableMetadata> result = new LinkedList<>();
        try
        {
            List<Object> items = repositoryManager.getAllArtifacts(repoData);

            if(!items.isEmpty())
            {
                return (List<SerializableMetadata>)(Object)items;
            }
        }
        catch ( Exception ex )
        {
            getLogger().error( "Failed to list package in metadata store", ex );
        }

        return null;
    }


    @Override
    public List<SerializableMetadata> listPackages(String context, int type)
    {
        RepositoryData repoData = getRepositoryData(context, type , "" );

        List<SerializableMetadata> result = new LinkedList<>();

        try
        {
            List<Object> items = repositoryManager.getAllArtifacts(repoData);

            if(!items.isEmpty())
            {
                return (List<SerializableMetadata>)(Object)items;
            }
        }
        catch ( Exception ex )
        {
            getLogger().error( "Failed to list package in metadata store", ex );
        }
        LOGGER.info( " returning listing packages " );
        return null;
    }


    @Override
    public Metadata put( InputStream is ) throws IOException
    {
        return put( is, CompressionType.NONE );
    }


    @Override
    public boolean delete( ArtifactId id ) throws IOException
    {
        RepositoryData repoData = getRepositoryData( id.getContext(), id.getType(), "" );

        FileStore fileStore = getFileStore();

        Object artifact = repositoryManager.getArtifact( repoData.getType(), id );

        if ( artifact != null)
        {
            fileStore.remove( id.getMd5Sum() );  // TODO
            repositoryManager.removeArtifact(repoData.getType(), artifact );
            return true;
        }

        return false;
    }


    /**
     * Gets logger instance associated classes that extend this abstract class.
     *
     * @return logger instance
     */
    protected abstract Logger getLogger();


    /**
     * Gets meta data store to be used in implementations classes of this abstract class.
     *
     * @return meta data store
     */
    //protected abstract PackageMetadataStore getMetadataStore();


    protected abstract RepositoryData getRepositoryData( String repoContext, int type, String owner );

    /**
     * Gets file store to be used in implementations classes of this abstract class.
     *
     * @return file store
     */
    protected abstract FileStore getFileStore();



    @Override
    public String md5()
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance( "MD5" );
            List<SerializableMetadata> list = listPackages();

            if ( list.size() != 0 )
            {
                messageDigest.update( list.toString().getBytes() );

                byte[] md5 = messageDigest.digest();
                return Hex.encodeHexString( md5 );
            }
        }
        catch ( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }

        return "";
    }
}

