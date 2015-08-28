package ai.subut.kurjun.repo;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.repo.http.PathBuilder;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;


class LocalRepositoryImpl extends RepositoryBase implements LocalRepository
{
    private static final Logger LOGGER = LoggerFactory.getLogger( LocalRepositoryImpl.class );

    private ReleaseIndexParser releaseIndexParser;
    private Path baseDirectory;


    @Inject
    public LocalRepositoryImpl( ReleaseIndexParser releaseIndexParser )
    {
        this.releaseIndexParser = releaseIndexParser;

        // TODO: set correct localhost url
        try
        {
            this.url = new URL( "http", "localhost", "" );
        }
        catch ( MalformedURLException ex )
        {
            LOGGER.error( "Failed to create URL for the localhost", ex );
            throw new IllegalStateException( ex );
        }
    }


    @Override
    public void init( String baseDirectory )
    {
        Path path = Paths.get( baseDirectory );
        if ( !Files.exists( path ) )
        {
            throw new IllegalArgumentException( "Apt base directory does not exist: " + baseDirectory );
        }
        if ( !Files.isDirectory( path ) )
        {
            throw new IllegalArgumentException( "Specified pathis not a directory: " + baseDirectory );
        }

        this.baseDirectory = path;
    }


    @Override
    public Path getBaseDirectoryPath()
    {
        return baseDirectory;
    }


    @Override
    public PackageMetadata put( InputStream is ) throws IOException
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public void setFileStore( FileStore fileStore )
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    protected Logger getLogger()
    {
        return LOGGER;
    }


    @Override
    protected ReleaseIndexParser getReleaseIndexParser()
    {
        return releaseIndexParser;
    }


    @Override
    protected InputStream openDistributionsFileStream() throws IOException
    {
        Path path = baseDirectory.resolve( "conf/distributions" );
        return new FileInputStream( path.toFile() );
    }


    @Override
    protected InputStream openReleaseIndexFileStream( String release ) throws IOException
    {
        String path = PathBuilder.instance().setRelease( release ).forReleaseIndexFile().build();
        Path p = baseDirectory.resolve( path );
        return new FileInputStream( p.toFile() );
    }


}

