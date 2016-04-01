package ai.subut.kurjun.identity;


import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.identity.service.RelationDataService;
import ai.subut.kurjun.model.identity.Relation;


/**
 *
 */
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
