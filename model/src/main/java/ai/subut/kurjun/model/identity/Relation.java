package ai.subut.kurjun.model.identity;


import java.util.Set;


/**
 * Relation Chain of the Objects
 */
public interface Relation
{
    void setId( long id );

    Set<Permission> getPermissions();

    void setPermissions( Set<Permission> permissions );

    long getId();

    String getPerms();

    void setPerms( String perms );

    RelationObject getSource();

    void setSource( RelationObject source );

    RelationObject getTarget();

    void setTarget( RelationObject target );

    RelationObject getTrustObject();

    void setTrustObject( RelationObject trustObject );

    int getType();

    void setType( int type );
}
