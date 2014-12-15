package ai.subut.kurjun.model;


import org.junit.Test;

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
        assertEquals( DepOp.Equal, DepOp.fromSymbol( "=" ) );
        assertEquals( DepOp.LaterEqual, DepOp.fromSymbol( ">=" ) );
        assertEquals( DepOp.StrictlyLater, DepOp.fromSymbol( ">>" ) );
        assertEquals( DepOp.StrictlyEarlier, DepOp.fromSymbol( "<<" ) );
        assertEquals( DepOp.EarlierEqual, DepOp.fromSymbol( "<=" ) );
    }


    @Test( expected = IllegalStateException.class )
    public void testDepOpBadInput() throws Exception
    {
        DepOp.fromSymbol( "asdf" );
    }


    @Test( expected = NullPointerException.class )
    public void testDepOpNull() throws Exception
    {
        DepOp.fromSymbol( null );
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
