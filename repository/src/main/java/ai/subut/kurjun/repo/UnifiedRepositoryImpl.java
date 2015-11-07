package ai.subut.kurjun.repo;


import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.Repository;
import ai.subut.kurjun.model.repository.UnifiedRepository;


/**
 * Unified repository implementation. This implementation does not differentiate repository package types. It is just a
 * wrapper to a collection of repository instances. All repository instances have suitable operations and so there is no
 * need to differentiate package types. Package type related flags or operations maybe added in future.
 *
 */
class UnifiedRepositoryImpl extends RepositoryBase implements UnifiedRepository
{

    private URL url;
    private Set<Repository> repositories;


    public UnifiedRepositoryImpl()
    {
        // TODO: set url
        this.repositories = new HashSet<>();
    }


    @Override
    public URL getUrl()
    {
        return url;
    }


    @Override
    public boolean isKurjun()
    {
        return true;
    }


    @Override
    public Set<ReleaseFile> getDistributions()
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }


    @Override
    public Set<Repository> getRepositories()
    {
        return repositories;
    }


    @Override
    public SerializableMetadata getPackageInfo( Metadata metadata )
    {
        Comparator<Repository> c = makeLocalsFirstComparator();
        Iterator<Repository> it = repositories.stream().sorted( c ).iterator();
        while ( it.hasNext() )
        {
            SerializableMetadata m = it.next().getPackageInfo( metadata );
            if ( m != null )
            {
                return m;
            }
        }
        return null;
    }


    @Override
    public InputStream getPackageStream( Metadata metadata )
    {
        Comparator<Repository> c = makeLocalsFirstComparator();
        Iterator<Repository> it = repositories.stream().sorted( c ).iterator();
        while ( it.hasNext() )
        {
            InputStream is = it.next().getPackageStream( metadata );
            if ( is != null )
            {
                return is;
            }
        }
        return null;
    }


    private Comparator<Repository> makeLocalsFirstComparator()
    {
        return new Comparator<Repository>()
        {
            @Override
            public int compare( Repository r1, Repository r2 )
            {
                // local repo shall go first so it shall have lesser value
                int i1 = r1 instanceof LocalRepository ? 0 : 1;
                int i2 = r2 instanceof LocalRepository ? 0 : 1;
                return Integer.compare( i1, i2 );
            }
        };
    }


}

