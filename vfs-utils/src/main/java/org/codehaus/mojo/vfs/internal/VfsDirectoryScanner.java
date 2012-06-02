package org.codehaus.mojo.vfs.internal;

import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

public interface VfsDirectoryScanner
{

    void setStartingDirectory( FileObject directory );

    /**
     * Sets the list of include patterns to use. All '/' and '\' characters are replaced by
     * <code>File.separatorChar</code>, so the separator used need not match
     * <code>File.separatorChar</code>.
     * <p>
     * When a pattern ends with a '/' or '\', "**" is appended.
     * 
     * @param includes A list of include patterns. May be <code>null</code>, indicating that all
     *            files should be included. If a non-<code>null</code> list is given, all elements
     *            must be non-<code>null</code>.
     */
    void setIncludes( String[] includes );

    /**
     * Sets the list of exclude patterns to use. All '\' characters are replaced by '/'
     * <p>
     * When a pattern ends with a '/' or '\', "**" is appended.
     * 
     * @param excludes A list of exclude patterns. May be <code>null</code>, indicating that no
     *            files should be excluded. If a non-<code>null</code> list is given, all elements
     *            must be non-<code>null</code>.
     */
    void setExcludes( String[] excludes );

    /**
     * 
     * @param isCaseSensitive
     */
    void setCaseSensitive( boolean isCaseSensitive );

    /**
     * Start the scan
     * @return
     * @throws FileSystemException
     */
    List<FileObject> scan()
        throws FileSystemException;

}