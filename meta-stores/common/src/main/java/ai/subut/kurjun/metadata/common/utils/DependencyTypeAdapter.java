package ai.subut.kurjun.metadata.common.utils;


import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import ai.subut.kurjun.metadata.common.apt.DefaultDependency;
import ai.subut.kurjun.model.metadata.Dependency;
import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.metadata.RelationOperator;


/**
 * Gson type adapter for {@link Dependency}. This type adapter is introduced to correctly convert objects with
 * {@link Dependency} fields to and from JSON. Without such type adapter de-serialization of classes like
 * {@link PackageMetadata} that have fields of interface type {@link Dependency} result in either parse errors or
 * unexpected results.
 *
 */
class DependencyTypeAdapter extends TypeAdapter<Dependency>
{
    final String PACKAGE_PROPERTY = "packageName";
    final String VERSION_PROPERTY = "version";
    final String DEPENDNCY_OP_PROPERTY = "dependencyOperator";
    final String ALTERNATIVES_PROPERTY = "alternatives";


    @Override
    public void write( JsonWriter out, Dependency value ) throws IOException
    {
        out.beginObject();
        out.name( PACKAGE_PROPERTY ).value( value.getPackage() );
        if ( value.getVersion() != null )
        {
            out.name( VERSION_PROPERTY ).value( value.getVersion() );
            out.name( DEPENDNCY_OP_PROPERTY ).value( value.getDependencyOperator().toString() );
        }
        if ( value.getAlternatives() != null )
        {
            out.name( ALTERNATIVES_PROPERTY ).beginArray();
            for ( Dependency dep : value.getAlternatives() )
            {
                write( out, dep );
            }
            out.endArray();
        }
        out.endObject();
    }


    @Override
    public Dependency read( JsonReader in ) throws IOException
    {
        if ( in.peek() == JsonToken.NULL )
        {
            in.nextNull();
            return null;
        }
        if ( in.peek() != JsonToken.BEGIN_OBJECT )
        {
            in.nextString();
            return null;
        }
        return readDependency( in );
    }


    @SuppressWarnings( "ConvertToStringSwitch" )
    private Dependency readDependency( JsonReader in ) throws IOException
    {
        DefaultDependency dep = new DefaultDependency();

        in.beginObject();
        while ( in.hasNext() )
        {
            String name = in.nextName();
            if ( name.equals( PACKAGE_PROPERTY ) )
            {
                dep.setPackage( in.nextString() );
            }
            else if ( name.equals( VERSION_PROPERTY ) )
            {
                dep.setVersion( in.nextString() );
            }
            else if ( name.equals( DEPENDNCY_OP_PROPERTY ) )
            {
                dep.setDependencyOperator( RelationOperator.valueOf( in.nextString() ) );
            }
            else if ( name.equals( ALTERNATIVES_PROPERTY ) )
            {
                in.beginArray();
                while ( in.hasNext() )
                {
                    dep.getAlternatives().add( readDependency( in ) );
                }
                in.endArray();
            }
            else
            {
                in.skipValue();
            }
        }
        in.endObject();

        return dep;
    }
}

