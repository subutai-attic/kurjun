package ai.subut.kurjun.security.service;


import com.google.inject.Provider;

import ai.subut.kurjun.db.file.FileDb;


/**
 * File db provider to be used for security related stores.
 *
 */
public interface FileDbProvider extends Provider<FileDb>
{

    @Override
    FileDb get();

}

