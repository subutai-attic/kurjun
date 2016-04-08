package ai.subut.kurjun.web.controllers.rest;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.identity.ObjectType;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.web.controllers.BaseController;
import ai.subut.kurjun.web.service.RepositoryService;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;


@Singleton
public class RestRepositoryController extends BaseController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RestRepositoryController.class );

    @Inject
    RepositoryService repositoryService;


    public Result getRepoList(@Param( "type" ) String repoType)
    {
        int repoTypeID = ObjectType.All.getId();

        if( Strings.isNullOrEmpty(repoType))
        {
            if(repoType.toLowerCase().equals( "apt" ))
                repoTypeID = ObjectType.AptRepo.getId();
            if(repoType.toLowerCase().equals( "template" ))
                repoTypeID = ObjectType.TemplateRepo.getId();
            if(repoType.toLowerCase().equals( "raw" ))
                repoTypeID = ObjectType.RawRepo.getId();
        }

        List<RepositoryData> repos = repositoryService.getRepositoryList(repoTypeID);

        LOGGER.debug( "Getting list of repositories" );

        return Results.ok().render( repos ).json();
    }

}
