package ai.subut.kurjun.web.service;


import java.io.File;
import java.util.List;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ninja.Renderable;


public interface RawManagerService extends BaseService
{
    String md5();

    Renderable getFile( String repository, String md5 );

    Renderable getFile( String md5, boolean isKurjun );

    boolean delete(UserSession userSession, String repository, String md5 );

    Renderable getFile( String name );

    SerializableMetadata getInfo( String md5 );

    SerializableMetadata getInfo(Metadata metadata );

    Metadata put(UserSession userSession, File file );

    Metadata put(UserSession userSession, File file, String repository );

    List<SerializableMetadata> list( String repository );

    Metadata put(UserSession userSession, final File file, final String filename, final String repository );
}
