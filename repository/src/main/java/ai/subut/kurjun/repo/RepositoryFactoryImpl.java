package ai.subut.kurjun.repo;


import java.net.URL;

import com.google.gson.Gson;

import ai.subut.kurjun.cfparser.service.ControlFileParser;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.RemoteRepository;
import ai.subut.kurjun.model.repository.UnifiedRepository;

import ai.subut.kurjun.repo.cache.PackageCache;
import ai.subut.kurjun.repo.util.http.WebClientFactory;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;
import ai.subut.kurjun.snap.service.SnapMetadataParser;
import ai.subut.kurjun.storage.factory.FileStoreFactory;
import ai.subut.kurjun.subutai.service.SubutaiTemplateParser;


/**
 * Implementation of {@link RepositoryFactory}. Used in Blueprint config file.
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
    private WebClientFactory webClientFactory;
    private Gson gson;


    public RepositoryFactoryImpl( ReleaseIndexParser releaseIndexParser, ControlFileParser controlFileParser,
                                  SnapMetadataParser snapMetadataParser, SubutaiTemplateParser templateParser,
                                  FileStoreFactory fileStoreFactory, PackageMetadataStoreFactory metadataStoreFactory,
                                  PackageCache cache, final WebClientFactory webClientFactory, final Gson gson )
    {
        this.releaseIndexParser = releaseIndexParser;
        this.controlFileParser = controlFileParser;
        this.snapParser = snapMetadataParser;
        this.templateParser = templateParser;
        this.fileStoreFactory = fileStoreFactory;
        this.metadataStoreFactory = metadataStoreFactory;
        this.cache = cache;
        this.webClientFactory = webClientFactory;
        this.gson = gson;
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
    public LocalRawRepository createLocalRaw( KurjunContext context )
    {
        return new LocalRawRepository( metadataStoreFactory, fileStoreFactory, context );
    }


    @Override
    public LocalRepository createLocalTemplate( KurjunContext context )
    {
        return new LocalTemplateRepository( metadataStoreFactory, fileStoreFactory, templateParser, context );
    }


    @Override
    public RemoteRepository createNonLocalSnap( String url, User identity )
    {
        return new RemoteSnapRepository( cache, url, identity );
    }


    @Override
    public RemoteRawRepository createNonLocalRaw( String url, User identity )
    {
        return new RemoteRawRepository( cache, webClientFactory, url, identity );
    }


    @Override
    public RemoteRepository createNonLocalTemplate( String url, User identity, String kurjunContext, String token )
    {
        return new RemoteTemplateRepository( cache, webClientFactory, gson, url, identity, kurjunContext, token );
    }


    @Override
    public RemoteRepository createNonLocalApt( URL url )
    {
        RemoteAptRepository repo = new RemoteAptRepository( url, webClientFactory );
        repo.releaseIndexParser = releaseIndexParser;
        repo.packagesIndexParser = null; // TODO: 
        repo.cache = cache;
        return repo;
    }


    @Override
    public UnifiedRepository createUnifiedRepo()
    {
        return new UnifiedRepositoryImpl();
    }
}
