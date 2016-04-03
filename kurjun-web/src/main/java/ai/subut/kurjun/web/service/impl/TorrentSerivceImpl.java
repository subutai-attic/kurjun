package ai.subut.kurjun.web.service.impl;


import java.io.InputStream;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.api.torrent.TorrentDAO;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.web.service.TorrentService;


@Singleton
public class TorrentSerivceImpl implements TorrentService
{
    @Inject
    TorrentDAO torrentDAO;


    @Override
    public InputStream getTorrent( final String id )
    {
        return null;
    }


    @Override
    public Metadata add( final String id )
    {
        return null;
    }


    @Override
    public int delete( final String id )
    {
        return 0;
    }


    @Override
    public void update( final String id )
    {

    }
}
