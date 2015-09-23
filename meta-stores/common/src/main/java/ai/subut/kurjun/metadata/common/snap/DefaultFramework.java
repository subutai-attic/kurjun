package ai.subut.kurjun.metadata.common.snap;


import java.io.Serializable;

import ai.subut.kurjun.model.metadata.snap.Framework;


/**
 * Default POJO implementation of {@link Framework}.
 *
 */
public class DefaultFramework implements Framework, Serializable
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

