package ai.subut.kurjun.model;


import org.junit.Test;

import ai.subut.kurjun.model.metadata.RelationOperator;
import ai.subut.kurjun.model.repository.Protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
}
