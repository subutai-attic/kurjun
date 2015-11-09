package ai.subut.kurjun.repo;


import ai.subut.kurjun.cfparser.service.ControlFileParser;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.repo.cache.PackageCache;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;
import ai.subut.kurjun.snap.service.SnapMetadataParser;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


/**
 * Implementation of {@link RepositoryFactory}. Used in Blueprint config file.
 *
 */
class RepositoryFactoryImpl implements RepositoryFactory
{
    private ReleaseIndexParser releaseIndexParser;
    private ControlFileParser controlFileParser;
    private SnapMetadataParser snapParser;
    private FileStoreFactory fileStoreFactory;
    private PackageMetadataStoreFactory metadataStoreFactory;
    private PackageCache cache;


    public RepositoryFactoryImpl(
            ReleaseIndexParser releaseIndexParser,
            ControlFileParser controlFileParser,
            SnapMetadataParser snapMetadataParser,
            FileStoreFactory fileStoreFactory,
            PackageMetadataStoreFactory metadataStoreFactory,
            PackageCache cache
    )
    {
        this.releaseIndexParser = releaseIndexParser;
        this.controlFileParser = controlFileParser;
        this.snapParser = snapMetadataParser;
        this.fileStoreFactory = fileStoreFactory;
        this.metadataStoreFactory = metadataStoreFactory;
        this.cache = cache;
    }


    @Override
    public LocalRepository createLocal( String baseDirectory )
    {
        return new LocalAptRepositoryWrapper( releaseIndexParser, baseDirectory );
    }


    @Override
    public LocalRepository createLocalApt( KurjunContext context )
    {
        return new LocalAptRepository( controlFileParser, fileStoreFactory, metadataStoreFactory, context );
    }


    @Override
    public LocalRepository createLocalSnap( KurjunContext context )
    {
        return new LocalSnapRepository( metadataStoreFactory, fileStoreFactory, snapParser, context );
    }


    @Override
    public NonLocalRepository createNonLocalSnap( String url, Identity identity )
    {
        return new NonLocalSnapRepository( cache, url, identity );
    }


    @Override
    public UnifiedRepository createUnifiedRepo()
    {
        return new UnifiedRepositoryImpl();
    }


}

