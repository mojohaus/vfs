package org.codehaus.mojo.vfs;


import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

public class DefaultVFSUtils
    implements VFSUtils
{

    public List<FileObject> getFileList( FileObject startDirectory, String[] includes, String[] excludes )
        throws FileSystemException
    {

        VfsDirectoryScanner dirScanner = new VfsDirectoryScanner();
        dirScanner.setStartingDirectory( startDirectory );
        dirScanner.setIncludes( includes );
        dirScanner.setExcludes( excludes );
        return dirScanner.scan();
        
    }

}
