package ai.subut.kurjun.model.metadata.snap;


import java.util.function.Predicate;
import java.util.regex.Pattern;


/**
 * Predicate interface to filter {@link SnapMetadata} instances.
 *
 */
public interface SnapMetadataFilter extends Predicate<SnapMetadata>
{

    /**
     * Returns an instance of this interface that filters metadata by matching names with given name pattern.
     *
     * @param namePattern name pattern, should be a valid regular expression
     * @return
     */
    static SnapMetadataFilter getNameFilter( String namePattern )
    {
        Pattern pattern = Pattern.compile( namePattern, Pattern.CASE_INSENSITIVE );
        return (SnapMetadata m) -> m.getName() != null && pattern.matcher( m.getName() ).matches();
    }

}

