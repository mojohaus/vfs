package org.codehaus.mojo.vfs;

import java.io.File;
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
    void merge( FileObject fromRepo, FileObject toRepo  )
        throws FileSystemException, IOException;

    
    /**
     * Merge Maven repositories with option to do dryRun by not push the merge to target repo
     * @param fromRepo
     * @param toRepo
     * @param stagingDirectory if not given( null ) a temporary one will be created
     * @param dryRun when true, dont push to target repository and leave staging alone to review
     * @throws FileSystemException
     */
    void merge( FileObject fromRepo, FileObject toRepo, File stagingDirectory, boolean dryRun )
        throws FileSystemException, IOException;

}
