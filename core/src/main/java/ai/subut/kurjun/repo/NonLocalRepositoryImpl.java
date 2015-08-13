package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.repo.http.HttpHandler;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;


class NonLocalRepositoryImpl extends RepositoryBase implements NonLocalRepository
{
    private static final Logger LOGGER = LoggerFactory.getLogger( NonLocalRepositoryImpl.class );

    private final HttpHandler httpHandler = new HttpHandler( this );
    private ReleaseIndexParser releaseIndexParser;


    @Inject
    public NonLocalRepositoryImpl( ReleaseIndexParser releaseIndexParser )
    {
        this.releaseIndexParser = releaseIndexParser;
    }


    @Override
    public void init( URL url )
    {
        this.url = url;
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
        return httpHandler.streamDistributionsFile();
    }


    @Override
    protected InputStream openReleaseIndexFileStream( String release ) throws IOException
    {
        return httpHandler.streamReleaseIndexFile( release, false );
    }


}

