package ai.subut.kurjun.core.dao.api.identity;


import com.google.inject.Singleton;

import ai.subut.kurjun.model.identity.Relation;


/**
 *
 */
@Singleton
public class RelationDataServiceImpl implements RelationDataService
{



    //***************************
    @Override
    public Relation persistTrustRelation( Relation relation )
    {
        try
        {
            return relation;
        }
        catch ( Exception ex )
        {
            return null;
        }
    }


}
