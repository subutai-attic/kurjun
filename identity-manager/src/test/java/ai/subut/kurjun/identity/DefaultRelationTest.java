package ai.subut.kurjun.identity;


import java.util.EnumSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.RelationObject;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class DefaultRelationTest
{
    private DefaultRelation defaultRelation;
    private Set<Permission> permissions = EnumSet.noneOf( Permission.class );

    @Mock
    RelationObject relationObject;


    @Before
    public void setUp() throws Exception
    {
        defaultRelation = new DefaultRelation();

        defaultRelation.setPermissions( permissions );
        defaultRelation.setSource( relationObject );
        defaultRelation.setTarget( relationObject );
        defaultRelation.setTrustObject( relationObject );
        defaultRelation.setType( 1 );
    }


    @Test
    public void getPermissions() throws Exception
    {
        // asserts
        assertNotNull( defaultRelation.getPermissions() );
    }


    @Test
    public void getId() throws Exception
    {
        // asserts
        assertNotNull( defaultRelation.getId() );
    }


    @Test
    public void getSource() throws Exception
    {
        // asserts
        assertNotNull( defaultRelation.getSource() );
    }


    @Test
    public void getTarget() throws Exception
    {
        // asserts
        assertNotNull( defaultRelation.getTarget() );
    }


    @Test
    public void getTrustObject() throws Exception
    {
        // asserts
        assertNotNull( defaultRelation.getTrustObject() );
    }


    @Test
    public void getType() throws Exception
    {
        // asserts
        assertNotNull( defaultRelation.getType() );
    }


    @Test
    public void equals() throws Exception
    {
        defaultRelation.equals( new Object() );
        defaultRelation.equals( defaultRelation );
        defaultRelation.hashCode();
    }
}