package ai.subut.kurjun.db.file;


import java.net.MalformedURLException;
import java.util.UUID;

import com.google.common.collect.Lists;

import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.metadata.common.raw.RawMetadata;
import ai.subut.kurjun.metadata.common.snap.DefaultSnapMetadata;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.apt.Priority;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;


public class RandomMetaData
{
    public DefaultTemplate generateTemplateMeta()
    {
        DefaultTemplate template = new DefaultTemplate();

        template.setMd5Sum( MetaDataConfiguration.md5() );
        template.setName( UUID.randomUUID().toString() );
        template.setVersion( UUID.randomUUID().toString() );
        template.setParent( "master" );
        template.setPackage( UUID.randomUUID().toString() );
        template.setArchitecture( Architecture.getRandom() );
        template.setConfigContents( MetaDataConfiguration.config() );
        template.setPackagesContents( MetaDataConfiguration.packages() );
        template.setSize( MetaDataConfiguration.size() );
        template.setOwnerFprint( MetaDataConfiguration.fingerprint() );
        template.setExtra( MetaDataConfiguration.extra() );

        return template;
    }


    public DefaultPackageMetadata generatePackageMetaData()
    {
        DefaultPackageMetadata packageMetadata = new DefaultPackageMetadata();

        packageMetadata.setMd5( MetaDataConfiguration.md5() );
        packageMetadata.setComponent( UUID.randomUUID().toString() );
        packageMetadata.setFilename( UUID.randomUUID().toString() );
        packageMetadata.setPackage( UUID.randomUUID().toString() );
        packageMetadata.setVersion( UUID.randomUUID().toString() );
        packageMetadata.setSource( UUID.randomUUID().toString() );
        packageMetadata.setMaintainer( UUID.randomUUID().toString() );
        packageMetadata.setArchitecture( Architecture.getRandom() );
        packageMetadata.setInstalledSize( ( int ) MetaDataConfiguration.size() );
        packageMetadata.setDependencies( Lists.newArrayList() );
        packageMetadata.setRecommends( Lists.newArrayList() );
        packageMetadata.setSuggests( Lists.newArrayList() );
        packageMetadata.setEnhances( Lists.newArrayList() );
        packageMetadata.setPreDepends( Lists.newArrayList() );
        packageMetadata.setConflicts( Lists.newArrayList() );
        packageMetadata.setBreaks( Lists.newArrayList() );
        packageMetadata.setReplaces( Lists.newArrayList() );
        packageMetadata.setProvides( Lists.newArrayList() );
        packageMetadata.setSection( UUID.randomUUID().toString() );
        packageMetadata.setPriority( Priority.getRandom() );
        packageMetadata.setDescription( "Some description" );

        try
        {
            packageMetadata.setHomepage( MetaDataConfiguration.url()  );
        }
        catch ( MalformedURLException e )
        {
            e.printStackTrace();
        }

        return packageMetadata;
    }


    public RawMetadata generateRawMetaData()
    {
        RawMetadata rawMetadata = new RawMetadata();

        rawMetadata.setMd5Sum( MetaDataConfiguration.md5() );
        rawMetadata.setName( UUID.randomUUID().toString() );
        rawMetadata.setSize( MetaDataConfiguration.size() );
        rawMetadata.setFingerprint( MetaDataConfiguration.fingerprint() );

        return rawMetadata;
    }


    public SnapMetadata generateSnapMetaData()
    {
        DefaultSnapMetadata snapMetadata = new DefaultSnapMetadata();

        snapMetadata.setName( UUID.randomUUID().toString() );
        snapMetadata.setMd5Sum( MetaDataConfiguration.md5() );
        snapMetadata.setVersion( UUID.randomUUID().toString() );
        snapMetadata.setVendor( UUID.randomUUID().toString() );
        snapMetadata.setSource( UUID.randomUUID().toString() );
        snapMetadata.setFrameworks( MetaDataConfiguration.frameworks() );

        return snapMetadata;
    }
}
