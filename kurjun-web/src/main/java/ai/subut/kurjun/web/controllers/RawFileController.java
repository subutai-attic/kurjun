package ai.subut.kurjun.web.controllers;


import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.metadata.common.raw.RawMetadata;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.web.filter.SecurityFilter;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.model.KurjunFileItem;
import ai.subut.kurjun.web.service.RawManagerService;
import ai.subut.kurjun.web.service.RepositoryService;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.session.FlashScope;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;

import static com.google.common.base.Preconditions.checkNotNull;


@Singleton
public class RawFileController extends BaseController
{

    @Inject
    private RawManagerService rawManagerService;

    @Inject
    private RepositoryService repositoryService;


    public Result list( @Param( "repository" ) String repository )
    {
        if ( repository == null )
        {
            repository = "all";
        }

        return Results.html().template( "views/raw-files.ftl" ).render( "files", rawManagerService.list( repository ) );
    }


    @FileProvider( SubutaiFileHandler.class )
    public Result upload( Context context, @Param( "file" ) FileItem fileItem, FlashScope flashScope )
    {
        UserSession userSession = ( UserSession ) context.getAttribute( SecurityFilter.USER_SESSION );
        String fingerprint = "raw";

        //checkNotNull( fileItem, "MD5 cannot be null" );
        //if ( userSession != null && userSession.getUser() != null )
        //{
        //  fingerprint = userSession.getUser().getKeyFingerprint();
        //}

        KurjunFileItem kurjunFileItem = ( KurjunFileItem ) fileItem;

        Metadata metadata;

        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        metadata = rawManagerService
                .put( userSession, kurjunFileItem.getFile(), kurjunFileItem.getFileName(), fingerprint );

        if ( metadata != null )
        {
            flashScope.success( "Uploaded successfully" );
        }
        else
        {

            flashScope.error( "Failed to upload." );
        }

        return Results.redirect( context.getContextPath() + "/raw-files" );
    }


    public Result download( @PathParam( "id" ) String id )
    {
        checkNotNull( id, "ID cannot be null" );

        String[] temp = id.split( "\\." );

        Renderable renderable = null;
        //temp contains [fprint].[md5]
        if ( temp.length == 2 )
        {
            renderable = rawManagerService.getFile( temp[0], temp[1] );
        }
        if ( renderable != null )
        {
            return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
        }
        return Results.text().render( "File not found" );
    }


    public Result delete( Context context, @PathParam( "id" ) String id, FlashScope flashScope )
    {
        checkNotNull( id, "ID cannot be null" );
        String[] temp = id.split( "\\." );

        boolean success = false;

        if ( temp.length == 2 )
        {
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
            success = rawManagerService.delete( uSession, temp[0], temp[1] );
        }

        if ( success )
        {
            flashScope.success( "Deleted successfully" );
            return Results.redirect( context.getContextPath() + "/raw-files" );
        }

        flashScope.error( "Failed to delete." );
        return Results.redirect( context.getContextPath() + "/raw-files" );
    }


    public Result md5()
    {
        return Results.ok().render( rawManagerService.md5() ).text();
    }


    public Result info( @Param( "id" ) String id, @Param( "name" ) String name, @Param( "md5" ) String md5,
                        @Param( "type" ) String type, @Param( "fingerprint" ) String fingerprint )
    {
        RawMetadata rawMetadata = new RawMetadata();

        if ( fingerprint == null && md5 != null )
        {
            fingerprint = "raw";
        }
        rawMetadata.setName( name );
        rawMetadata.setMd5Sum( md5 );
        rawMetadata.setFingerprint( fingerprint );

        Metadata metadata = rawManagerService.getInfo( rawMetadata );

        if ( metadata != null )
        {
            return Results.ok().render( metadata ).json();
        }
        return Results.notFound().render( "Not found" ).text();
    }
}
