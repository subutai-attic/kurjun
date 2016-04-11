package ai.subut.kurjun.ar;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.compress.archivers.ar.ArArchiveEntry;

/**
 * Interface for Ar (archive) commands.
 */
public interface Ar {
    List<ArArchiveEntry> list() throws IOException;
    void extract( File extractTo, ArArchiveEntry entry ) throws IOException;
}
