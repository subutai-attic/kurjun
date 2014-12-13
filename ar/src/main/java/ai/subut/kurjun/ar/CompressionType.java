package ai.subut.kurjun.ar;


import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * The type of compression used.
 */
public enum  CompressionType
{
    NONE( null ), GZIP( "gz" ), BZIP2( "bz2" ), XZ ( "xz" ), LZMA ( "lzma" );


    // using this map is faster than standard Enum valueOf methods
    private static final Map<String,CompressionType> extensionMap;
    private final String extension;

    static {
        extensionMap = new HashMap<>( 5 );
        for ( CompressionType type: CompressionType.values() )
        {
            extensionMap.put( type.getExtension(), type );
        }
    }


    private CompressionType( String extension )
    {
        this.extension = extension;
    }


    public static String getExtension( String filename ) {
        if ( filename.lastIndexOf( '.' ) == -1 )
        {
            return null;
        }

        return filename.substring( filename.lastIndexOf( '.' ) + 1 );
    }


    public String getExtension()
    {
        return extension;
    }


    public static CompressionType getCompressionType( String filename )
    {
        return extensionMap.get( getExtension( filename ) );
    }


    public static CompressionType getCompressionType( File file )
    {
        return getCompressionType( file.getName() );
    }
}
