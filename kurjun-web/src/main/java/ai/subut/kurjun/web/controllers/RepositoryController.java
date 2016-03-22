package ai.subut.kurjun.web.controllers;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.security.AuthorizedUser;
import ai.subut.kurjun.web.service.RepositoryService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class RepositoryController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger( RepositoryController.class );

    @Inject
    private RepositoryService repositoryService;

    public Result getRepoList()
    {
        return Results.html().template("views/repositories.ftl").render( "repos", repositoryService.getRepositories() );
    }

    public Result addRepo()
    {
        return Results.ok();
    }

    public Result getRepo()
    {
        return Results.ok();
    }
}
