package ai.subut.kurjun.web.service;


import java.io.File;
import java.util.List;

import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ninja.Renderable;


public interface RawManagerService
{
    Renderable getFile( byte[] md5 );

    Renderable getFile( byte[] md5, boolean isKurjun );

    boolean delete( byte[] md5 );

    Renderable getFile( String name );

    SerializableMetadata getInfo( byte[] md5 );

    boolean put( File file );

    boolean put( File file, String repository );

    List<SerializableMetadata> list();
}
