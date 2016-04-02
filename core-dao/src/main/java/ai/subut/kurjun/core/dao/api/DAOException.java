package ai.subut.kurjun.core.dao.api;


/**
 *
 */
public class DAOException extends Exception
{

    public DAOException()
    {
    }


    public DAOException( Throwable cause )
    {
        super( cause );
    }


    public DAOException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
