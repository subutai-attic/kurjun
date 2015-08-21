package ai.subut.kurjun.model.metadata;


import java.io.Serializable;
import java.util.List;


/**
 * A dependency specified in the Depends list of a control file.
 */
public interface Dependency extends Serializable
{
    String getPackage();


    /**
     * This represents an alternative dependency that seems to be a package name only
     * reference as an OR'd class of package to use.
     *
     * NOTE: if this is OR'd alternative is a full dependency reference and a dependency
     * can have multiple OR'd values then it makes sense to make this a reference to
     * another dependency making this property more of a linked list of OR'd dependencies
     * in a dependency class.
     *
     * @todo Need to look deeper into this. One would think this would be a full dependency?
     * @return the alternative dependency package name
     */
    List<Dependency> getAlternatives();


    String getVersion();
    RelationOperator getDependencyOperator();
}
