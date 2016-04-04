package ai.subut.kurjun.web.service.impl;


import ai.subut.kurjun.model.metadata.Metadata;
import com.google.inject.Inject;

import ai.subut.kurjun.storage.factory.FileStoreFactory;
import ai.subut.kurjun.web.service.TorrentService;
import ninja.Renderable;


public class TorrentServiceImpl implements TorrentService
{

    @Inject
    FileStoreFactory fileStoreFactory;
    private static final String SUBUTAI_CONTEXT = "subutai";


//    @Override
    public void createTorrentFiles()
    {

    }

    @Override
    public Renderable getTorrent(String id) {
        return null;
    }

    @Override
    public Metadata add(String id) {
        return null;
    }

    @Override
    public int delete(String id) {
        return 0;
    }

    @Override
    public void update(String id) {

    }
}
