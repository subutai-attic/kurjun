package ai.subut.kurjun.repo;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.repository.LocalRepository;
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

        try
        {
            this.url = new URL( "http", "localhost", null );
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
    public Set<ReleaseFile> getDistributions()
    {
        File file = baseDirectory.resolve( "conf/distributions" ).toFile();
        if ( !file.exists() )
        {
            throw new IllegalStateException( "Invalid apt repo" );
        }

        List<String> releases = new ArrayList<>();
        Pattern pattern = Pattern.compile( "Codename:\\s*(\\w+)" );
        try ( BufferedReader br = new BufferedReader( new FileReader( file ) ) )
        {
            String line;
            while ( ( line = br.readLine() ) != null )
            {
                Matcher matcher = pattern.matcher( line );
                if ( matcher.matches() )
                {
                    releases.add( matcher.group( 1 ) );
                }
            }
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to read distributions file", ex );
            return Collections.emptySet();
        }

        Set<ReleaseFile> result = new HashSet<>();
        for ( String release : releases )
        {
            File releaseFile = baseDirectory.resolve( "dists/" + release + "/Release" ).toFile();
            try ( InputStream is = new FileInputStream( releaseFile ) )
            {
                ReleaseFile rf = releaseIndexParser.parse( is );
                result.add( rf );
            }
            catch ( IOException ex )
            {
                LOGGER.error( "Failed to parse release index for {}", release, ex );
            }
        }
        return result;
    }


}

