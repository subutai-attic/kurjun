package ai.subut.kurjun.cfparser;


import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.model.metadata.RelationOperator;
import ai.subut.kurjun.model.metadata.Dependency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Test cases for the {@link ai.subut.kurjun.cfparser.DependencyParser}.
 */
public class DependencyParserTest
{
    private static final Logger LOG = LoggerFactory.getLogger( DependencyParserTest.class );

    String[] SIMPLE_DEP_LIST = {
            "foo",
            "foo, bar",
            "foo, bar, cat, rat"
    };

    String[] VERSIONED_DEP_LIST = {
            "foo (>= 2.2.1)",
            "foo (>= 2.2.1), bar (<= 5.6.0)",
            "foo (>= 2.2.1), bar (<= 5.6.0), cat (= 2.4.1), rat (>> 9.1.10), dog (<< 3.12.23)"
    };

    String[] MIXED_DEP_LIST = {
            "foo (>= 2.2.1) | alt1",
            "foo (>= 2.2.1) | alt1, bar (<= 5.6.0) | alt2 (>= 2.1.0) | alt3",
            "foo (>= 2.2.1) | alt1, bar (<= 5.6.0) | alt2 (>= 2.1.0) | alt3, cat (= 2.4.1), rat (>> 9.1.10) | alt4, dog (<< 3.12.23)"
    };

    DependencyParser parser;


    @Before
    public void setParser()
    {
        parser = new DependencyParser();
    }


    @Test( expected = IllegalStateException.class )
    public void testEmptyString()
    {
        parser.getDependencies( "" );
    }


    @Test( expected = NullPointerException.class )
    public void testNullString()
    {
        parser.getDependencies( null );
    }


    @Test
    public void testSimple() throws Exception
    {
        for ( String depsRaw : SIMPLE_DEP_LIST )
        {
            List<Dependency> depList = parser.getDependencies( depsRaw );
            assertNotNull( depList );

            switch ( depsRaw )
            {
                case "foo":
                    assertEquals( "The size of the dependency list should be one.", 1, depList.size() );
                    assertEquals( "The one dependency should be equal to 'foo'", "foo", depList.get( 0 ).getPackage() );
                    break;
                case "foo, bar":
                    assertEquals( "The size of the dependency list should be two.", 2, depList.size() );
                    assertEquals( "The first dependency should be equal to 'foo'", "foo",
                            depList.get( 0 ).getPackage() );
                    assertEquals( "The second dependency should be equal to 'bar'", "bar",
                            depList.get( 1 ).getPackage() );
                    break;
                case "foo, bar, cat, rat":
                    assertEquals( "The size of the dependency list should be 4.", 4, depList.size() );
                    assertEquals( "The first dependency should be equal to 'foo'", "foo",
                            depList.get( 0 ).getPackage() );
                    assertEquals( "The second dependency should be equal to 'bar'", "bar",
                            depList.get( 1 ).getPackage() );
                    assertEquals( "The third dependency should be equal to 'cat'", "cat",
                            depList.get( 2 ).getPackage() );
                    assertEquals( "The fourth and last dependency should be equal to 'rat'", "rat",
                            depList.get( 3 ).getPackage() );
                    break;
            }
        }
    }


    @Test
    public void testVersioned() throws Exception
    {
        for ( String depsRaw : VERSIONED_DEP_LIST )
        {
            List<Dependency> depList = parser.getDependencies( depsRaw );
            assertNotNull( depList );

            switch ( depsRaw )
            {
                case "foo (>= 2.2.1)":
                    assertEquals( "The size of the dependency list should be one.", 1, depList.size() );

                    assertEquals( "The one dependency should be equal to 'foo'", "foo", depList.get( 0 ).getPackage() );
                    assertEquals( "The first dependency's version should be equal to '2.2.1'", "2.2.1",
                            depList.get( 0 ).getVersion() );
                    assertEquals( "The first dependency's dep op is equal to '>='", ">=",
                            depList.get( 0 ).getDependencyOperator().getSymbol() );
                    break;
                case "foo (>= 2.2.1), bar (<= 5.6.0)":
                    assertEquals( "The size of the dependency list should be two.", 2, depList.size() );

                    assertEquals( "The first dependency should be equal to 'foo'", "foo",
                            depList.get( 0 ).getPackage() );
                    assertEquals( "The first dependency's version should be equal to '2.2.1'", "2.2.1",
                            depList.get( 0 ).getVersion() );
                    assertEquals( "The first dependency's dep op is equal to '>='", ">=",
                            depList.get( 0 ).getDependencyOperator().getSymbol() );

                    assertEquals( "The second dependency should be equal to 'bar'", "bar",
                            depList.get( 1 ).getPackage() );
                    assertEquals( "The second dependency's version should be equal to '5.6.0'", "5.6.0",
                            depList.get( 1 ).getVersion() );
                    assertEquals( "The second dependency's dep op is equal to '<='", "<=",
                            depList.get( 1 ).getDependencyOperator().getSymbol() );
                    break;
                case "foo (>= 2.2.1), bar (<= 5.6.0), cat (= 2.4.1), rat (>> 9.1.10), dog (<< 3.12.23)":
                    assertEquals( "The size of the dependency list should be 5.", 5, depList.size() );

                    assertEquals( "The first dependency should be equal to 'foo'", "foo",
                            depList.get( 0 ).getPackage() );
                    assertEquals( "The first dependency's version should be equal to '2.2.1'", "2.2.1",
                            depList.get( 0 ).getVersion() );
                    assertEquals( "The first dependency's dep op is equal to '>='", ">=",
                            depList.get( 0 ).getDependencyOperator().getSymbol() );

                    assertEquals( "The second dependency should be equal to 'bar'", "bar",
                            depList.get( 1 ).getPackage() );
                    assertEquals( "The second dependency's version should be equal to '5.6.0'", "5.6.0",
                            depList.get( 1 ).getVersion() );
                    assertEquals( "The second dependency's dep op is equal to '<='", "<=",
                            depList.get( 1 ).getDependencyOperator().getSymbol() );

                    assertEquals( "The third dependency should be equal to 'cat'", "cat",
                            depList.get( 2 ).getPackage() );
                    assertEquals( "The third dependency's version should be equal to '2.4.1'", "2.4.1",
                            depList.get( 2 ).getVersion() );
                    assertEquals( "The third dependency's dep op is equal to '='", "=",
                            depList.get( 2 ).getDependencyOperator().getSymbol() );

                    assertEquals( "The fourth dependency should be equal to 'rat'", "rat",
                            depList.get( 3 ).getPackage() );
                    assertEquals( "The fourth dependency's version should be equal to '9.1.10'", "9.1.10",
                            depList.get( 3 ).getVersion() );
                    assertEquals( "The fourth dependency's dep op is equal to '>>'", ">>",
                            depList.get( 3 ).getDependencyOperator().getSymbol() );

                    assertEquals( "The fifth and last dependency should be equal to 'dog'", "dog",
                            depList.get( 4 ).getPackage() );
                    assertEquals( "The fifth dependency's version should be equal to '3.12.23'", "3.12.23",
                            depList.get( 4 ).getVersion() );
                    assertEquals( "The fifth dependency's dep op is equal to '<<'", "<<",
                            depList.get( 4 ).getDependencyOperator().getSymbol() );
                    break;
            }
        }
    }


    @Test
    public void testMixed() throws Exception
    {
        for ( String depsRaw : MIXED_DEP_LIST )
        {
            LOG.debug( "Processing depsRaw = {}", depsRaw );
            List<Dependency> depList = parser.getDependencies( depsRaw );
            assertNotNull( depList );

            switch ( depsRaw )
            {
                case "foo (>= 2.2.1) | alt1":
                    assertEquals( "The size of the dependency list should be one.", 1, depList.size() );

                    assertEquals( "The one dependency should be equal to 'foo'", "foo", depList.get( 0 ).getPackage() );
                    assertEquals( "The first dependency's version should be equal to '2.2.1'", "2.2.1",
                            depList.get( 0 ).getVersion() );
                    assertEquals( "The first dependency's dep op is equal to '>='", ">=",
                            depList.get( 0 ).getDependencyOperator().getSymbol() );
                    assertEquals( "alt1", depList.get( 0 ).getAlternatives().get( 0 ).getPackage() );
                    break;
                case "foo (>= 2.2.1) | alt1, bar (<= 5.6.0) | alt2 (>= 2.1.0) | alt3":
                    assertEquals( "The size of the dependency list should be two.", 2, depList.size() );

                    assertEquals( "The first dependency should be equal to 'foo'", "foo",
                            depList.get( 0 ).getPackage() );
                    assertEquals( "The first dependency's version should be equal to '2.2.1'", "2.2.1",
                            depList.get( 0 ).getVersion() );
                    assertEquals( "The first dependency's dep op is equal to '>='", ">=",
                            depList.get( 0 ).getDependencyOperator().getSymbol() );
                    assertEquals( "alt1", depList.get( 0 ).getAlternatives().get( 0 ).getPackage() );

                    assertEquals( "The second dependency should be equal to 'bar'", "bar",
                            depList.get( 1 ).getPackage() );
                    assertEquals( "The second dependency's version should be equal to '5.6.0'", "5.6.0",
                            depList.get( 1 ).getVersion() );
                    assertEquals( "The second dependency's dep op is equal to '<='", "<=",
                            depList.get( 1 ).getDependencyOperator().getSymbol() );
                    assertEquals( "alt2", depList.get( 1 ).getAlternatives().get( 0 ).getPackage() );
                    assertEquals( "2.1.0", depList.get( 1 ).getAlternatives().get( 0 ).getVersion() );
                    assertEquals( RelationOperator.LaterEqual, depList.get( 1 ).getAlternatives().get( 0 ).getDependencyOperator() );
                    break;
                case "foo (>= 2.2.1) | alt1, bar (<= 5.6.0) | alt2 (>= 2.1.0) | alt3, cat (= 2.4.1), rat (>> 9.1.10) | alt4, dog (<< 3.12.23)":
                    assertEquals( "The size of the dependency list should be 5.", 5, depList.size() );

                    assertEquals( "The first dependency should be equal to 'foo'", "foo",
                            depList.get( 0 ).getPackage() );
                    assertEquals( "The first dependency's version should be equal to '2.2.1'", "2.2.1",
                            depList.get( 0 ).getVersion() );
                    assertEquals( "The first dependency's dep op is equal to '>='", ">=",
                            depList.get( 0 ).getDependencyOperator().getSymbol() );

                    assertEquals( "The second dependency should be equal to 'bar'", "bar",
                            depList.get( 1 ).getPackage() );
                    assertEquals( "The second dependency's version should be equal to '5.6.0'", "5.6.0",
                            depList.get( 1 ).getVersion() );
                    assertEquals( "The second dependency's dep op is equal to '<='", "<=",
                            depList.get( 1 ).getDependencyOperator().getSymbol() );

                    assertEquals( "The third dependency should be equal to 'cat'", "cat",
                            depList.get( 2 ).getPackage() );
                    assertEquals( "The third dependency's version should be equal to '2.4.1'", "2.4.1",
                            depList.get( 2 ).getVersion() );
                    assertEquals( "The third dependency's dep op is equal to '='", "=",
                            depList.get( 2 ).getDependencyOperator().getSymbol() );

                    assertEquals( "The fourth dependency should be equal to 'rat'", "rat",
                            depList.get( 3 ).getPackage() );
                    assertEquals( "The fourth dependency's version should be equal to '9.1.10'", "9.1.10",
                            depList.get( 3 ).getVersion() );
                    assertEquals( "The fourth dependency's dep op is equal to '>>'", ">>",
                            depList.get( 3 ).getDependencyOperator().getSymbol() );

                    assertEquals( "The fifth and last dependency should be equal to 'dog'", "dog",
                            depList.get( 4 ).getPackage() );
                    assertEquals( "The fifth dependency's version should be equal to '3.12.23'", "3.12.23",
                            depList.get( 4 ).getVersion() );
                    assertEquals( "The fifth dependency's dep op is equal to '<<'", "<<",
                            depList.get( 4 ).getDependencyOperator().getSymbol() );
                    break;
            }
        }
    }
}
