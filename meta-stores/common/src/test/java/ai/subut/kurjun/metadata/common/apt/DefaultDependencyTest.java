package ai.subut.kurjun.metadata.common.apt;


import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.RelationOperator;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class DefaultDependencyTest
{
    private DefaultDependency defaultDependency;


    @Before
    public void setUp() throws Exception
    {
        // mock
        List<Dependency> alternatives = new ArrayList<>();

        defaultDependency = new DefaultDependency();

        defaultDependency.setVersion( "1.0.0" );
        defaultDependency.setPackage( "package" );
        defaultDependency.setAlternatives( alternatives );
        defaultDependency.setDependencyOperator( null );
    }


    @Test
    public void getPackage() throws Exception
    {
        assertNotNull( defaultDependency.getPackage() );
    }


    @Test
    public void getAlternatives() throws Exception
    {
        assertNotNull( defaultDependency.getAlternatives() );
    }


    @Test
    public void getVersion() throws Exception
    {
        assertNotNull( defaultDependency.getVersion() );
    }


    @Test
    public void getDependencyOperator() throws Exception
    {
        defaultDependency.getDependencyOperator() ;
    }
}