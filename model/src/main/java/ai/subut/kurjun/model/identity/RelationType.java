package ai.subut.kurjun.model.identity;


/**
 *
 */
public enum RelationType
{
    Owner( 1, "Owner" ),
    Shared( 2, "Shared" );

    private String name;
    private int id;


    RelationType( int id, String name )
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
