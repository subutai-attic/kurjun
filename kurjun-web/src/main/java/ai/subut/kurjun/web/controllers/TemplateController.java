package ai.subut.kurjun.web.controllers;


import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.metadata.common.utils.IdValidators;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.model.KurjunFileItem;
import ai.subut.kurjun.web.service.RelationManagerService;
import ai.subut.kurjun.web.service.RepositoryService;
import ai.subut.kurjun.web.service.TemplateManagerService;

import com.google.common.base.Strings;
import com.google.inject.Inject;

import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.session.FlashScope;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TemplateController extends BaseController
{

    private static final Logger LOGGER = LoggerFactory.getLogger( TemplateController.class );

    @Inject
    private TemplateManagerService templateManagerService;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private RelationManagerService relationManagerService;


    public Result listTemplates( Context context, FlashScope flashScope, @Param("repo") String repo )
    {
        List<SerializableMetadata> defaultTemplateList = new ArrayList<>();
        try
        {
            repo = StringUtils.isBlank(repo)? "public":repo;
            //*****************************************************
            templateManagerService.setUserSession( (UserSession ) context.getAttribute( "USER_SESSION" ) );
            defaultTemplateList = templateManagerService.list( repo, false );
            //*****************************************************
        }
        catch ( IOException e )
        {
            flashScope.error( "Failed to get list of templates.");
            LOGGER.error( "Failed to get list of templates: " + e.getMessage() );
        }
        List<String> repos = repositoryService.getRepositories();

        return Results.html().template("views/templates.ftl").render( "templates", defaultTemplateList )
                .render("repos", repos).render("sel_repo", repo);
    }


    public Result getUploadTemplateForm()
    {
        List<String> repos = repositoryService.getRepositories();
        return Results.html().template("views/_popup-upload-templ.ftl").render("repos", repos);
    }


    @FileProvider( SubutaiFileHandler.class )
    public Result uploadTemplate( Context context, @Param( "repository" ) String repository,
                                  @Param("repo_name") String repoName, @Param("repo_type") String repoType,
                                  @Param( "file" ) FileItem file, FlashScope flashScope )
    {
        try {
            if ( repoType.equals("new")) {
                repository = repoName;
            }

            if ( StringUtils.isBlank( repository ) ) {
                repository = "public";
            }

            KurjunFileItem fileItem = (KurjunFileItem) file;
            /*
            if (md5 != null && !md5.isEmpty()) {
                if (!fileItem.md5().equals(md5)) {
                    fileItem.cleanup();
                    flashScope.error( "Failed: MD5 checksum mismatch.");
                    return Results.redirect( "/" );
                }
            }
            */
            //*****************************************************
            templateManagerService.setUserSession( (UserSession ) context.getAttribute( "USER_SESSION" ) );
            String id = templateManagerService.upload( repository, fileItem.getInputStream() );
            //*****************************************************

            if( Strings.isNullOrEmpty(id))
            {
                flashScope.error("Failed to upload template.Access Permission error.");
                return Results.redirect( "/" );
            }
            else
            {
                String[] temp = id.split("\\.");
                //temp contains [fprint].[md5]
                if (temp.length == 2) {
                    flashScope.success("Template uploaded successfully");
                    return Results.redirect( "/" );
                }
            }
        }
        catch ( IOException e )
        {
            LOGGER.error( "Failed to upload template: {}", e.getMessage() );
        }

        flashScope.error("Failed to upload template");
        return Results.redirect( "/" );
    }


    public Result getTemplateInfo( Context context,@PathParam( "id" ) String id,
                                   @Param( "name" ) String name, @Param( "version" ) String version,
                                   @Param( "md5" ) String md5, @Param( "type" ) String type )
    {
        if ( !StringUtils.isBlank(id) )
        {
            TemplateId tid = IdValidators.Template.validate( id );

            //*****************************************************
            templateManagerService.setUserSession( (UserSession ) context.getAttribute( "USER_SESSION" ) );
            DefaultTemplate defaultTemplate = templateManagerService.getTemplate( tid, md5, name, version );
            //*****************************************************

            if ( defaultTemplate != null )
            {
                return Results.html().template("views/_popup-view-tpl.ftl").render( "templ_info", defaultTemplate );
            }
        }

        return Results.html().template("views/_popup-view-tpl.ftl");
    }


    public Result downloadTemplate( Context context, @PathParam( "id" ) String id )
    {
        try
        {
            TemplateId tid = IdValidators.Template.validate( id );

            //*****************************************************
            templateManagerService.setUserSession( (UserSession ) context.getAttribute( "USER_SESSION" ) );
            Renderable renderable = templateManagerService.renderableTemplate( tid.getOwnerFprint(), tid.getMd5(), false );
            //*****************************************************

            return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
        }
        catch ( IOException e )
        {
            LOGGER.error( "Failed to download template: " + e.getMessage() );
            return Results.internalServerError().text().render("Failed to download template");
        }
    }


    public Result deleteTemplate( Context context,@PathParam( "id" ) String id,
                                  FlashScope flashScope )
    {
        try
        {
            TemplateId tid = IdValidators.Template.validate(id);

            //*****************************************************
            templateManagerService.setUserSession( (UserSession ) context.getAttribute( "USER_SESSION" ) );
            boolean status = templateManagerService.delete(tid);
            //*****************************************************

            if(status)
                flashScope.success( "Template removed successfully" );
            else
                flashScope.error( "Access permission error. Template not removed !!!" );

        }
        catch (Exception e)
        {
            LOGGER.error( "Failed to remove template: " + e.getMessage() );
            flashScope.error("Failed to remove template.");
        }

        return Results.redirect("/");
    }
}
