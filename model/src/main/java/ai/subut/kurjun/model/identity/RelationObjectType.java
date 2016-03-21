package ai.subut.kurjun.model.identity;


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
}
