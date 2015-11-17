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
import ai.subut.kurjun.subutai.service.SubutaiTemplateParser;


/**
 * Implementation of {@link RepositoryFactory}. Used in Blueprint config file.
 *
 */
public class RepositoryFactoryImpl implements RepositoryFactory
{
    private ReleaseIndexParser releaseIndexParser;
    private ControlFileParser controlFileParser;
    private SnapMetadataParser snapParser;
    private SubutaiTemplateParser templateParser;
    private FileStoreFactory fileStoreFactory;
    private PackageMetadataStoreFactory metadataStoreFactory;
    private PackageCache cache;


    public RepositoryFactoryImpl(
            ReleaseIndexParser releaseIndexParser,
            ControlFileParser controlFileParser,
            SnapMetadataParser snapMetadataParser,
            SubutaiTemplateParser templateParser,
            FileStoreFactory fileStoreFactory,
            PackageMetadataStoreFactory metadataStoreFactory,
            PackageCache cache
    )
    {
        this.releaseIndexParser = releaseIndexParser;
        this.controlFileParser = controlFileParser;
        this.snapParser = snapMetadataParser;
        this.templateParser = templateParser;
        this.fileStoreFactory = fileStoreFactory;
        this.metadataStoreFactory = metadataStoreFactory;
        this.cache = cache;
    }


    @Override
    public LocalRepository createLocalAptWrapper( String baseDirectory )
    {
        return new LocalAptRepositoryWrapper( releaseIndexParser, baseDirectory );
    }


    @Override
    public LocalRepository createLocalApt( KurjunContext context )
    {
        return new LocalAptRepository( controlFileParser, templateParser, fileStoreFactory, metadataStoreFactory,
                                       context );
    }


    @Override
    public LocalRepository createLocalSnap( KurjunContext context )
    {
        return new LocalSnapRepository( metadataStoreFactory, fileStoreFactory, snapParser, context );
    }


    @Override
    public LocalRepository createLocalTemplate( KurjunContext context )
    {
        return new LocalTemplateRepository( metadataStoreFactory, fileStoreFactory, templateParser, context );
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

