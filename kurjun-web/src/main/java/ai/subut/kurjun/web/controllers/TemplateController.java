package ai.subut.kurjun.web.controllers;


import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.metadata.common.utils.IdValidators;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.web.controllers.rest.RestTemplateController;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.model.KurjunFileItem;
import ai.subut.kurjun.web.service.TemplateManagerService;
import com.google.inject.Inject;
import ninja.Result;
import ninja.Results;
import ninja.exceptions.InternalServerErrorException;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.session.FlashScope;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;
import ninja.uploads.MemoryFileItemProvider;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TemplateController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityController.class );

    @Inject
    private TemplateManagerService templateManagerService;


    @FileProvider( SubutaiFileHandler.class )
    public Result uploadTemplate( @Param( "repository" ) String repository, @Param( "file" ) FileItem file,
                                  @Param( "md5" ) String md5, FlashScope flashScope )
    {
        try {
            if (repository == null) {
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


    public Result deleteTemplate( @PathParam( "id" ) String id, FlashScope flashScope )
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
