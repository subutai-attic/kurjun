package ai.subut.kurjun.web.service;


import ai.subut.kurjun.model.metadata.Metadata;
import ninja.Renderable;


public interface TorrentService
{

    Renderable getTorrent( String id );

    Metadata add( String id );

    int delete( String id );

    void update( String id );
}
