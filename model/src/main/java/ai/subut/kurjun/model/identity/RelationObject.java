package ai.subut.kurjun.model.identity;


/**
 *
 */
public interface RelationObject
{

    int getType();

    String getUniqueId();

    String getObjectId();

    void setObjectId( String objectId );

    void setType( int type );
}
