package ai.subut.kurjun.model;


import java.net.URL;
import java.util.List;


/**
 * Interface for package meta data.
 */
public interface PkgMeta
{
    String getPackage();

    String getVersion();

    String getMaintainer();

    Arch getArchitecture();

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

    List<Dependency> getSuggests();

    List<Dependency> getConflicts();

    List<Dependency> getBreaks();

    List<Dependency> getReplaces();

    List<String> getProvides();

    String getSection();

    Priority getPriority();

    URL getHomepage();

    String getDescription();
}
