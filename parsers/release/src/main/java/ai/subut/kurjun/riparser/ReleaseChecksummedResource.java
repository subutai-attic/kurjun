package ai.subut.kurjun.riparser;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ai.subut.kurjun.model.index.Checksum;
import ai.subut.kurjun.model.index.ChecksummedResource;


public class ReleaseChecksummedResource implements ChecksummedResource
{

    final String path;
    long size;
    Map<Checksum, String> checksums = new HashMap<>();


    public ReleaseChecksummedResource( String path )
    {
        this.path = path;
    }


    @Override
    public String getRelativePath()
    {
        return path;
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


    public Map<Checksum, String> getChecksums()
    {
        return checksums;
    }


    @Override
    public String getChecksum( Checksum type )
    {
        String checksum = checksums.get( type );
        return checksum;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof ChecksummedResource )
        {
            ChecksummedResource other = ( ChecksummedResource ) obj;
            return Objects.equals( this.path, other.getRelativePath() );
        }
        return false;
    }


    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode( this.path );
        return hash;
    }
}

