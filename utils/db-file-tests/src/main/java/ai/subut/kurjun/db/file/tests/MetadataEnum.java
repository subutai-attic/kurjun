package ai.subut.kurjun.db.file.tests;


import java.util.function.Supplier;

import org.apache.commons.lang.math.RandomUtils;

import ai.subut.kurjun.metadata.common.apt.DefaultIndexPackageMetaData;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.metadata.common.raw.RawMetadata;
import ai.subut.kurjun.metadata.common.snap.DefaultSnapMetadata;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


/**
 * Document me!
 */
public enum MetadataEnum
{
    RAW( RawMetadata.class, "rawFiles" ),
    TEMPLATE( DefaultTemplate.class, "subutaiTemplates" ),
    PACKAGE( DefaultPackageMetadata.class, "aptPackages" ),
    SNAP( DefaultSnapMetadata.class, "snapPackages" );

    private final Class<? extends SerializableMetadata> metadataClass;
    private final String mapName;


    MetadataEnum( Class<? extends SerializableMetadata> metadataClass, String mapName )
    {
        this.metadataClass = metadataClass;
        this.mapName = mapName;
    }


    public static MetadataEnum getRandom()
    {
        final int index = RandomUtils.nextInt() % 3;
        return MetadataEnum.values()[index];
    }


    public Supplier<SerializableMetadata> getSupplier()
    {
        return new Supplier<SerializableMetadata>()
        {
            @Override
            public SerializableMetadata get()
            {
                RandomMetaData randomMetaData = new RandomMetaData();

                if ( metadataClass.equals( RawMetadata.class ) )
                {
                    return randomMetaData.generateRawMetaData();
                }

                if ( metadataClass.equals( DefaultTemplate.class ) )
                {
                    return randomMetaData.generateTemplateMeta();
                }

                if ( metadataClass.equals( DefaultPackageMetadata.class ) )
                {
                    return randomMetaData.generatePackageMetaData();
                }

                if ( metadataClass.equals( DefaultIndexPackageMetaData.class ) )
                {
                    return KurjunRandom.defaultIndexPackageMetaData();
                }

                if ( metadataClass.equals( DefaultSnapMetadata.class ) )
                {
                    return randomMetaData.generateSnapMetaData();
                }

                throw new IllegalStateException( "Should not get here." );
            }
        };
    }


    public static MetadataEnum get( final Class metadataClass )
    {
        if ( metadataClass.isAssignableFrom( RawMetadata.class ) )
        {
            return RAW;
        }

        if ( metadataClass.isAssignableFrom( DefaultTemplate.class ) )
        {
            return TEMPLATE;
        }

        if ( metadataClass.isAssignableFrom( DefaultPackageMetadata.class ) )
        {
            return PACKAGE;
        }

        throw new IllegalArgumentException( metadataClass.getCanonicalName() + " is not SerializableMetadata" );
    }


    public String getMapName()
    {
        return mapName;
    }
}
