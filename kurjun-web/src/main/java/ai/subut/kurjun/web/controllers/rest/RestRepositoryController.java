package ai.subut.kurjun.web.controllers.rest;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.web.controllers.BaseController;
import ai.subut.kurjun.web.service.RepositoryService;
import ninja.Result;
import ninja.Results;


@Singleton
public class RestRepositoryController extends BaseController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RestRepositoryController.class );

    @Inject
    RepositoryService repositoryService;


    public Result list()
    {
        LOGGER.debug( "Getting list of repositories" );
        List<String> repos = repositoryService.getRepositories();

        return Results.ok().render( repos ).json();
    }
}
