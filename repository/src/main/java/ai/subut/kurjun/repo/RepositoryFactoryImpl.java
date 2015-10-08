package ai.subut.kurjun.repo;


import ai.subut.kurjun.cfparser.service.ControlFileParser;
import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


/**
 * Implementation of {@link RepositoryFactory}. Used in Blueprint config file.
 *
 */
class RepositoryFactoryImpl implements RepositoryFactory
{
    private ReleaseIndexParser releaseIndexParser;
    private ControlFileParser controlFileParser;
    private FileStoreFactory fileStoreFactory;
    private PackageMetadataStoreFactory metadataStoreFactory;


    public RepositoryFactoryImpl(
            ReleaseIndexParser releaseIndexParser,
            ControlFileParser controlFileParser,
            FileStoreFactory fileStoreFactory,
            PackageMetadataStoreFactory metadataStoreFactory )
    {
        this.releaseIndexParser = releaseIndexParser;
        this.controlFileParser = controlFileParser;
        this.fileStoreFactory = fileStoreFactory;
        this.metadataStoreFactory = metadataStoreFactory;
    }


    @Override
    public LocalRepository createLocal( String baseDirectory )
    {
        return new LocalAptRepositoryImpl( releaseIndexParser, baseDirectory );
    }


    @Override
    public LocalRepository createLocalKurjun( KurjunContext kurjunContext )
    {
        return new KurjunLocalRepository( controlFileParser, fileStoreFactory, metadataStoreFactory, kurjunContext );
    }

}

