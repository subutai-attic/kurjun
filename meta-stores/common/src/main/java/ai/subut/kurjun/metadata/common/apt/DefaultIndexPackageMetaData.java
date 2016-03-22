package ai.subut.kurjun.metadata.common.apt;


import java.util.Arrays;
import java.util.List;

import ai.subut.kurjun.model.index.IndexPackageMetaData;
import ai.subut.kurjun.model.index.TagItem;


/**
 * Simple POJO implementation of {@link IndexPackageMetaData}.
 *
 */
public class DefaultIndexPackageMetaData extends DefaultPackageMetadata implements IndexPackageMetaData
{

    private byte[] sha1;
    private byte[] sha256;
    private long size;
    private byte[] descriptionMd5;
    private List<TagItem> tag;


    @Override
    public byte[] getSHA1()
    {
        return sha1 != null ? Arrays.copyOf( sha1, sha1.length ) : null;
    }


    public void setSha1( byte[] sha1 )
    {
        this.sha1 = sha1;
    }


    @Override
    public byte[] getSHA256()
    {
        return sha256 != null ? Arrays.copyOf( sha256, sha256.length ) : null;
    }


    public void setSha256( byte[] sha256 )
    {
        this.sha256 = sha256;
    }


    @Override
    public long getSize()
    {
        return size;
    }


    public void setSize( long size )
    {
        this.size = size;
    }


    @Override
    public byte[] getDescriptionMd5()
    {
        return descriptionMd5;
    }


    public void setDescriptionMd5( byte[] descriptionMd5 )
    {
        this.descriptionMd5 = descriptionMd5;
    }


    @Override
    public List<TagItem> getTag()
    {
        return tag;
    }


    public void setTag( List<TagItem> tag )
    {
        this.tag = tag;
    }


}

