package ai.subut.kurjun.web.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.relation.RelationService;

import ai.subut.kurjun.model.identity.RelationObjectType;
import ai.subut.kurjun.web.service.RelationManagerService;
import ai.subut.kurjun.web.service.RepositoryService;
import com.google.inject.Inject;
import ninja.Result;
import ninja.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class RepositoryController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger( RepositoryController.class );

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private RelationManagerService relationManagerService;

    public Result getRepoList()
    {
        List<String> repos = repositoryService.getRepositories();
        repos.remove( "vapt" );
        repos.remove( "raw" );

        Map<String, String> ownerMap = new HashMap<>();
        relationManagerService.getAllRelations().stream().filter( r ->
                r.getSource().getId().equals( r.getTarget().getId() )
                        && r.getTrustObject().getType() == RelationObjectType.RepositoryTemplate.getId() )
                              .forEach( r -> ownerMap.put( r.getTrustObject().getId(), r.getSource().getId() ));

        return Results.html().template("views/repositories.ftl").render( "repos", repos )
                .render( "owners", ownerMap );
    }
}
