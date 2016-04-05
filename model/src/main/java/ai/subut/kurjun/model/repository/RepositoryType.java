package ai.subut.kurjun.model.repository;


/**
 *
 */
public enum RepositoryType
{
    TemplateRepo( 1, "Template-Repository" ),
    AptRepo( 1, "Apt-Repository" ),
    RawRepo( 1, "Raw-Repository" );

    private String name;
    private int id;


    RepositoryType( int id, String name )
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
