package ai.subut.kurjun.subutai;


import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.template.TemplateMetadata;


/**
 * POJO implementation of {@link TemplateMetadata}.
 *
 */
public class DefaultTemplateMetadata implements TemplateMetadata
{

    private String name;
    private String version;
    private Architecture architecture;


    @Override
    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    @Override
    public String getVersion()
    {
        return version;
    }


    public void setVersion( String version )
    {
        this.version = version;
    }


    @Override
    public Architecture getArchitecture()
    {
        return architecture;
    }


    public void setArchitecture( Architecture architecture )
    {
        this.architecture = architecture;
    }


}

