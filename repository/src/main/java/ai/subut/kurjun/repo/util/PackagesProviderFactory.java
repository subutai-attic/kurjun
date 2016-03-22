package ai.subut.kurjun.repo.util;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.MetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.Repository;
import ai.subut.kurjun.repo.RepositoryHelpers;
import ai.subut.kurjun.repo.service.PackagesIndexBuilder;


/**
 * Factory class for {@link PackagesIndexBuilder.PackagesProvider} instances.
 *
 */
public class PackagesProviderFactory
{

    private static final Logger LOGGER = LoggerFactory.getLogger( PackagesProviderFactory.class );

    @Inject
    PackageMetadataStoreFactory metadataStoreFactory;

    @Inject
    Gson gson;


    /**
     * Creates packages provider that fetches packages from supplied repository that are contained in given component
     * and are of the supplied architecture.
     *
     * @param repository repository to fetch packages
     * @param component component of the repository from which to fetch packages
     * @param arch architecture of the packages
     * @return
     */
    public PackagesIndexBuilder.PackagesProvider create( Repository repository, String component, Architecture arch )
    {
        if ( !RepositoryHelpers.isAptRepository( repository ) )
        {
            throw new IllegalArgumentException( "Not an apt repository." );
        }

        // create new list to securely use its iterator
        List<SerializableMetadata> items = new ArrayList<>( repository.listPackages() );

        Iterator<SerializableMetadata> it = items.iterator();
        while ( it.hasNext() )
        {
            DefaultPackageMetadata pm = gson.fromJson( it.next().serialize(), DefaultPackageMetadata.class );
            if ( !component.equals( pm.getComponent() ) || arch != pm.getArchitecture() )
            {
                it.remove();
            }
        }

        return new PackagesIndexBuilder.PackagesProvider()
        {
            @Override
            public List<SerializableMetadata> getPackages()
            {
                return items;
            }
        };
    }


    /**
     * Creates packages provider that fetches packages from meta data store of the supplied context. Packages in meta
     * data store are filtered by component and architecture.
     *
     * @param context context to use to create meta data store
     * @param component component in which packages should be located
     * @param arch architecture of the packages
     * @return
     * @throws IOException
     */
    public PackagesIndexBuilder.PackagesProvider create( KurjunContext context, String component, Architecture arch )
            throws IOException
    {
        List<SerializableMetadata> items = new LinkedList<>();

        PackageMetadataStore metadataStore = metadataStoreFactory.create( context );

        MetadataListing list = metadataStore.list();
        items.addAll( filterMetadata( component, arch, list ) );
        while ( list.isTruncated() )
        {
            list = metadataStore.listNextBatch( list );
            items.addAll( filterMetadata( component, arch, list ) );
        }

        return new PackagesIndexBuilder.PackagesProvider()
        {
            @Override
            public List<SerializableMetadata> getPackages()
            {
                return items;
            }
        };
    }


    private List<DefaultPackageMetadata> filterMetadata( String component, Architecture arch, MetadataListing ls )
    {
        List<DefaultPackageMetadata> res = new LinkedList<>();
        try
        {
            for ( SerializableMetadata m : ls.getPackageMetadata() )
            {
                DefaultPackageMetadata pm = gson.fromJson( m.serialize(), DefaultPackageMetadata.class );
                if ( component.equals( pm.getComponent() ) && arch == pm.getArchitecture() )
                {
                    res.add( pm );
                }
            }
        }
        catch ( JsonSyntaxException ex )
        {
            LOGGER.error( "Metadata is not a Debian package data.", ex );
        }
        return res;
    }

}

