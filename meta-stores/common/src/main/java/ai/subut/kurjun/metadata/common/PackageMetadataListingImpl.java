package ai.subut.kurjun.metadata.common;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.metadata.PackageMetadataListing;


public class PackageMetadataListingImpl implements PackageMetadataListing
{
    private List<PackageMetadata> metadata = new LinkedList<>();
    private Object marker;
    private boolean truncated;


    @Override
    public Collection<PackageMetadata> getPackageMetadata()
    {
        return metadata;
    }


    @Override
    public Object getMarker()
    {
        return marker;
    }


    public void setMarker( Object marker )
    {
        this.marker = marker;
    }


    @Override
    public boolean isTruncated()
    {
        return truncated;
    }


    public void setTruncated( boolean truncated )
    {
        this.truncated = truncated;
    }


}

