package ai.subut.kurjun.web.service;


import java.io.File;
import java.util.List;

import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ninja.Renderable;


public interface RawManagerService
{
    String md5();

    Renderable getFile( byte[] md5 );

    Renderable getFile( byte[] md5, boolean isKurjun );

    boolean delete( byte[] md5 );

    Renderable getFile( String name );

    SerializableMetadata getInfo( byte[] md5 );

    Metadata put( File file );

    Metadata put( File file, String repository );

    List<SerializableMetadata> list();

    Metadata put( final File file, final String filename, final String repository );
}
