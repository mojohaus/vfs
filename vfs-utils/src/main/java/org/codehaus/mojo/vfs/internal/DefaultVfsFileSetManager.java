package org.codehaus.mojo.vfs.internal;

import java.util.List;

import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileName;
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
        
        //better to use FileObject.move()??
        copy( fileSet.getDirectory(), fileSet.getOutputDirectory(), fos );

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

        copy( fileSet.getDirectory(), fileSet.getOutputDirectory(), fos );
    }

    private void copy( FileObject fromDir, FileObject toDir, List<FileObject> fromFiles )
        throws FileSystemException
    {

        FileName baseName = fromDir.getName();
        for ( FileObject fromFile : fromFiles )
        {
            String relPath = baseName.getRelativeName( fromFile.getName() );
            FileObject toFile = toDir.resolveFile( relPath );
            toFile.copyFrom( fromFile, new AllFileSelector() );
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
