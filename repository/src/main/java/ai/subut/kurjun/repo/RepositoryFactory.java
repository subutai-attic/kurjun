package ai.subut.kurjun.repo;


import com.google.inject.name.Named;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.repository.LocalRepository;


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
     * Creates virtual repository for the supplied context.
     *
     * @param kurjunContext context
     * @return Kurjun local apt repository
     */
    @Named( RepositoryModule.LOCAL_KURJUN )
    LocalRepository createLocalKurjun( KurjunContext kurjunContext );

}

