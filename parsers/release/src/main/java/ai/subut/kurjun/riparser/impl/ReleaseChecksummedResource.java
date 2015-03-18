package ai.subut.kurjun.riparser.impl;


import java.nio.charset.StandardCharsets;
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


    @Override
    public byte[] getChecksum( Checksum type )
    {
        String checksum = checksums.get( type );
        if ( checksum != null )
        {
            return checksum.getBytes( StandardCharsets.UTF_8 );
        }
        else
        {
            return null;
        }
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

