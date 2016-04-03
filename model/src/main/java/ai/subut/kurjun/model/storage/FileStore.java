package ai.subut.kurjun.model.storage;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * A md5 sum based flat key-value file store.
 */
public interface FileStore
{
    /**
     * Checks to see if a file with the specified md5 sum exists.
     *
     * @param md5 the md5 sum of the file
     *
     * @return true if a file having the same md5sum exists, false otherwise
     *
     * @throws IOException if there are problems accessing the store
     */
    boolean contains( String md5 ) throws IOException;


    /**
     * Gets the contents of a file from the store as a stream.
     *
     * @param md5 the md5 sum of the file
     *
     * @return null if the file does not exist, otherwise returns a stream containing the contents of the file
     *
     * @throws IOException if there are problems access the store
     */
    InputStream get( String md5 ) throws IOException;


    /**
     * Gets the contents of a file from the store and dumps it into local file.
     *
     * @param md5 the md5 sum of the file to retrieve
     * @param target the file into which the contents should be dumped
     *
     * @return true if the contents were successfully dumped, false otherwise
     *
     * @throws IOException if there are problems accessing the store or writing to the target file
     */
    boolean get( String md5, File target ) throws IOException;


    /**
     * Puts a source file into the store.
     *
     * @param source the source file
     *
     * @return the md5 sum of the file
     *
     * @throws IOException if there are problems accessing the store or the source file
     */
    String put( File source ) throws IOException;

    /**
     * Puts a source file into the store.
     *
     * @param source the source file
     * @return the md5 sum of the file
     *
     * @throws IOException if there are problems accessing the store or the source file
     */
    byte[] put( InputStream source ) throws IOException;


    /**
     * Puts a source file specified by a URL into the store.
     *
     * @param source the source file URL
     *
     * @return the md5 sum of the file
     *
     * @throws IOException if there are problems accessing the store or the source file
     */
    String put( URL source ) throws IOException;


    /**
     * Puts a source file into the store.
     *
     * @param filename the name of the file
     * @param source the source content stream
     *
     * @return the md5 sum of the file
     *
     * @throws IOException if there are problems accessing the store or the source content
     */
    String put( String filename, InputStream source ) throws IOException;


    /**
     * Removes a file from the store.
     *
     * @param md5 the md5 sum of the file to remove
     *
     * @return true if the file was removed, false if no such file exists
     *
     * @throws IOException if there are problems accessing the store
     */
    boolean remove( String md5 ) throws IOException;


    /**
     * Returns total size (in bytes) of the files stored in this store.
     *
     * @return total size of the store (in bytes)
     */
    long size() throws IOException;


    /**
     * Returns size (in bytes) of the package file that corresponds to the supplied digest value.
     *
     * @param md5 md5 digest to look file for
     *
     * @return size of the package file in bytes if found; otherwise - 0L
     */
    long sizeOf( String md5 ) throws IOException;

//    /**
//     * Returns UUID based filename
//     *
//     * @return uuid without - chars
//     */
//    String filename();
//
//    /**
//     * Creates Path from given filename
//     *
//     * @return absolute path
//     */
//    Path createPath( String filename );
}

