package ai.subut.kurjun.http.local;


import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.common.service.PropertyKey;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


@Singleton
@MultipartConfig
class KurjunAptRepoUploadServlet extends HttpServletBase
{

    private static final String DEB_PACKAGE_PART = "package";

    @Inject
    private KurjunProperties properties;

    @Inject
    private FileStoreFactory fileStoreFactory;

    @Inject
    private RepositoryFactory repositoryFactory;

    private LocalRepository repository;


    @Override
    public void init() throws ServletException
    {
        String parentDir = properties.get( PropertyKey.FILE_SYSTEM_PARENT_DIR );
        FileStore fileStore = fileStoreFactory.createFileSystemFileStore( parentDir );

        repository = repositoryFactory.createLocalKurjun( fileStore );
    }


    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( !ServletUtils.isMultipart( req ) )
        {
            badRequest( resp, "Not a multipart request" );
            return;
        }

        ServletUtils.setMultipartConfig( req, this.getClass() );

        Part part = req.getPart( DEB_PACKAGE_PART );
        if ( part != null )
        {
            try ( InputStream is = part.getInputStream() )
            {
                repository.put( is );
            }
        }
        else
        {
            String msg = String.format( "No package attached with name '%s'", DEB_PACKAGE_PART );
            badRequest( resp, msg );
        }
    }

}

