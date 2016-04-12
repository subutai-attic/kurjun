package ai.subut.kurjun.model.metadata.apt;


/**
 * The priority of the package.
 */
public enum Priority
{
    required, important, standard, optional, extra;


    public static Priority getRandom()
    {
        return values()[( int ) ( Math.random() * values().length )];
    }
}
