package ai.subut.kurjun.riparser;


import org.vafer.jdeb.debian.ControlField;
import org.vafer.jdeb.debian.ControlFile;

import ai.subut.kurjun.model.index.ReleaseFile;


class ReleaseIndexFields extends ControlFile
{

    static final ControlField[] FIELDS =
    {
        new ControlField( ReleaseFile.ORIGIN_FIELD ),
        new ControlField( ReleaseFile.LABEL_FILED ),
        new ControlField( ReleaseFile.SUITE_FILED, true ),
        new ControlField( ReleaseFile.VERSION_FILED ),
        new ControlField( ReleaseFile.CODENAME_FILED, true ),
        new ControlField( ReleaseFile.DATE_FILED ),
        new ControlField( ReleaseFile.ARCHITECTURES_FILED, true ),
        new ControlField( ReleaseFile.COMPONENTS_FILED, true ),
        new ControlField( ReleaseFile.DESCRIPTION_FILED ),
        new ControlField( ReleaseFile.MD5SUM_FILED, false, ControlField.Type.MULTILINE ),
        new ControlField( ReleaseFile.SHA1_FILED, false, ControlField.Type.MULTILINE ),
        new ControlField( ReleaseFile.SHA256_FILED, true, ControlField.Type.MULTILINE ),
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

