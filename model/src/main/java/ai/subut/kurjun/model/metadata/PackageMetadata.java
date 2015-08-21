package ai.subut.kurjun.model.metadata;


import java.io.Serializable;
import java.net.URL;
import java.util.List;


/**
 * Interface for package meta data.
 */
public interface PackageMetadata extends Serializable
{
    String PACKAGE_FIELD = "Package";
    String VERSION_FIELD = "Version";
    String MAINTAINER_FIELD = "Maintainer";
    String ARCHITECTURE_FIELD = "Architecture";
    String INSTALLED_SIZE_FIELD = "Installed-Size";
    String DEPENDS_FIELD = "Depends";
    String BUILD_DEPENDS_FIELD = "Build-Depends";
    String BUILD_DEPENDS_INDEP_FIELD = "Build-Depends-Indep";
    String RECOMMENDS_FIELD = "Recommends";
    String PRE_DEPENDS_FIELD = "Pre-Depends";
    String SUGGESTS_FIELD = "Suggests";
    String CONFLICTS_FIELD = "Conflicts";
    String BREAKS_FIELD = "Breaks";
    String REPLACES_FIELD = "Replaces";
    String PROVIDES_FIELD = "Provides";
    String ENHANCES_FIELD = "Enhances";
    String SECTION_FIELD = "Section";
    String PRIORITY_FIELD = "Priority";
    String HOMEPAGE_FIELD = "Homepage";
    String DESCRIPTION_FIELD = "Description";


    /**
     * Gets the md5 sum for the Debian package file.
     *
     * @return the md5 sum for the Debian package file
     */
    byte[] getMd5Sum();


    /**
     * Gets the file name of the Debian package file.
     *
     * @return the file name of the Debian package file
     */
    String getFilename();

    String getPackage();

    String getVersion();

    String getMaintainer();

    Architecture getArchitecture();

    /**
     * Installed size in kilobytes. Corresponds to the Installed-Size field in the control file.
     *
     * @return installed size in kb
     */
    int getInstalledSize();

    /**
     * Gets a list of Dependency objects. Corresponds to the Depends field in the control file.
     *
     * @return the Depends property values as a List of Dependency objects
     */
    List<Dependency> getDependencies();

    List<Dependency> getRecommends();

    List<Dependency> getSuggests();

    List<Dependency> getEnhances();

    List<Dependency> getPreDepends();

    List<Dependency> getConflicts();

    List<Dependency> getBreaks();

    List<Dependency> getReplaces();

    List<String> getProvides();

    String getSection();

    Priority getPriority();

    URL getHomepage();

    String getDescription();
}
