package ai.subut.kurjun.model.identity;


/**
 *
 */
public enum UserType
{
    System( 1, "System-User" ),
    Regular( 2, "Regular-User" ),
    RegularOwner( 3, "Regular-Owner" );

    private String name;
    private int id;


    UserType( int id, String name )
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
