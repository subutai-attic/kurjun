package ai.subut.kurjun.metadata.common.utils;


import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import ai.subut.kurjun.metadata.common.apt.DefaultDependency;
import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.model.metadata.apt.RelationOperator;


/**
 * Gson type adapter for {@link Dependency}. This type adapter is introduced to correctly convert objects with {@link
 * Dependency} fields to and from JSON. Without such type adapter de-serialization of classes like {@link
 * PackageMetadata} that have fields of interface type {@link Dependency} result in either parse errors or unexpected
 * results.
 */
class DependencyTypeAdapter extends TypeAdapter<Dependency>
{
    static final String PACKAGE_PROPERTY = "packageName";
    static final String VERSION_PROPERTY = "version";
    static final String DEPENDNCY_OP_PROPERTY = "dependencyOperator";
    static final String ALTERNATIVES_PROPERTY = "alternatives";


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


    private Dependency readDependency( JsonReader in ) throws IOException
    {
        DefaultDependency dep = new DefaultDependency();

        in.beginObject();
        while ( in.hasNext() )
        {
            String name = in.nextName();
            switch ( name )
            {
                case PACKAGE_PROPERTY:
                    dep.setPackage( in.nextString() );
                    break;
                case VERSION_PROPERTY:
                    dep.setVersion( in.nextString() );
                    break;
                case DEPENDNCY_OP_PROPERTY:
                    dep.setDependencyOperator( RelationOperator.valueOf( in.nextString() ) );
                    break;
                case ALTERNATIVES_PROPERTY:
                    in.beginArray();
                    while ( in.hasNext() )
                    {
                        dep.getAlternatives().add( readDependency( in ) );
                    }
                    in.endArray();
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        return dep;
    }
}

