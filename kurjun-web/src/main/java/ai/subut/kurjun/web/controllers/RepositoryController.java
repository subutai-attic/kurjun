package ai.subut.kurjun.web.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.web.service.RepositoryService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ninja.Result;
import ninja.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Singleton
public class RepositoryController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger( RepositoryController.class );

    @Inject
    private RepositoryService repositoryService;


    public Result getRepoList()
    {
        //List<RepositoryData> repos = repositoryService.getRepositoryList();
        List<RepositoryData> repos = repositoryService.getRepositoryList();

        Map<String, String> ownerMap = new HashMap<>();
        return Results.html().template("views/repositories.ftl").render( "repos", repos )
                .render( "owners", ownerMap );
    }
}
