package ai.subut.kurjun.model.metadata;


/**
 * Architecture enumeration.
 */
public enum Architecture
{
    ALL,
    AMD64,
    i386;


    /**
     * Gets architecture by name. This method does case-insensitive matching for architecture enum items. This method is
     * preferred to {@link Enum#valueOf(java.lang.Class, java.lang.String)} because in most of the places arch fields
     * are in lower case whereas enum items are defined in upper case.
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


    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }

}

