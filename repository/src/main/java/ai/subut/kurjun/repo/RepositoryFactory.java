package ai.subut.kurjun.repo;


import com.google.inject.name.Named;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.annotation.Nullable;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.model.repository.PackageType;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.model.security.Identity;
import java.net.URL;


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
    @Named( "APT_WRAPPER" )
    LocalRepository createLocalAptWrapper( String baseDirectory );


    /**
     * Creates virtual repository for the supplied context.
     *
     * @param context context
     * @return Kurjun local apt repository
     */
    @Named( PackageType.DEB )
    LocalRepository createLocalApt( KurjunContext context );


    /**
     * Creates local snap repository for the supplied context.
     *
     * @param context context
     * @return local snap repository
     */
    @Named( PackageType.SNAP )
    LocalRepository createLocalSnap( KurjunContext context );


    /**
     * Creates local templates repository for the supplied context.
     *
     * @param context
     * @return
     */
    @Named( PackageType.SUBUTAI )
    LocalRepository createLocalTemplate( KurjunContext context );


    /**
     * Creates non-local snap repository at specified URL.
     *
     * @param url URL to remote repository
     * @param identity identity to be used for requests for remote repo, maybe {@code null}
     * @return non-local snap repository
     */
    @Named( PackageType.SNAP )
    NonLocalRepository createNonLocalSnap( String url, @Nullable Identity identity );

    /**
     * Creates non-local template repository at specified URL.
     *
     * @param url URL to remote repository
     * @param identity identity to be used for requests for remote repo, maybe {@code null}
     * @param useToken flag to indicate where to request access token from remote url and add this token
     * to query parameters of the next request
     * @return non-local template repository
     */
    @Named( PackageType.SUBUTAI )
    NonLocalRepository createNonLocalTemplate( String url, @Nullable Identity identity, boolean useToken );
    
    /**
     * Creates non-local virtual apt repository at specified URL.
     *
     * @param url URL to remote repository
     * @return non-local template repository
     */
    @Named( PackageType.DEB )
    NonLocalRepository createNonLocalApt( URL url );


    /**
     * Creates unified repository at specified URL.
     *
     * @return
     */
    UnifiedRepository createUnifiedRepo();


}

