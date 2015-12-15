package ai.subut.kurjun.repo.util;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.repository.Repository;
import ai.subut.kurjun.repo.service.PackagesIndexBuilder;


/**
 * Factory interface to create apt index file builders like release index file or packages index file.
 *
 */
public interface AptIndexBuilderFactory
{

    /**
     * Creates packages index file builder for the supplied context.
     *
     * @param context
     * @return
     */
    PackagesIndexBuilder createPackagesIndexBuilder( KurjunContext context );


    /**
     * Created release index builder for the supplied context.
     *
     * @param repository repository to build release index for
     * @param context
     * @return release index builder
     */
    ReleaseIndexBuilder createReleaseIndexBuilder( Repository repository, KurjunContext context );

}

