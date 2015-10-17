package ai.subut.kurjun.http.local;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.metadata.MetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.repo.service.PackageFilenameBuilder;
import ai.subut.kurjun.repo.service.PackageFilenameParser;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


@Singleton
class KurjunAptPoolServlet extends HttpServletBase
{

    @Inject
    private PackageMetadataStoreFactory metadataStoreFactory;

    @Inject
    private FileStoreFactory fileStoreFactory;

    @Inject
    private PackageFilenameParser filenameParser;

    @Inject
    private PackageFilenameBuilder filenameBuilder;

    @Inject
    private Gson gson;


    private KurjunContext context;


    @Override
    public void init() throws ServletException
    {
        this.context = HttpServer.CONTEXT;
    }


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        String path = "/pool" + req.getPathInfo();
        String packageName = filenameParser.getPackageFromFilename( path );
        String version = filenameParser.getVersionFromFilename( path );
        if ( packageName == null || version == null )
        {
            badRequest( resp, "Invalid pool path" );
            return;
        }

        PackageMetadataStore metadataStore = metadataStoreFactory.create( context );

        MetadataListing list = metadataStore.list();
        PackageMetadata metadata = findMetadata( packageName, version, list.getPackageMetadata() );

        while ( metadata == null && list.isTruncated() )
        {
            list = metadataStore.listNextBatch( list );
            metadata = findMetadata( packageName, version, list.getPackageMetadata() );
        }

        if ( metadata != null )
        {
            FileStore fileStore = fileStoreFactory.create( context );
            try ( InputStream is = fileStore.get( metadata.getMd5Sum() ) )
            {
                if ( is != null )
                {
                    String filename = filenameBuilder.makePackageFilename( metadata );
                    resp.setHeader( "Content-Disposition", "attachment; filename=" + filename );
                    IOUtils.copy( is, resp.getOutputStream() );
                }
                else
                {
                    notFound( resp, "Package file not found" );
                }
            }
        }
        else
        {
            notFound( resp, "Package not found" );
        }
    }


    private PackageMetadata findMetadata( String packageName, String version, Collection<SerializableMetadata> ls )
    {
        for ( SerializableMetadata m : ls )
        {
            if ( m.getName().equals( packageName ) && m.getVersion().equals( version ) )
            {
                return gson.fromJson( m.serialize(), DefaultPackageMetadata.class );
            }
        }
        return null;
    }


}
