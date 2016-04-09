package ai.subut.kurjun.web.controllers;


import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.identity.ObjectType;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.web.filter.SecurityFilter;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.model.KurjunFileItem;
import ai.subut.kurjun.web.service.RawManagerService;
import ai.subut.kurjun.web.service.RepositoryService;
import ai.subut.kurjun.web.service.impl.RawManagerServiceImpl;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.FlashScope;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;


@Singleton
public class RawFileController extends BaseController
{

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private RawManagerService rawManagerService;


    public Result list( Context context , @Param( "repository" ) String repository, @Param( "node" ) String node )
    {
        node = StringUtils.isBlank( node )? "local":node;
        repository = StringUtils.isBlank( repository ) ? RawManagerServiceImpl.DEFAULT_RAW_REPO_NAME: repository;

        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        return Results.html().template( "views/raw-files.ftl" )
                      .render( "files", rawManagerService.list( uSession, repository, node ) )
                      .render( "repos", rawManagerService.getRepoList() ).render( "sel_repo", repository)
                      .render( "node", node);
    }


    @FileProvider( SubutaiFileHandler.class )
    public Result upload( Context context, @Param( "file" ) FileItem fileItem, @Param("repository") String repo,
                          FlashScope flashScope )
    {
        UserSession userSession = ( UserSession ) context.getAttribute( SecurityFilter.USER_SESSION );

        KurjunFileItem kurjunFileItem = ( KurjunFileItem ) fileItem;

        Metadata metadata;

        metadata = rawManagerService
                .put( userSession, kurjunFileItem.getFile(), kurjunFileItem.getFileName(), repo );

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

    public Result getUploadRawFileForm()
    {
        return Results.html().template( "views/_popup-upload-raw.ftl" )
                      .render( "repos", repositoryService.getRepositoryContextList( ObjectType.RawRepo.getId() ) );
    }



    public Result download( @Param("repository") String repo, @Param( "md5" ) String md5 )
    {
        Renderable renderable = null;
        renderable = rawManagerService.getFile( repo, md5 );

        if ( renderable != null )
        {
            return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
        }
        return Results.text().render( "File not found" );
    }


    public Result delete( Context context, @Param("repository") String repo, @Param( "md5" ) String md5, FlashScope flashScope )
    {
        boolean success = false;

        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        success = rawManagerService.delete( uSession, repo, md5 );

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


    public Result info( @Param( "repository" ) String repository, @Param( "md5" ) String md5,
                        @Param( "node" ) String node,
                        @Param( "name" ) String name)
    {
        Metadata metadata = rawManagerService.getInfo( repository, md5, name, node);

        if ( metadata != null )
        {
            return Results.ok().render( metadata ).json();
        }
        return Results.notFound().render( "Not found" ).text();
    }
}
