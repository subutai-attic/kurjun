package ai.subut.kurjun.model.identity;


import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public enum RelationObjectType
{
    User( 1, "User" ),
    RepositoryParent( 2, "Repository-Parent" ),
    RepositoryContent( 3, "Repository-Content" ),
    RepositoryTemplate( 4, "Repository-Template" ),
    RepositoryApt( 5, "Repository-Apt" ),
    RepositoryRaw( 6, "Repository-Raw" );

    private String name;
    private int id;


    RelationObjectType( int id, String name )
    {
        this.id = id;
        this.name = name;
    }


    public String getName()
    {
        return name;
    }


    public int getId()
    {
        return id;
    }

    public static RelationObjectType valueOf( int v )
    {
        switch ( v )
        {
            case 1: return User;
            case 2: return RepositoryParent;
            case 3: return RepositoryContent;
            case 4: return RepositoryTemplate;
            case 5: return RepositoryApt;
            case 6: return RepositoryRaw;
            default: return null;
        }
    }

    public static Map<String, String> getMap()
    {
        RelationObjectType[] values = RelationObjectType.values();
        Map<String, String> map = new HashMap<>(  );
        for ( RelationObjectType r : values ) {
            map.put( String.valueOf(r.getId()), r.getName() );
        }

        return map;
    }
}
