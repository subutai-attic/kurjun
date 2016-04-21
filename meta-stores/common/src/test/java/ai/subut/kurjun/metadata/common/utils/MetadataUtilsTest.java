package ai.subut.kurjun.metadata.common.utils;


import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.ar.Ar;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.apt.DefaultIndexPackageMetaDataTest;
import ai.subut.kurjun.model.index.IndexPackageMetaData;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class MetadataUtilsTest
{
    private MetadataUtils metadataUtils;

    @Mock
    PackageMetadata packageMetadata;

    @Mock
    IndexPackageMetaData indexPackageMetaData;

    @Mock
    SnapMetadata snapMetadata;

    @Mock
    SubutaiTemplateMetadata subutaiTemplateMetadata;

    @Mock
    Metadata metadata;


    @Before
    public void setUp() throws Exception
    {
    }


    @Test
    public void testComparator()
    {
        metadataUtils.makeVersionComparator();
    }


    @Test
    public void testSerializablePackageMetadata()
    {
        // mocks
        when( packageMetadata.getMd5Sum() ).thenReturn( DefaultIndexPackageMetaDataTest.md5() );
        when( packageMetadata.getComponent() ).thenReturn( "component" );
        when( packageMetadata.getFilename() ).thenReturn( "fileName" );
        when( packageMetadata.getPackage() ).thenReturn( "package" );
        when( packageMetadata.getVersion() ).thenReturn( "version" );
        when( packageMetadata.getSource() ).thenReturn( "source" );
        when( packageMetadata.getMaintainer() ).thenReturn( "maintainer" );
        when( packageMetadata.getArchitecture() ).thenReturn( Architecture.ALL );
        when( packageMetadata.getInstalledSize() ).thenReturn( 5 );


        metadataUtils.serializablePackageMetadata( packageMetadata );
    }


    @Test
    public void testSerializableIndexPackageMetadata()
    {
        assertNotNull( metadataUtils.serializableIndexPackageMetadata( indexPackageMetaData ) );
    }


    @Test
    public void testSerializableSnapMetadata()
    {
        assertNotNull( metadataUtils.serializableSnapMetadata( snapMetadata ) );
    }


    @Test
    public void testSerializableTemplateMetadata()
    {
        assertNotNull( metadataUtils.serializableTemplateMetadata( subutaiTemplateMetadata ) );
    }


    @Test
    public void testMakeParamsMapMetadataIsNull()
    {
        metadataUtils.makeParamsMap( metadata );
    }

    @Test
    public void testMakeParams()
    {
        when( metadata.getId() ).thenReturn( "test" );
        when( metadata.getMd5Sum() ).thenReturn( DefaultIndexPackageMetaDataTest.md5() );
        when( metadata.getName() ).thenReturn( "name" );
        when( metadata.getVersion() ).thenReturn( "1.0.0" );

        metadataUtils.makeParamsMap( metadata );
    }


    @Test
    public void testMakeVersionComparator()
    {
        DefaultMetadata m1 = new DefaultMetadata();
        m1.setName( "one" );
        m1.setVersion( "7.35.0" );

        DefaultMetadata m2 = new DefaultMetadata();
        m2.setName( "two" );
        m2.setVersion( null );

        DefaultMetadata m3 = new DefaultMetadata();
        m3.setName( "three" );
        m3.setVersion( "1.2-8ubuntu" );

        DefaultMetadata m4 = new DefaultMetadata();
        m4.setName( "four" );
        m4.setVersion( "4.0" );

        List<Metadata> ls = Arrays.asList( m1, m2, m3, m4 );
        ls.sort( MetadataUtils.makeVersionComparator() );

        Assert.assertEquals( m2.getName(), ls.get( 0 ).getName() );
        Assert.assertEquals( m3.getName(), ls.get( 1 ).getName() );
        Assert.assertEquals( m4.getName(), ls.get( 2 ).getName() );
        Assert.assertEquals( m1.getName(), ls.get( 3 ).getName() );
    }
}

