package ai.subut.kurjun.riparser.impl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.debian.ControlFile;

import ai.subut.kurjun.model.index.Checksum;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.riparser.ReleaseIndexParser;


public class ReleaseIndexParserImpl implements ReleaseIndexParser
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ReleaseIndexParserImpl.class );


    // TODO: HANDLE FILES WITH SIGNATURES
    @Override
    public ReleaseFile parse( InputStream is ) throws IOException
    {
        ReleaseIndexFields fp = new ReleaseIndexFields();
        try
        {
            fp.parse( is );
        }
        catch ( ParseException ex )
        {
            LOGGER.error( "Failed to parse release index fields", ex );
            throw new IOException( ex );
        }

        DefaultReleaseFile rf = new DefaultReleaseFile( fp );
        rf.setIndexResources( buildResources( fp ) );
        return rf;
    }


    private Map<String, ReleaseChecksummedResource> buildResources( ControlFile cf ) throws IOException
    {
        Map<String, ReleaseChecksummedResource> resources = new HashMap<>();
        parseChecksumField( Checksum.MD5, cf.get( ReleaseFile.MD5SUM_FILED ), resources );
        parseChecksumField( Checksum.SHA1, cf.get( ReleaseFile.SHA1_FILED ), resources );
        parseChecksumField( Checksum.SHA256, cf.get( ReleaseFile.SHA256_FILED ), resources );
        return resources;
    }


    private void parseChecksumField( Checksum checksumType, String value,
                                     Map<String, ReleaseChecksummedResource> accumulated )
            throws IOException
    {
        String line;
        BufferedReader br = new BufferedReader( new StringReader( value ) );
        while ( ( line = br.readLine() ) != null )
        {
            String[] arr = line.trim().split( " " );
            String path = arr[2];
            // format: checksum size relativePath
            if ( arr.length == 3 )
            {
                ReleaseChecksummedResource res = accumulated.get( path );
                if ( res != null )
                {
                    res.checksums.put( checksumType, arr[0] );
                }
                else
                {
                    res = new ReleaseChecksummedResource( path );
                    res.checksums.put( checksumType, arr[0] );
                    res.size = Long.parseLong( arr[1] );
                    accumulated.put( res.path, res );
                }
            }
        }
    }

}

