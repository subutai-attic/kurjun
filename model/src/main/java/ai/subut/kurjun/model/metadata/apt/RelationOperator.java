package ai.subut.kurjun.model.metadata.apt;


/**
 * Dependency comparison operators used in the Depends attribute of control files.
 */
public enum RelationOperator
{
    StrictlyEarlier( "<<" ), EarlierEqual( "<=" ), Equal( "="), LaterEqual( ">=" ), StrictlyLater( ">>" );

    private final String symbol;

    private RelationOperator( String symbol )
    {
        this.symbol = symbol;
    }

    public String getSymbol()
    {
        return this.symbol;
    }

    public static RelationOperator fromSymbol( String symbol )
    {
        switch ( symbol )
        {
            case "<<": return StrictlyEarlier;
            case "<=": return EarlierEqual;
            case "=" : return Equal;
            case ">=": return LaterEqual;
            case ">>": return StrictlyLater;
            default: throw new IllegalStateException( "Unrecognized symbol: " + symbol );
        }
    }
}
