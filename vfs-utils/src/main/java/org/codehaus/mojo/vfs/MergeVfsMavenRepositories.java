package org.codehaus.mojo.vfs;

import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

public interface MergeVfsMavenRepositories
{
    /**
     * Merge Maven repositories
     * @param fromRepo
     * @param toRepo
     * @throws FileSystemException
     */
    void merge( FileObject fromRepo, FileObject toRepo )
        throws FileSystemException, IOException;

}
