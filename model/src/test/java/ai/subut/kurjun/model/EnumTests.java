package ai.subut.kurjun.model;


import org.junit.Test;

import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.RelationObjectType;
import ai.subut.kurjun.model.identity.RelationType;
import ai.subut.kurjun.model.identity.UserType;
import ai.subut.kurjun.model.index.Checksum;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.apt.Priority;
import ai.subut.kurjun.model.metadata.apt.RelationOperator;
import ai.subut.kurjun.model.repository.PackageType;
import ai.subut.kurjun.model.repository.Protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * Test cases for enumeration types.
 */
public class EnumTests
{
    @Test
    public void testDepOp() throws Exception
    {
        assertEquals( RelationOperator.Equal, RelationOperator.fromSymbol( "=" ) );
        assertEquals( RelationOperator.LaterEqual, RelationOperator.fromSymbol( ">=" ) );
        assertEquals( RelationOperator.StrictlyLater, RelationOperator.fromSymbol( ">>" ) );
        assertEquals( RelationOperator.StrictlyEarlier, RelationOperator.fromSymbol( "<<" ) );
        assertEquals( RelationOperator.EarlierEqual, RelationOperator.fromSymbol( "<=" ) );
    }


    @Test( expected = IllegalStateException.class )
    public void testDepOpBadInput() throws Exception
    {
        RelationOperator.fromSymbol( "asdf" );
    }


    @Test( expected = NullPointerException.class )
    public void testDepOpNull() throws Exception
    {
        RelationOperator.fromSymbol( null );
    }


    @Test
    public void testProtocol() throws Exception
    {
        assertTrue( Protocol.HTTP.isWebBased() );
        assertFalse( Protocol.HTTP.isSecure() );
        assertTrue( Protocol.HTTPS.isWebBased() );
        assertTrue( Protocol.HTTPS.isSecure() );
        assertFalse( Protocol.SSH.isWebBased() );
        assertTrue( Protocol.SSH.isSecure() );
    }


    @Test
    public void testIdentityEnum()
    {
        // asserts Permission
        assertNotNull( Permission.Write.getId() );
        assertNotNull( Permission.Write.getName() );
        assertNotNull( Permission.Write.getCode() );

        // asserts RelationObjectType
        assertNotNull( RelationObjectType.RepositoryApt.getName() );
        assertNotNull( RelationObjectType.RepositoryApt.getId() );
        assertNotNull( RelationObjectType.getMap() );

        assertEquals( RelationObjectType.User, RelationObjectType.valueOf( 1 ) );
        assertEquals( RelationObjectType.RepositoryParent, RelationObjectType.valueOf( 2 ) );
        assertEquals( RelationObjectType.RepositoryContent, RelationObjectType.valueOf( 3 ) );
        assertEquals( RelationObjectType.RepositoryTemplate, RelationObjectType.valueOf( 4 ) );
        assertEquals( RelationObjectType.RepositoryApt, RelationObjectType.valueOf( 5 ) );
        assertEquals( RelationObjectType.RepositoryRaw, RelationObjectType.valueOf( 6 ) );
        assertNull( RelationObjectType.valueOf( 7 ) );

        // asserts RelationType
        assertNotNull( RelationType.Owner.getId() );
        assertNotNull( RelationType.Owner.getName() );

        // asserts UserType
        assertNotNull( UserType.Regular.getId() );
        assertNotNull( UserType.Regular.getName() );

        // asserts Checksum
        Checksum.MD5.toString();

        // asserts Metadata
        assertNotNull( Priority.getRandom() );
        assertNotNull( Priority.extra );

        assertNotNull( RelationOperator.EarlierEqual );
        assertNotNull( RelationOperator.EarlierEqual.getSymbol() );

        assertNotNull( Architecture.getRandom() );
        assertNotNull( Architecture.getRandom().toString() );
        assertNotNull( Architecture.getByValue( "all" ) );
        assertNull( Architecture.getByValue( "ashsh" ) );

        assertNotNull( PackageType.DEB );
        assertNotNull( PackageType.getPackageTypes() );


    }
}
