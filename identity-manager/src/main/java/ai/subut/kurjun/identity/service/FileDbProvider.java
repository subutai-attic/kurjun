package ai.subut.kurjun.identity.service;


import ai.subut.kurjun.db.file.FileDb;


/**
 *
 */
public interface FileDbProvider
{
    //*******************************
    FileDb get( boolean readOnly );
}
