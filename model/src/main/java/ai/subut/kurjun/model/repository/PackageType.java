package ai.subut.kurjun.model.repository;


import java.util.ArrayList;
import java.util.List;


/**
 * Package types supported by Kurjun repositories.
 *
 */
public final class PackageType
{

    public static final String DEB = "deb";
    public static final String SNAP = "snap";
    public static final String SUBUTAI = "subutai";


    private PackageType()
    {
        // not to be constructed
    }


    public static List<String> getPackageTypes()
    {
        List<String> ls = new ArrayList<>();
        ls.add( DEB );
        ls.add( SNAP );
        ls.add( SUBUTAI );
        return ls;
    }

}

