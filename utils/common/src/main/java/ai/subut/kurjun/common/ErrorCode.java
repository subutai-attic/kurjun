package ai.subut.kurjun.common;


/**
 *
 */
public enum ErrorCode
{
    Success( 0, "Success"),
    SystemError( 1, "SystemError" ),
    AccessPermissionError( 2, "AccessPermissionError"),
    ObjectNotFound( 3, "ObjectNotFound");

    private String name;
    private int id;


    ErrorCode( int id, String name )
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
