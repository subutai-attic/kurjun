package ai.subut.kurjun.index;


import org.vafer.jdeb.debian.ControlField;
import org.vafer.jdeb.debian.ControlFile;

import ai.subut.kurjun.model.index.IndexPackageMetaData;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;


public class PackageIndexFieldsParser extends ControlFile
{

    public static final ControlField[] FIELDS =
    {
        new ControlField( PackageMetadata.PACKAGE_FIELD, true ),
        new ControlField( PackageMetadata.VERSION_FIELD, true ),
        new ControlField( PackageMetadata.SECTION_FIELD, true ),
        new ControlField( PackageMetadata.PRIORITY_FIELD, true ),
        new ControlField( PackageMetadata.ARCHITECTURE_FIELD, true ),
        new ControlField( PackageMetadata.DEPENDS_FIELD ),
        new ControlField( PackageMetadata.PRE_DEPENDS_FIELD ),
        new ControlField( PackageMetadata.RECOMMENDS_FIELD ),
        new ControlField( PackageMetadata.SUGGESTS_FIELD ),
        new ControlField( PackageMetadata.BREAKS_FIELD ),
        new ControlField( PackageMetadata.ENHANCES_FIELD ),
        new ControlField( PackageMetadata.CONFLICTS_FIELD ),
        new ControlField( PackageMetadata.PROVIDES_FIELD ),
        new ControlField( PackageMetadata.REPLACES_FIELD ),
        new ControlField( PackageMetadata.INSTALLED_SIZE_FIELD ),
        new ControlField( PackageMetadata.MAINTAINER_FIELD, true ),
        new ControlField( PackageMetadata.DESCRIPTION_FIELD, true, ControlField.Type.MULTILINE ),
        new ControlField( PackageMetadata.HOMEPAGE_FIELD ),
        //
        new ControlField( IndexPackageMetaData.FILENAME_FIELD, true ),
        new ControlField( IndexPackageMetaData.SIZE_FIELD, true ),
        new ControlField( IndexPackageMetaData.MD5SUM_FIELD, true ),
        new ControlField( IndexPackageMetaData.SHA1_FIELD, true ),
        new ControlField( IndexPackageMetaData.SHA256_FIELD, true ),
        new ControlField( IndexPackageMetaData.DESCRIPTION_MD5_FIELD ),
    };


    @Override
    protected ControlField[] getFields()
    {
        return FIELDS;
    }


    @Override
    protected char getUserDefinedFieldLetter()
    {
        return 'B';
    }

}

