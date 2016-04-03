package ai.subut.kurjun.web.service;


import java.io.InputStream;

import ai.subut.kurjun.model.metadata.Metadata;


public interface TorrentService
{
    InputStream getTorrent( String id );

    Metadata add( String id );

    int delete( String id );

    void update( String id );

}
