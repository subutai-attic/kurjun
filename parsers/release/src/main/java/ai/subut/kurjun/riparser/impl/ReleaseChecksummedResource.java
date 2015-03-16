/*
 * Copyright 2015 azilet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.subut.kurjun.riparser.impl;


import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import ai.subut.kurjun.model.index.Checksum;
import ai.subut.kurjun.model.index.ChecksummedResource;


public class ReleaseChecksummedResource implements ChecksummedResource
{

    String path;
    long size;
    Map<Checksum, String> checksums;


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

