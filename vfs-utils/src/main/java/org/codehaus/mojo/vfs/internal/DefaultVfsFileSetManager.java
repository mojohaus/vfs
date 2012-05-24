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
        VfsDirectoryScanner scanner = createScanner( fileSet );
        return scanner.scan();
    }

    public void delete( VfsFileSet fileSet )
        throws FileSystemException
    {
        VfsDirectoryScanner scanner = createScanner( fileSet );
        List<FileObject> fos = scanner.scan();
        for ( FileObject fo : fos )
        {
            //TODO check for return error
            fo.delete();
        }

    }

    public void move( VfsFileSet fileSet )
        throws FileSystemException
    {
        VfsDirectoryScanner scanner = createScanner( fileSet );
        List<FileObject> fos = scanner.scan();
        for ( FileObject fo : fos )
        {
            //        
        }

        for ( FileObject fo : fos )
        {
            fo.delete();
        }

    }

    public void copy( VfsFileSet fileSet )
        throws FileSystemException
    {
        VfsDirectoryScanner scanner = createScanner( fileSet );
        List<FileObject> fos = scanner.scan();
        for ( FileObject fo : fos )
        {
            //TODO check for return error
            // copy from src to dest
        }

    }

    private static VfsDirectoryScanner createScanner( VfsFileSet fileSet )
    {

        VfsDirectoryScanner scanner = new DefaultVfsDirectoryScanner();
        scanner.setIncludes( fileSet.getIncludes() );
        scanner.setExcludes( fileSet.getExcludes() );
        scanner.setStartingDirectory( fileSet.getDirectory() );
        scanner.setCaseSensitive( fileSet.isCaseSensitive() );

        return scanner;
    }

}
