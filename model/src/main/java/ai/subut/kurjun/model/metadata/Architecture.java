package ai.subut.kurjun.model.metadata;


/**
 * Architecture enumeration.
 */
public enum Architecture
{
    amd64, i386;


    /**
     * Gets architecture by name. This method does case-insensitive search.
     *
     * @param arch
     * @return
     */
    public static Architecture getByValue( String arch )
    {
        for ( Architecture a : values() )
        {
            if ( a.toString().equalsIgnoreCase( arch ) )
            {
                return a;
            }
        }
        return null;
    }

}
