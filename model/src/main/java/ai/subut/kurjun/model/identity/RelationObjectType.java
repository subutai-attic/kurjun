package ai.subut.kurjun.model.identity;


/**
 *
 */
public enum RelationObjectType
{
    User( 1, "User" ),
    Repository( 2, "Repository" ),
    RepositoryContent( 3, "Repository-Content" );

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
}
