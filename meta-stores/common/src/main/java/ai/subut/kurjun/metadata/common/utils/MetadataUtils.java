package ai.subut.kurjun.metadata.common.utils;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ai.subut.kurjun.metadata.common.apt.DefaultDependency;
import ai.subut.kurjun.metadata.common.apt.DefaultIndexPackageMetaData;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.metadata.common.snap.DefaultSnapMetadata;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.model.index.IndexPackageMetaData;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;


public class MetadataUtils
{

    public static final Gson JSON;

    static
    {
        GsonBuilder gb = new GsonBuilder();

        // register Dependency type adapter for correct deserialization of fields like List<Dependency>
        gb.registerTypeAdapter( Dependency.class, new DependencyTypeAdapter() );
        //gb.registerTypeAdapter( Framework.class, new DefaultFramework() );

        JSON = gb.create();
    }

    private MetadataUtils()
    {
        // utility class
    }


    /**
     * Makes a comparator that compares meta data instances by their versions. Returned comparator sorts items in
     * ascending order by version values and takes into account null versions.
     */
    public static Comparator<Metadata> makeVersionComparator()
    {
        return ( Metadata m1, Metadata m2 ) -> {
            if ( m1.getVersion() != null )
            {
                return m2.getVersion() != null ? m1.getVersion().compareTo( m2.getVersion() ) : 1;
            }
            else
            {
                // m1 version is null here
                return m2.getVersion() == null ? 0 : -1;
            }
        };
    }


    /**
     * Converts supplied package meta data instance to a serializable form. Package metadata implementations may not be
     * serializable. This method ensures that the returned instance is serializable.
     *
     * @param meta meta data to convert
     *
     * @return serializable instance of the supplied meta data
     */
    public static DefaultPackageMetadata serializablePackageMetadata( PackageMetadata meta )
    {
        if ( meta instanceof DefaultPackageMetadata )
        {
            return ( DefaultPackageMetadata ) meta;
        }

        DefaultPackageMetadata result = new DefaultPackageMetadata();
        copyPackageMetadata( meta, result );
        return result;
    }


    /**
     * Converts supplied package meta data instance to a serializable form. Package metadata implementations may not be
     * serializable. This method ensures that the returned instance is serializable.
     *
     * @param meta meta data to convert
     *
     * @return serializable instance of the supplied meta data
     */
    public static DefaultIndexPackageMetaData serializableIndexPackageMetadata( IndexPackageMetaData meta )
    {
        if ( meta instanceof DefaultIndexPackageMetaData )
        {
            return ( DefaultIndexPackageMetaData ) meta;
        }

        DefaultIndexPackageMetaData result = new DefaultIndexPackageMetaData();
        copyPackageMetadata( meta, result );

        result.setSha1( meta.getSHA1() );
        result.setSha256( meta.getSHA256() );
        result.setSize( meta.getSize() );
        result.setDescriptionMd5( meta.getDescriptionMd5() );
        result.setTag( meta.getTag() );

        return result;
    }


    /**
     * Converts supplied snap meta data to its serializable meta data form.
     *
     * @param meta snap meta data to convert
     *
     * @return serializable meta data
     */
    public static DefaultSnapMetadata serializableSnapMetadata( SnapMetadata meta )
    {
        if ( meta instanceof DefaultSnapMetadata )
        {
            return ( DefaultSnapMetadata ) meta;
        }
        DefaultSnapMetadata m = new DefaultSnapMetadata();
        m.setMd5Sum( meta.getMd5Sum() );
        m.setName( meta.getName() );
        m.setVersion( meta.getVersion() );
        m.setVendor( meta.getVendor() );
        m.setSource( meta.getSource() );
        m.setFrameworks( meta.getFrameworks() );
        return m;
    }


    /**
     * Converts supplied Subutai template metadata into its serializable form.
     *
     * @param metadata meta data to convert
     *
     * @return serializable form of meta data
     */
    public static DefaultTemplate serializableTemplateMetadata( SubutaiTemplateMetadata metadata )
    {
        if ( metadata instanceof DefaultTemplate )
        {
            return ( DefaultTemplate ) metadata;
        }
        DefaultTemplate m = new DefaultTemplate();
        m.setId( metadata.getOwnerFprint(), metadata.getMd5Sum() );
        m.setMd5Sum( metadata.getMd5Sum() );
        m.setName( metadata.getName() );
        m.setVersion( metadata.getVersion() );
        m.setParent( metadata.getParent() );
        m.setPackage( metadata.getPackage() );
        m.setArchitecture( metadata.getArchitecture() );
        m.setOwnerFprint( metadata.getOwnerFprint() );
        m.setConfigContents( metadata.getConfigContents() );
        m.setPackagesContents( metadata.getPackagesContents() );
        m.setExtra( metadata.getExtra() );
        m.setSize( metadata.getSize() );
        m.setAlias( metadata.getAlias() );
        m.setDateAdded( metadata.getDateAdded() );
        return m;
    }


    public static Map<String, String> makeParamsMap( Metadata metadata )
    {
        Map<String, String> params = new HashMap<>();
        if ( metadata.getId() != null )
        {
            params.put( "id", String.valueOf( metadata.getId() ) );
        }
        if ( metadata.getMd5Sum() != null )
        {
            params.put( "md5", metadata.getMd5Sum() );
        }
        if ( metadata.getName() != null )
        {
            params.put( "name", metadata.getName() );
        }
        if ( metadata.getVersion() != null )
        {
            params.put( "version", metadata.getVersion() );
        }
        return params;
    }


    private static void copyPackageMetadata( PackageMetadata source, DefaultPackageMetadata target )
    {
        target.setMd5( source.getMd5Sum() );
        target.setComponent( source.getComponent() );
        target.setFilename( source.getFilename() );
        target.setPackage( source.getPackage() );
        target.setVersion( source.getVersion() );
        target.setSource( source.getSource() );
        target.setMaintainer( source.getMaintainer() );
        target.setArchitecture( source.getArchitecture() );
        target.setInstalledSize( source.getInstalledSize() );
        target.setDependencies( cloneDependencies( source.getDependencies() ) );
        target.setRecommends( cloneDependencies( source.getRecommends() ) );
        target.setSuggests( cloneDependencies( source.getSuggests() ) );
        target.setEnhances( cloneDependencies( source.getEnhances() ) );
        target.setPreDepends( cloneDependencies( source.getPreDepends() ) );
        target.setConflicts( cloneDependencies( source.getConflicts() ) );
        target.setBreaks( cloneDependencies( source.getBreaks() ) );
        target.setReplaces( cloneDependencies( source.getReplaces() ) );
        target.setProvides( source.getProvides() != null ? new ArrayList<>( source.getProvides() ) : null );
        target.setSection( source.getSection() );
        target.setPriority( source.getPriority() );
        target.setHomepage( source.getHomepage() );
        target.setDescription( source.getDescription() );

        if ( source instanceof DefaultPackageMetadata )
        {
            target.getExtra().putAll( ( ( DefaultPackageMetadata ) source ).getExtra() );
        }
    }


    private static List<Dependency> cloneDependencies( List<Dependency> dependencies )
    {
        if ( dependencies == null )
        {
            return null;
        }

        List<Dependency> result = new ArrayList<>();
        for ( Dependency dependency : dependencies )
        {
            DefaultDependency dep = new DefaultDependency();
            dep.setPackage( dependency.getPackage() );
            dep.setVersion( dependency.getVersion() );
            dep.setDependencyOperator( dependency.getDependencyOperator() );
            dep.setAlternatives( cloneDependencies( dependency.getAlternatives() ) );
            result.add( dep );
        }
        return result;
    }
}

