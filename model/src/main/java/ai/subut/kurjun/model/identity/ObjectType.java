package ai.subut.kurjun.model.identity;


import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public enum ObjectType
{

    Undefined( 0, "Undefined" ),
    All( 1, "All" ),
    User( 2, "User" ),
    Artifact( 3, "Repository-Content" ),
    TemplateRepo( 4, "Template-Repository" ),
    AptRepo( 5, "Apt-Repository" ),
    RawRepo( 6, "Raw-Repository" ),
    RemoteTemplateRepo( 7, "Remote-Template-Repository" ),
    RemoteAptRepo( 8, "Remote-Apt-Repository" ),
    RemoteRawRepo( 9, "Remote-Raw-Repository" );


    private String name;
    private int id;


    ObjectType( int id, String name )
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


    public static Map<String, String> getMap()
    {
        ObjectType[] values = ObjectType.values();
        Map<String, String> map = new HashMap<>(  );

        for ( ObjectType r : values )
        {
            map.put( String.valueOf(r.getId()), r.getName() );
        }

        return map;
    }
}
