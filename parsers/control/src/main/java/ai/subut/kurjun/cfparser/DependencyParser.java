package ai.subut.kurjun.cfparser;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.RelationOperator;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;


/**
 * Parses dependency relationships where a comma separated list of package names
 * are provided with possible version information and alternative dependencies.
 * The exact syntax used for such fields is best described in Chapter 7 of the
 * <a href=https://www.debian.org/doc/debian-policy/ch-relationships.html>Debian
 * Policy Manual</a>.
 */
public class DependencyParser
{
    private static final Logger LOG = LoggerFactory.getLogger( DependencyParser.class );


    public List<Dependency> getDependencies( String value )
    {
        checkNotNull( value, "The field value to be parsed CANNOT be null!" );
        checkState( value.length() > 0 );

        String[] depsRaw = value.split( "," );
        List<Dependency> deps = new ArrayList<>();
        for ( String rawDep : depsRaw )
        {
            rawDep = rawDep.trim();
            Dependency dep;

            if ( rawDep.indexOf( '|' ) != -1 )
            {
                dep = getDependencyWithAlternatives( rawDep );
            }
            else if ( rawDep.indexOf( '(' ) != -1 )
            {
                dep = getDependencyWithVersion( rawDep );
            }
            else
            {
                dep = new Dep( rawDep );
            }

            deps.add( dep );
            LOG.debug( "Extracted dependency {} from '{}'", rawDep, value );
        }

        return deps;
    }


    /**
     * Parses dependencies that have version information but are not part of
     * a set of alternatives. The other method is used for that.
     *
     * @param rawDep the raw single dependency element that needs to be parsed
     * @return the Dependency object with populated information
     */
    private Dep getDependencyWithVersion( String rawDep )
    {
        checkNotNull( rawDep );
        checkState( rawDep.indexOf( '(' ) != -1, rawDep );
        rawDep = rawDep.trim();
        checkState( rawDep.charAt( rawDep.length() - 1 ) == ')', "rawDep = " + rawDep );

        String[] parts = rawDep.split( "\\(" );
        checkState( parts.length == 2 );
        parts[0] = parts[0].trim();
        parts[1] = parts[1].trim();

        String pkg = parts[0];
        String version;
        RelationOperator op;

        if ( parts[1].charAt( 0 ) == '=' )
        {
            op = RelationOperator.Equal;
            version = parts[1].substring( 1, parts[1].length() - 1 ).trim();
        }
        else
        {
            op = RelationOperator.fromSymbol( parts[1].substring( 0, 2 ).trim() );
            version = parts[1].substring( 2, parts[1].length() - 1 ).trim();
        }

        return new Dep( pkg, op, version );
    }


    private Dep getDependencyWithAlternatives( String rawDep )
    {
        checkState( rawDep.indexOf( '|' ) != -1 );

        // this first is special and what we view as the real dep
        // with others as alternatives for that dependency
        Dep dep;
        String[] parts = rawDep.split( "\\|" );
        String first = parts[0];
        if ( first.indexOf( '(' ) != -1 )
        {
            dep = getDependencyWithVersion( first );
        }
        else
        {
            dep = new Dep( first );
        }

        parts = Arrays.copyOfRange( parts, 1, parts.length );
        for ( String part : parts )
        {
            part = part.trim();

            if ( part.indexOf( '(' ) != -1 )
            {
                dep.addAlternative( getDependencyWithVersion( part ) );
            }
            else
            {
                dep.addAlternative( new Dep( part ) );
            }
        }
        return dep;
    }


    class Dep implements Dependency
    {
        String pkg;
        String version;
        RelationOperator op;
        List<Dependency> alternatives;


        Dep( String pkg )
        {
            this.pkg = pkg;
        }


        public Dep( final String pkg, final RelationOperator op, final String version )
        {
            this.pkg = pkg;
            this.op  = op;
            this.version = version;
        }


        @Override
        public String getPackage()
        {
            return pkg;
        }


        @Override
        public List<Dependency> getAlternatives()
        {
            return alternatives;
        }


        void addAlternative( Dep dep )
        {
            if ( alternatives == null )
            {
                alternatives = new ArrayList<>();
            }

            alternatives.add( dep );
        }


        @Override
        public String getVersion()
        {
            return version;
        }


        @Override
        public RelationOperator getDependencyOperator()
        {
            return op;
        }
    }
}
