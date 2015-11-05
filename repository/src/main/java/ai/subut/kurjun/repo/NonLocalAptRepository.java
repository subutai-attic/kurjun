package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.repo.http.HttpHandler;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;


/**
 * Nonlocal repository implementation. Remote repositories can be either non-virtual or virtual, this does not matter
 * for {@link NonLocalRepository} implementation.
 *
 */
class NonLocalAptRepository extends RepositoryBase implements NonLocalRepository
{
    private static final Logger LOGGER = LoggerFactory.getLogger( NonLocalAptRepository.class );

    private final HttpHandler httpHandler = new HttpHandler( this );
    private URL url;
    private ReleaseIndexParser releaseIndexParser;


    /**
     * Constructs nonlocal repository located by the specified URL.
     *
     * @param releaseIndexParser
     * @param url URL of the remote repository
     */
    @Inject
    public NonLocalAptRepository( ReleaseIndexParser releaseIndexParser, @Assisted URL url )
    {
        this.releaseIndexParser = releaseIndexParser;
        this.url = url;
    }


    @Override
    public URL getUrl()
    {
        return url;
    }


    @Override
    public boolean isKurjun()
    {
        // TODO: how to define if remote repo is Kurjun or not
        return false;
    }


    @Override
    public Set<ReleaseFile> getDistributions()
    {
        throw new UnsupportedOperationException( "TODO: how to get releases from a remote repo" );
    }


    @Override
    public InputStream getPackage( Metadata metadata )
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    protected InputStream openReleaseIndexFileStream( String release ) throws IOException
    {
        return httpHandler.streamReleaseIndexFile( release, false );
    }


}

