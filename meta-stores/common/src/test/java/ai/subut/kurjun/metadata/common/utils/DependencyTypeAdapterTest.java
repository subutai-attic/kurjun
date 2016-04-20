package ai.subut.kurjun.metadata.common.utils;


import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.org.glassfish.gmbal.ManagedAttribute;

import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.RelationOperator;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class DependencyTypeAdapterTest
{
    static final String PACKAGE_PROPERTY = "packageName";
    static final String VERSION_PROPERTY = "version";
    static final String DEPENDNCY_OP_PROPERTY = "dependencyOperator";
    static final String ALTERNATIVES_PROPERTY = "alternatives";

    private DependencyTypeAdapter typeAdapter;

    @Mock
    JsonReader jsonReader;

    @Mock
    JsonWriter jsonWriter;

    @Mock
    Dependency dependency;


    @Before
    public void setUp() throws Exception
    {
        typeAdapter = new DependencyTypeAdapter();
    }


    @Test
    public void writeWhenVersionIsNull() throws Exception
    {
        when( jsonWriter.name( anyString() ) ).thenReturn( jsonWriter );
        when( jsonWriter.value( anyString() ) ).thenReturn( jsonWriter );
        when( dependency.getPackage() ).thenReturn( "test" );

        typeAdapter.write( jsonWriter, dependency );
    }

    @Test
    public void write() throws Exception
    {
        when( dependency.getVersion() ).thenReturn( "1.0.0" );
        when( jsonWriter.name( anyString() ) ).thenReturn( jsonWriter );
        when( jsonWriter.value( anyString() ) ).thenReturn( jsonWriter );
        when( dependency.getPackage() ).thenReturn( "test" );
        when( dependency.getDependencyOperator() ).thenReturn( RelationOperator.EarlierEqual );

        typeAdapter.write( jsonWriter, dependency );
    }


    @Test
    public void readReturnNull() throws Exception
    {
        assertNull( typeAdapter.read( jsonReader ) );
    }


    @Test
    public void readReturnNull2() throws Exception
    {
        when( jsonReader.peek() ).thenReturn( JsonToken.NULL );

        assertNull( typeAdapter.read( jsonReader ) );
    }


    @Test
    public void readPackageProperty() throws Exception
    {
        when( jsonReader.peek() ).thenReturn( JsonToken.BEGIN_OBJECT );
        when( jsonReader.hasNext() ).thenReturn( true ).thenReturn( false );
        when( jsonReader.nextName() ).thenReturn( PACKAGE_PROPERTY );
        when( jsonReader.nextString() ).thenReturn( "test" );

        assertNotNull( typeAdapter.read( jsonReader ) );
    }


    @Test
    public void readVersionProperty() throws Exception
    {
        when( jsonReader.peek() ).thenReturn( JsonToken.BEGIN_OBJECT );
        when( jsonReader.hasNext() ).thenReturn( true ).thenReturn( false );
        when( jsonReader.nextName() ).thenReturn( VERSION_PROPERTY );
        when( jsonReader.nextString() ).thenReturn( "test" );

        assertNotNull( typeAdapter.read( jsonReader ) );
    }


    @Test
    public void readDependencyProperty() throws Exception
    {
        when( jsonReader.peek() ).thenReturn( JsonToken.BEGIN_OBJECT );
        when( jsonReader.hasNext() ).thenReturn( true ).thenReturn( false );
        when( jsonReader.nextName() ).thenReturn( DEPENDNCY_OP_PROPERTY );
        when( jsonReader.nextString() ).thenReturn( RelationOperator.EarlierEqual.toString() );

        assertNotNull( typeAdapter.read( jsonReader ) );
    }


    @Test
    public void readAlternativesProperty() throws Exception
    {
        when( jsonReader.peek() ).thenReturn( JsonToken.BEGIN_OBJECT );
        when( jsonReader.hasNext() ).thenReturn( true ).thenReturn( true ).thenReturn( false );
        when( jsonReader.nextName() ).thenReturn( ALTERNATIVES_PROPERTY );
        when( jsonReader.nextString() ).thenReturn( "test" );

        assertNotNull( typeAdapter.read( jsonReader ) );
    }


    @Test
    public void readDefault() throws Exception
    {
        when( jsonReader.peek() ).thenReturn( JsonToken.BEGIN_OBJECT );
        when( jsonReader.hasNext() ).thenReturn( true ).thenReturn( true ).thenReturn( false );
        when( jsonReader.nextName() ).thenReturn( "test" );
        when( jsonReader.nextString() ).thenReturn( "test" );

        assertNotNull( typeAdapter.read( jsonReader ) );
    }
}