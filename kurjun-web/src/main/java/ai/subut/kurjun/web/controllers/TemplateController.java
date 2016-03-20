package ai.subut.kurjun.web.controllers;


import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.metadata.common.utils.IdValidators;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.model.KurjunFileItem;
import ai.subut.kurjun.web.security.AuthorizedUser;
import ai.subut.kurjun.web.service.RepositoryService;
import ai.subut.kurjun.web.service.TemplateManagerService;
import com.google.gson.Gson;
import com.google.inject.Inject;
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


public class TemplateController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityController.class );

    @Inject
    private TemplateManagerService templateManagerService;

    @Inject
    private RepositoryService repositoryService;


    public Result listTemplates( @AuthorizedUser UserSession userSession, FlashScope flashScope )
    {
        List<SerializableMetadata> defaultTemplateList = new ArrayList<>();
        try
        {
            String fingerprint = "public";
            if ( userSession.getUser() != null && StringUtils.isBlank(userSession.getUser().getKeyFingerprint()) )
            {
                fingerprint = userSession.getUser().getKeyFingerprint();
            }

            //LOGGER.info( "token: "+userSession.getUser().getUserToken().getFullToken());
            LOGGER.info("User session: "+new Gson().toJson( userSession ));

            defaultTemplateList = templateManagerService.list( fingerprint, false );
        }
        catch ( IOException e )
        {
            flashScope.error( "Failed to get list of templates.");
            LOGGER.error( "Failed to get list of templates: " + e.getMessage() );
        }

        return Results.html().template("views/home.ftl").render( "templates", defaultTemplateList );
    }


    public Result getUploadTemplateForm( @AuthorizedUser UserSession userSession )
    {
        List<String> repos = repositoryService.getRepositories();
        return Results.html().template("views/_popup-add-tpl.ftl").render("repos", repos);
    }


    @FileProvider( SubutaiFileHandler.class )
    public Result uploadTemplate( @AuthorizedUser UserSession userSession, @Param( "repository" ) String repository,
                                  @Param( "file" ) FileItem file, @Param( "md5" ) String md5, FlashScope flashScope )
    {
        try {
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
            String id = templateManagerService.upload(repository, fileItem.getInputStream());

            String[] temp = id.split("\\.");
            //temp contains [fprint].[md5]
            if (temp.length == 2) {
                flashScope.success("Template uploaded successfully");
                return Results.redirect( "/" );
            }
        }
        catch ( IOException e )
        {
            LOGGER.error( "Failed to upload template: {}", e.getMessage() );
        }

        flashScope.error("Failed to upload template");
        return Results.redirect( "/" );
    }


    public Result getTemplateInfo( @AuthorizedUser UserSession userSession, @PathParam( "id" ) String id,
                                   @Param( "name" ) String name, @Param( "version" ) String version,
                                  @Param( "md5" ) String md5, @Param( "type" ) String type )
    {
        if ( !StringUtils.isBlank(id) )
        {
            TemplateId tid = IdValidators.Template.validate( id );
            DefaultTemplate defaultTemplate = templateManagerService.getTemplate( tid, md5, name, version );

            if ( defaultTemplate != null )
            {
                return Results.html().template("views/_popup-view-tpl.ftl").render( "templ_info", defaultTemplate );
            }
        }

        return Results.html().template("views/_popup-view-tpl.ftl");
    }


    public Result downloadTemplate( @AuthorizedUser UserSession userSession, @PathParam( "id" ) String id )
    {
        try
        {
            TemplateId tid = IdValidators.Template.validate( id );
            Renderable renderable = templateManagerService.renderableTemplate( tid.getOwnerFprint(), tid.getMd5(), false );
            return new Result( 200 ).render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
        }
        catch ( IOException e )
        {
            LOGGER.error( "Failed to download template: " + e.getMessage() );
            return Results.internalServerError();
        }
    }


    public Result deleteTemplate( @AuthorizedUser UserSession userSession, @PathParam( "id" ) String id,
                                  FlashScope flashScope )
    {
        try
        {
            TemplateId tid = IdValidators.Template.validate(id);
            templateManagerService.delete(tid);
            flashScope.success( "Template removed successfully" );
        }
        catch (Exception e)
        {
            LOGGER.error( "Failed to remove template: " + e.getMessage() );
            flashScope.error("Failed to remove template.");
        }

        return Results.redirect("/");
    }
}
