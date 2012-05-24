package org.codehaus.mojo.vfs.internal;

import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.codehaus.mojo.vfs.VfsDirectoryScanner;
import org.codehaus.mojo.vfs.VfsFileSet;
import org.codehaus.mojo.vfs.VfsFileSetManager;

public class DefaultVfsFileSetManager
    implements VfsFileSetManager
{

    public List<FileObject> list( VfsFileSet fileSet )
        throws FileSystemException
    {
        VfsDirectoryScanner scanner = new DefaultVfsDirectoryScanner();
        scanner.setIncludes( fileSet.getIncludes() );
        scanner.setExcludes( fileSet.getExcludes() );
        scanner.setStartingDirectory( fileSet.getDirectory() );
        scanner.setCaseSensitive( fileSet.isCaseSensitive() );
        return scanner.scan();
    }

    public void delete( VfsFileSet fileSet )
        throws FileSystemException
    {
        // TODO Auto-generated method stub

    }

    public void move( VfsFileSet fileSet )
        throws FileSystemException
    {
        // TODO Auto-generated method stub

    }

    public void copy( VfsFileSet fileSet )
        throws FileSystemException
    {
        // TODO Auto-generated method stub

    }

}
