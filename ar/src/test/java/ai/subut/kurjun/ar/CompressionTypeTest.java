package ai.subut.kurjun.ar;


import java.io.File;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


/**
 * Unit tests for class {@link ai.subut.kurjun.ar.CompressionType}.
 */
public class CompressionTypeTest
{

    @Test
    public void testGetExtension()
    {
        assertNull( CompressionType.getExtension( "foo" ) );

        assertEquals( "gz", CompressionType.getExtension( "foo.gz" ) );
        assertEquals( "xz", CompressionType.getExtension( "foo.xz" ) );
        assertEquals( "bz2", CompressionType.getExtension( "foo.bz2" ) );
        assertEquals( "lzma", CompressionType.getExtension( "foo.lzma" ) );

        assertEquals( "gz", CompressionType.getExtension( "foo.bar.gz" ) );
        assertEquals( "xz", CompressionType.getExtension( "foo.bar.xz" ) );
        assertEquals( "bz2", CompressionType.getExtension( "foo.bar.bz2" ) );
        assertEquals( "lzma", CompressionType.getExtension( "foo.bar.lzma" ) );
    }
    

    @Test
    public void testGetCompressionType()
    {
        assertEquals( CompressionType.NONE,  CompressionType.getCompressionType( "foo" ) );
        assertEquals( CompressionType.GZIP,  CompressionType.getCompressionType( "foo.gz" ) );
        assertEquals( CompressionType.BZIP2, CompressionType.getCompressionType( "foo.bz2" ) );
        assertEquals( CompressionType.XZ,    CompressionType.getCompressionType( "foo.xz" ) );
        assertEquals( CompressionType.LZMA,  CompressionType.getCompressionType( "foo.lzma" ) );

        assertEquals( CompressionType.NONE,  CompressionType.getCompressionType( new File( "foo" ) ) );
        assertEquals( CompressionType.GZIP,  CompressionType.getCompressionType( new File( "foo.gz" ) ) );
        assertEquals( CompressionType.BZIP2, CompressionType.getCompressionType( new File( "foo.bz2" ) ) );
        assertEquals( CompressionType.XZ,    CompressionType.getCompressionType( new File( "foo.xz" ) ) );
        assertEquals( CompressionType.LZMA,  CompressionType.getCompressionType( new File( "foo.lzma" ) ) );
    }
}
