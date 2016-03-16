package ai.subut.kurjun.model.identity;


/**
 *
 */
public enum Permission
{
    Read( 1, "Read" ),
    Write( 2, "Write" ),
    Update( 3, "Update" ),
    Delete( 4, "Delete" );

    private String name;
    private int id;


    Permission( int id, String name )
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
