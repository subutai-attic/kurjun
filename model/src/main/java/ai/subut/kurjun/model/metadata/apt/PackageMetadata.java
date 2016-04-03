package ai.subut.kurjun.model.metadata.apt;


import java.net.URL;
import java.util.List;

import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;


/**
 * Interface for package meta data.
 */
public interface PackageMetadata extends Metadata
{
    String PACKAGE_FIELD = "Package";
    String VERSION_FIELD = "Version";
    String SOURCE_FIELD = "Source";
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
    @Override
    String getMd5Sum();


    /**
     * Gets component or area of the repository this package metadata comes from. For example, main, contrib, non-free
     * are common components in Debian ecosystem.
     * <p>
     * Binary package indices of component $COMP are located under dists/$DIST/$COMP/binary-$arch subdirectory.
     * <p>
     * Refer to https://www.debian.org/doc/debian-policy/ch-archive.html#s-subsections for more info.
     *
     * @return component name this package belongs to
     */
    String getComponent();

    /**
     * Gets the file name of the Debian package file.
     *
     * @return the file name of the Debian package file
     */
    String getFilename();

    String getPackage();

    @Override
    String getVersion();

    String getSource();

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
