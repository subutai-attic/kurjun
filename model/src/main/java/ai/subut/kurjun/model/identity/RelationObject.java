package ai.subut.kurjun.model.identity;


/**
 *
 */
public interface RelationObject
{

    int getType();

    long getId();

    void setId( long id );

    String getUniqueId();

    String getObjectId();

    void setObjectId( String objectId );

    void setType( int type );
}
