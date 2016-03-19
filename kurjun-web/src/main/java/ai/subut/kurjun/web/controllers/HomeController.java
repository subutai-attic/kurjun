package ai.subut.kurjun.web.controllers;


import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.web.filter.SecurityFilter;
import ai.subut.kurjun.web.security.AuthorizedUser;
import ai.subut.kurjun.web.service.TemplateManagerService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.FlashScope;
import ninja.session.Session;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityController.class );

    @Inject
    private TemplateManagerService templateManagerService;


    public Result homePage(@AuthorizedUser UserSession userSession, @Param( "fingerprint" ) String fingerprint,
                           Session session, FlashScope flashScope )
    {
        List<SerializableMetadata> defaultTemplateList = new ArrayList<>();
        try
        {
            if (StringUtils.isBlank( fingerprint ))
            {
                fingerprint = "public";
            }

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
}
