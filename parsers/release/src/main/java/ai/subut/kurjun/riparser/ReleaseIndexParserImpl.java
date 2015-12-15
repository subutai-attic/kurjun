package ai.subut.kurjun.riparser;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.SignatureException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.debian.ControlFile;

import ai.subut.kurjun.model.index.Checksum;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.riparser.pgp.PGPClearSign;
import ai.subut.kurjun.riparser.pgp.PGPVerification;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;


class ReleaseIndexParserImpl implements ReleaseIndexParser
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ReleaseIndexParserImpl.class );

    private Pattern multiSpacePattern = Pattern.compile( "\\s{2,}" );


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

        ReleaseFileWrapper rf = new ReleaseFileWrapper( fp );
        rf.setIndexResources( buildResources( fp ) );
        return rf;
    }


    @Override
    public ReleaseFile parseClearSigned( InputStream is, InputStream keyStream ) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream( 1024 * 2 );
        try
        {
            boolean verified = PGPClearSign.verifyFile( is, keyStream, out );
            if ( verified )
            {
                return parse( new ByteArrayInputStream( out.toByteArray() ) );
            }
            LOGGER.info( "Release data not verified" );
            return null;
        }
        catch ( PGPException | SignatureException ex )
        {
            throw new IOException( "Release file not verified", ex );
        }
    }


    @Override
    public ReleaseFile parseWithSignature( InputStream is, InputStream signStream, InputStream keyStream ) throws IOException
    {
        // read data stream first
        ByteArrayOutputStream data = new ByteArrayOutputStream( 1024 * 2 );
        int n;
        byte[] buf = new byte[1024];
        while ( ( n = is.read( buf ) ) > 0 )
        {
            data.write( buf, 0, n );
        }

        boolean verified = PGPVerification.verifySignature( new ByteArrayInputStream( data.toByteArray() ), signStream,
                                                            keyStream );
        if ( verified )
        {
            return parse( new ByteArrayInputStream( data.toByteArray() ) );
        }
        throw new IOException( "Release file not verified" );
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
            String[] arr = replaceMultipleSpaces( line ).trim().split( " " );
            // format: checksum size relativePath
            if ( arr.length == 3 )
            {
                String path = arr[2];
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


    private String replaceMultipleSpaces( String s )
    {
        return multiSpacePattern.matcher( s ).replaceAll( " " );
    }

}

