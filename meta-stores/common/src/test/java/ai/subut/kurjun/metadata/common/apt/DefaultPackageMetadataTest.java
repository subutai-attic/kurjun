package ai.subut.kurjun.metadata.common.apt;


import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.apt.Dependency;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class DefaultPackageMetadataTest
{
    private DefaultIndexPackageMetaData packageMetaData;
    private List<Dependency> dependencies;
    private List<Dependency> recommends;
    private List<Dependency> suggests;
    private List<Dependency> enhances;
    private List<Dependency> preDepends;
    private List<Dependency> conflicts;
    private List<Dependency> breaks;
    private List<Dependency> replaces;
    private List<String> provides;
    private Map<String, String> extra = new HashMap<>();


    @Before
    public void setUp() throws Exception
    {
        dependencies = Lists.newArrayList();
        recommends = Lists.newArrayList();
        suggests = Lists.newArrayList();
        enhances = Lists.newArrayList();
        preDepends = Lists.newArrayList();
        conflicts = Lists.newArrayList();
        breaks = Lists.newArrayList();
        replaces = Lists.newArrayList();
        provides = Lists.newArrayList();

        packageMetaData = new DefaultIndexPackageMetaData();

        packageMetaData.setMd5( DefaultIndexPackageMetaDataTest.md5() );
        packageMetaData.setComponent( "component" );
        packageMetaData.setFilename( "fileName" );
        packageMetaData.setPackage( "package" );
        packageMetaData.setVersion( "1.0.0" );
        packageMetaData.setSource( "source" );
        packageMetaData.setMaintainer( "maintainer" );
        packageMetaData.setArchitecture( Architecture.ALL );
        packageMetaData.setInstalledSize( 5 );
        packageMetaData.setDependencies( dependencies );
        packageMetaData.setRecommends( recommends );
        packageMetaData.setSuggests( suggests );
        packageMetaData.setEnhances( enhances );
        packageMetaData.setPreDepends( preDepends );
        packageMetaData.setConflicts( conflicts );
        packageMetaData.setBreaks( breaks );
        packageMetaData.setReplaces( replaces );
        packageMetaData.setProvides( provides );
        packageMetaData.setSection( "section" );
        packageMetaData.setPriority( null );
        packageMetaData.setHomepage( new URL( "http://test.com" ) );
        packageMetaData.setDescription( "description" );
        packageMetaData.setExtra( extra );
    }


    @Test
    public void getId() throws Exception
    {
        assertNotNull( packageMetaData.getId() );
    }


    @Test
    public void getMd5Sum() throws Exception
    {
        assertNotNull( packageMetaData.getMd5Sum() );
    }


    @Test
    public void getComponent() throws Exception
    {
        assertNotNull( packageMetaData.getComponent() );
    }


    @Test
    public void getFilename() throws Exception
    {
        assertNotNull( packageMetaData.getFilename() );
    }


    @Test
    public void getPackage() throws Exception
    {
        assertNotNull( packageMetaData.getPackage() );
    }


    @Test
    public void getName() throws Exception
    {
        assertNotNull( packageMetaData.getName() );
    }


    @Test
    public void getVersion() throws Exception
    {
        assertNotNull( packageMetaData.getVersion() );
    }


    @Test
    public void getSource() throws Exception
    {
        assertNotNull( packageMetaData.getSource() );
    }


    @Test
    public void getMaintainer() throws Exception
    {
        assertNotNull( packageMetaData.getMaintainer() );
    }


    @Test
    public void getArchitecture() throws Exception
    {
        assertNotNull( packageMetaData.getArchitecture() );
    }


    @Test
    public void getInstalledSize() throws Exception
    {
        assertNotNull( packageMetaData.getInstalledSize() );
    }


    @Test
    public void getDependencies() throws Exception
    {
        assertNotNull( packageMetaData.getDependencies() );
    }


    @Test
    public void getRecommends() throws Exception
    {
        assertNotNull( packageMetaData.getRecommends() );
    }


    @Test
    public void getSuggests() throws Exception
    {
        assertNotNull( packageMetaData.getSuggests() );
    }


    @Test
    public void getEnhances() throws Exception
    {
        assertNotNull( packageMetaData.getEnhances() );
    }


    @Test
    public void getPreDepends() throws Exception
    {
        assertNotNull( packageMetaData.getPreDepends() );
    }


    @Test
    public void getConflicts() throws Exception
    {
        assertNotNull( packageMetaData.getConflicts() );
    }


    @Test
    public void getBreaks() throws Exception
    {
        assertNotNull( packageMetaData.getBreaks() );
    }


    @Test
    public void getReplaces() throws Exception
    {
        packageMetaData.getReplaces();
    }


    @Test
    public void getProvides() throws Exception
    {
        packageMetaData.getProvides();
    }


    @Test
    public void getSection() throws Exception
    {
        assertNotNull( packageMetaData.getSection() );
    }


    @Test
    public void getPriority() throws Exception
    {
        packageMetaData.getPriority();
    }


    @Test
    public void getHomepage() throws Exception
    {
        assertNotNull( packageMetaData.getHomepage() );
    }


    @Test
    public void getDescription() throws Exception
    {
        assertNotNull( packageMetaData.getDescription() );
    }


    @Test
    public void getExtra() throws Exception
    {
        assertNotNull( packageMetaData.getExtra() );
    }


    @Test
    public void serialize() throws Exception
    {
        packageMetaData.serialize();
    }


    @Test
    public void equals() throws Exception
    {
        packageMetaData.equals( new Object() );
        packageMetaData.equals( packageMetaData );
        packageMetaData.hashCode();
        packageMetaData.toString();
    }
}