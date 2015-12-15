package ai.subut.kurjun.repo;


import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final Set<Repository> repositories;
    private final Set<Repository> secondaryRepositories;


    public UnifiedRepositoryImpl()
    {
        // TODO: set url
        this.repositories = new HashSet<>();
        this.secondaryRepositories = new HashSet<>();
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
        if ( RepositoryHelpers.isAptRepository( this ) )
        {
            Set<ReleaseFile> releases = new HashSet<>();
            for ( Repository r : repositories )
            {
                releases.addAll( r.getDistributions() );
            }
            return releases;
        }
        throw new UnsupportedOperationException( "Not supported for non-apt repositories." );
    }


    @Override
    public Set<Repository> getRepositories()
    {
        return repositories;
    }


    @Override
    public SerializableMetadata getPackageInfo( Metadata metadata )
    {
        Iterator<Repository> it = getAllRepositories().iterator();
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
        Iterator<Repository> it = getAllRepositories().iterator();
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


    @Override
    public List<SerializableMetadata> listPackages()
    {
        List<SerializableMetadata> result = new LinkedList<>();
        for ( Repository repo : getAllRepositories() )
        {
            List<SerializableMetadata> list = repo.listPackages();
            for ( SerializableMetadata meta : list )
            {
                if ( !result.contains( meta ) )
                {
                    result.add( meta );
                }
            }
        }
        return result;
    }


    private Comparator<Repository> makeLocalsFirstComparator()
    {
        return (Repository r1, Repository r2) ->
        {
            // local repo shall go first so it shall have lesser value
            int i1 = r1 instanceof LocalRepository ? 0 : 1;
            int i2 = r2 instanceof LocalRepository ? 0 : 1;
            return Integer.compare( i1, i2 );
        };
    }


    @Override
    public Set<Repository> getSecondaryRepositories()
    {
        return secondaryRepositories;
    }


    private List<Repository> getAllRepositories()
    {
        Comparator<Repository> c = makeLocalsFirstComparator();
        List<Repository> sorted = repositories.stream().sorted( c ).collect( Collectors.toList() );
        List<Repository> list = new LinkedList<>();
        list.addAll( sorted );
        list.addAll( secondaryRepositories );
        return list;
    }

}

