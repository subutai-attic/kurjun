package ai.subut.kurjun.rest.template;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.metadata.common.utils.IdValidators;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.rest.RestManagerBase;
import io.subutai.common.protocol.TemplateKurjun;


public class RestTemplateManagerImpl extends RestManagerBase implements RestTemplateManager
{

    private static final Logger LOGGER = LoggerFactory.getLogger( RestTemplateManagerImpl.class );

    private static final Gson GSON = new GsonBuilder().create();

    private final TemplateManagerStandalone templateManager;


    public RestTemplateManagerImpl()
    {
        this.templateManager = new TemplateManagerStandalone();
        this.templateManager.init();
    }


    @Override
    public Response getRepositories()
    {
        Set<String> list = templateManager.getRepositories();
        return Response.ok( GSON.toJson( list ) ).build();
    }


    @Override
    public Response getTemplate( String repository, String id, String name, String version, String type,
                                 boolean isKurjunClient )
    {
        try
        {
            if ( id != null )
            {
                TemplateId tid = IdValidators.Template.validate( id );
                byte[] md5bytes = decodeMd5( tid.getMd5() );
                if ( md5bytes != null )
                {
                    TemplateKurjun template =
                            templateManager.getTemplate( repository, md5bytes, tid.getOwnerFprint(), isKurjunClient );
                    if ( template != null )
                    {
                        InputStream is = templateManager
                                .getTemplateData( repository, md5bytes, tid.getOwnerFprint(), isKurjunClient );
                        if ( is != null )
                        {
                            return Response.ok( is ).header( "Content-Disposition",
                                    "attachment; filename=" + makeFilename( template ) )
                                           .header( "Content-Type", "application/octet-stream" ).build();
                        }
                    }
                }
            }
            else if ( RestTemplateManager.RESPONSE_TYPE_ID.equals( type ) )
            {
                TemplateKurjun template = templateManager.getTemplate( repository, name, version, isKurjunClient );

                if ( template != null )
                {
                    return Response.ok( template.getId() ).build();
                }
            }
        }
        catch ( IllegalArgumentException ex )
        {
            LOGGER.error( "", ex );
            return badRequest( ex.getMessage() );
        }
        catch ( IOException ex )
        {
            String msg = "Failed to get template info";
            LOGGER.error( msg, ex );
            return Response.serverError().entity( msg ).build();
        }
        return packageNotFoundResponse();
    }


    @Override
    public Response getTemplateInfo( String repository, String id, String name, String version, boolean isKurjunClient )
    {
        try
        {
            if ( id != null )
            {
                TemplateId tid = IdValidators.Template.validate( id );
                byte[] md5bytes = decodeMd5( tid.getMd5() );
                if ( md5bytes != null )
                {
                    TemplateKurjun template =
                            templateManager.getTemplate( repository, md5bytes, tid.getOwnerFprint(), isKurjunClient );
                    if ( template != null )
                    {
                        return Response.ok( GSON.toJson( convertToDefaultTemplate( template ) ) ).build();
                    }
                }
            }

            TemplateKurjun template = templateManager.getTemplate( repository, name, version, isKurjunClient );

            if ( template != null )
            {
                return Response.ok( GSON.toJson( convertToDefaultTemplate( template ) ) ).build();
            }
        }
        catch ( IllegalArgumentException ex )
        {
            LOGGER.error( "", ex );
            return badRequest( ex.getMessage() );
        }
        catch ( IOException ex )
        {
            String msg = "Failed to get template info";
            LOGGER.error( msg, ex );
            return Response.serverError().entity( msg ).build();
        }
        return packageNotFoundResponse();
    }


    @Override
    public Response getTemplateList( String repository, boolean isKurjunClient )
    {
        try
        {
            List<TemplateKurjun> list = templateManager.list( repository, isKurjunClient );

            if ( list != null )
            {
                List<DefaultTemplate> deflist =
                        list.stream().map( t -> convertToDefaultTemplate( t ) ).collect( Collectors.toList() );
                return Response.ok( GSON.toJson( deflist ) ).build();
            }
        }
        catch ( IOException ex )
        {
            String msg = "Failed to get template list info";
            LOGGER.error( msg, ex );
            return Response.serverError().entity( msg ).build();
        }
        return Response.ok( "No templates" ).build();
    }


    @Override
    public Response uploadTemplate( String repository, Attachment attachment )
    {
        File temp = null;
        try
        {
            temp = Files.createTempFile( null, null ).toFile();
            attachment.transferTo( temp );

            try ( InputStream is = new FileInputStream( temp ) )
            {
                String tid = templateManager.upload( repository, is );
                if ( tid != null )
                {
                    return Response.ok( tid ).build();
                }
                else
                {
                    return Response.serverError().entity( "Failed to put template" ).build();
                }
            }
        }
        catch ( IOException ex )
        {
            String msg = "Failed to put template";
            LOGGER.error( msg, ex );
            return Response.serverError().entity( msg ).build();
        }
        finally
        {
            FileUtils.deleteQuietly( temp );
        }
    }


    @Override
    public Response deleteTemplate( String repository, String id )
    {
        try
        {
            TemplateId tid = IdValidators.Template.validate( id );
            byte[] md5bytes = decodeMd5( tid.getMd5() );
            if ( md5bytes != null )
            {
                try
                {
                    boolean deleted = templateManager.delete( repository, tid.getOwnerFprint(), md5bytes );
                    if ( deleted )
                    {
                        return Response.ok( "Template deleted" ).build();
                    }
                    else
                    {
                        return packageNotFoundResponse();
                    }
                }
                catch ( IOException ex )
                {
                    String err = "Failed to delete template";
                    LOGGER.error( err, ex );
                    return Response.serverError().entity( err ).build();
                }
            }
            return badRequest( "Invalid md5 checksum" );
        }
        catch ( IllegalArgumentException ex )
        {
            LOGGER.error( "", ex );
            return badRequest( ex.getMessage() );
        }
    }


    @Override
    public Response addRemoteRepo( final String token, final String address )
    {
        try
        {
            templateManager.addRemoteRepository( new URL( address ),token );
        }
        catch ( MalformedURLException e )
        {
            e.printStackTrace();
        }

        return null;
    }


    private DefaultTemplate convertToDefaultTemplate( TemplateKurjun template )
    {
        return convertToDefaultTemplate( template, true );
    }


    private DefaultTemplate convertToDefaultTemplate( TemplateKurjun template, boolean includeFileContents )
    {
        DefaultTemplate defaultTemplate = new DefaultTemplate();
        defaultTemplate.setOwnerFprint( template.getOwnerFprint() );
        defaultTemplate.setName( template.getName() );
        defaultTemplate.setVersion( template.getVersion() );
        defaultTemplate.setMd5Sum( decodeMd5( template.getMd5Sum() ) );
        defaultTemplate.setArchitecture( Architecture.getByValue( template.getArchitecture() ) );
        defaultTemplate.setParent( template.getParent() );
        defaultTemplate.setPackage( template.getPackageName() );
        if ( includeFileContents )
        {
            defaultTemplate.setConfigContents( template.getConfigContents() );
            defaultTemplate.setPackagesContents( template.getPackagesContents() );
        }
        return defaultTemplate;
    }


    private String makeFilename( TemplateKurjun t )
    {
        return t.getName() + "_" + t.getVersion() + ".tar.gz";
    }


    @Override
    protected Logger getLogger()
    {
        return LOGGER;
    }
}
