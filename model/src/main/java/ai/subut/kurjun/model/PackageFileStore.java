package ai.subut.kurjun.model;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * A md5 sum based flat key-value Debian package store.
 */
public interface PackageFileStore
{
    /**
     * Checks to see if a Debian package with the specified md5 sum exists
     * within this PkgStore.
     *
     * @param md5 the md5 sum of the package
     * @return true if a Debian package having the same md5sum exists, false otherwise
     * @throws IOException if there are problems accessing the store
     */
    boolean contains( byte[] md5 ) throws IOException;


    /**
     * Gets the contents of a Debian package from the store as a stream.
     *
     * @param md5 the md5 sum of the package
     * @return null if the Debian package does not exist, otherwise returns a
     * stream containing the contents of the package
     * @throws IOException if there are problems access the store
     */
    InputStream get( byte[] md5 ) throws IOException;


    /**
     * Gets the contents of a Debian package from the store and dumps it into a File.
     *
     * @param md5 the md5 sum of the package
     * @param target the file into which the contents should be dumped
     * @return true if the contents were successfully dumped, false otherwise
     * @throws IOException if there are problems accessing the store or writing to
     * the target file
     */
    boolean get( byte[] md5, File target ) throws IOException;


    /**
     * Puts a source Debian package file into the store.
     *
     * @param source the source Debian package file
     * @return the md5 sum of the package
     * @throws IOException if there are problems accessing the store or the source file
     */
    byte[] put( File source ) throws IOException;


    /**
     * Puts a source Debian package specified by a URL into the store.
     *
     * @param source the source Debian package URL
     * @return the md5 sum of the package
     * @throws IOException if there are problems accessing the store or the source package
     */
    byte[] put( URL source ) throws IOException;


    /**
     * Puts a source Debian package into the store.
     *
     * @param source the source Debian package content stream
     * @return the md5 sum of the package
     * @throws IOException if there are problems accessing the store or the source package
     */
    byte[] put( InputStream source ) throws IOException;


    /**
     * Removes a Debian package from the store.
     *
     * @param md5 the md5 sum of the package to remove
     * @return true if the package was removed, false if no such package exists
     * @throws IOException if there are problems accessing the store
     */
    boolean remove( byte[] md5 ) throws IOException;
}
