package ai.subut.kurjun.web.service.impl;


import com.google.inject.Inject;

import ai.subut.kurjun.storage.factory.FileStoreFactory;
import ai.subut.kurjun.web.service.TorrentService;


public class TorrentServiceImpl implements TorrentService
{

    @Inject
    FileStoreFactory fileStoreFactory;
    private static final String SUBUTAI_CONTEXT = "subutai";


    @Override
    public void createTorrentFiles()
    {

    }
}
