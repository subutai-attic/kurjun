package ai.subut.kurjun.core.dao.api.identity;


import javax.persistence.EntityManager;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.model.identity.RelationObject;


/**
 *
 */
public class RelationObjectDAO   extends GenericDAOImpl<RelationObject>
{
    /*
    @Inject
    public RelationObjectDAO( final Provider<EntityManager> entityManagerProvider )
    {
        super( entityManagerProvider );
    }*/


    public RelationObjectDAO()
    {
        super();
    }

}
