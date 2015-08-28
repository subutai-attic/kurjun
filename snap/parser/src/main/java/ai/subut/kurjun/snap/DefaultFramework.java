package ai.subut.kurjun.snap;


import ai.subut.kurjun.model.metadata.snap.Framework;


public class DefaultFramework implements Framework
{

    private String name;


    @Override
    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


}

