package ai.subut.kurjun.index;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.vafer.jdeb.debian.ControlFile;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.index.service.PackagesIndexParser;
import ai.subut.kurjun.model.index.IndexPackageMetaData;


class PackagesIndexParserImpl implements PackagesIndexParser
{

    @Override
    public List<IndexPackageMetaData> parse( File indexFile ) throws IOException
    {
        List<IndexPackageMetaData> res = new LinkedList<>();
        try ( InputStream is = openStream( indexFile );
              BufferedReader br = new BufferedReader( new InputStreamReader( is ) ) )
        {
            String batch;
            while ( !( batch = readNextMetadataBatch( br ) ).isEmpty() )
            {
                ControlFile cfp = new PackageIndexFieldsParser();
                try
                {
                    cfp.parse( batch );
                }
                catch ( ParseException ex )
                {
                    throw new IOException( ex );
                }
                IndexPackageMetaData item = new IndexPackageMetadataImpl( cfp );
                res.add( item );
            }
        }
        return res;
    }


    private String readNextMetadataBatch( BufferedReader reader ) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        String line;
        while ( ( line = reader.readLine() ) != null )
        {
            if ( line.trim().isEmpty() )
            {
                if ( sb.length() > 0 )
                {
                    break;
                }
            }
            else
            {
                sb.append( line ).append( System.lineSeparator() );
            }
        }
        return sb.toString();
    }


    private InputStream openStream( File indexFile ) throws IOException
    {
        InputStream fis = new FileInputStream( indexFile );
        switch ( CompressionType.getCompressionType( indexFile ) )
        {
            case NONE:
                return fis;
            case GZIP:
                return new GZIPInputStream( fis );
            case BZIP2:
                return new BZip2CompressorInputStream( new BufferedInputStream( fis ) );
            case XZ:
                return new XZCompressorInputStream( new BufferedInputStream( fis ) );
            case LZMA:
                return new LZMACompressorInputStream( new BufferedInputStream( fis ) );
            default:
                throw new AssertionError( CompressionType.getCompressionType( indexFile ).name() );
        }
    }


}

