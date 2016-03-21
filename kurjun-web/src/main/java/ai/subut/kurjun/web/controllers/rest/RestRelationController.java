package ai.subut.kurjun.web.controllers.rest;

import java.util.List;

import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.web.controllers.BaseController;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ninja.Result;
import ninja.Results;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Controller for Trust Relation Management
 */
@Singleton
public class RestRelationController extends BaseController
{
    @Inject
    IdentityManagerService identityManagerservice;

    private static final Logger LOGGER = LoggerFactory.getLogger( RestIdentityController.class );


    //*************************
    public Result getAllRelations()
    {
        List<Relation> relations = identityManagerservice.getAllRelations();

        return Results.ok().render( relations ).json();
    }

}
