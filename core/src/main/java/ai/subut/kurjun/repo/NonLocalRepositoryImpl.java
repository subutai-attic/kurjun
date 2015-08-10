package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;


class NonLocalRepositoryImpl extends RepositoryBase implements NonLocalRepository
{
    private static final Logger LOGGER = LoggerFactory.getLogger( NonLocalRepositoryImpl.class );

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
    public Set<ReleaseFile> getDistributions()
    {
        List<String> releases;
        try
        {
            releases = readAptReleases();
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to releases of a remote repo", ex );
            return Collections.emptySet();
        }

        Set<ReleaseFile> result = new HashSet<>();
        for ( String release : releases )
        {
            URL releaseIndexUrl = makeReleaseIndexUrl( release, false );
            try ( InputStream is = releaseIndexUrl.openStream() )
            {
                ReleaseFile rf = releaseIndexParser.parse( is );
                result.add( rf );
            }
            catch ( IOException ex )
            {
                LOGGER.error( "Failed to read release index for {}", release, ex );
            }
        }
        return result;
    }


}

