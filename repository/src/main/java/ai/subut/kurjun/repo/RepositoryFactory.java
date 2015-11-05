package ai.subut.kurjun.repo;


import com.google.inject.name.Named;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.PackageType;


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
    @Named( "NONVIRTUAL_APT_REPO" )
    LocalRepository createLocal( String baseDirectory );


    /**
     * Creates virtual repository for the supplied context.
     *
     * @param context context
     * @return Kurjun local apt repository
     */
    @Named( PackageType.DEB )
    LocalRepository createLocalApt( KurjunContext context );


    @Named( PackageType.SNAP )
    LocalRepository createLocalSnap( KurjunContext context );
}

