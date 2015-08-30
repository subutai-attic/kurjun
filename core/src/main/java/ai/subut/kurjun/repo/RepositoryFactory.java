package ai.subut.kurjun.repo;


import com.google.inject.name.Named;

import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.storage.FileStore;


/**
 * Factory interface to create repositories.
 *
 */
public interface RepositoryFactory
{

    /**
     * Creates non-virtual local apt repository at specified base directory.
     *
     * @param baseDirectory base directory of the local repository
     * @return
     */
    @Named( RepositoryModule.LOCAL_NONVIRTUAL )
    LocalRepository createLocal( String baseDirectory );


    /**
     * Creates virtual repository where files are stored in supplied file store.
     *
     * @param fileStore file store of the repository
     * @return
     */
    @Named( RepositoryModule.LOCAL_KURJUN )
    LocalRepository createLocalKurjun( FileStore fileStore );

}

