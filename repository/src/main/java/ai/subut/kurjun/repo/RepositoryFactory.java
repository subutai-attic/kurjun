package ai.subut.kurjun.repo;


import java.net.URL;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.annotation.Nullable;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.RemoteRepository;
import ai.subut.kurjun.model.repository.PackageType;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.model.security.Identity;


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
    RemoteRepository createNonLocalSnap( String url, @Nullable Identity identity );


    /**
     * Creates non-local template repository at specified URL.
     *
     * @param url URL to remote repository
     * @param identity identity to be used for requests for remote repo, maybe {@code null}
     * @param kurjunContext kurjun context
     * @param token access token to the remote repository
     * @return non-local template repository
     */
    @Named( PackageType.SUBUTAI )
    RemoteRepository createNonLocalTemplate( @Assisted( "url" ) String url, @Nullable Identity identity,
                                             @Assisted( "context" ) String kurjunContext, @Assisted( "token" ) @Nullable String token );


    /**
     * Creates non-local virtual apt repository at specified URL.
     *
     * @param url URL to remote repository
     * @return non-local template repository
     */
    @Named( PackageType.DEB )
    RemoteRepository createNonLocalApt( URL url );


    /**
     * Creates unified repository at specified URL.
     *
     * @return
     */
    UnifiedRepository createUnifiedRepo();

}
