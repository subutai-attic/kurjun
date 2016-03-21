package ai.subut.kurjun.model.identity;


/**
 *
 */
public enum Permission
{
    Read( 1, "Read", 'R' ),
    Write( 2, "Write", 'W' ),
    Update( 3, "Update", 'U' ),
    Delete( 4, "Delete", 'D' );

    private String name;
    private int id;
    private char code;


    Permission( int id, String name, char code )
    {
        this.id = id;
        this.name = name;
        this.code = code;
    }


    public String getName()
    {
        return name;
    }


    public int getId()
    {
        return id;
    }

    public char getCode()
    {
        return code;
    }

}
