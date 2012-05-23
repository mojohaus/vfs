package org.codehaus.mojo.vfs;

import java.util.List;

import junit.framework.Assert;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.junit.Test;

public class VfsUtilsTest
    extends AbstractVfsTestCase
{

    @Test
    public void testLocalFileListWithIncludes()
        throws Exception
    {
        String url = "file://" + basedir.getCanonicalPath();
        
        FileSystemManager fsManager = VFS.getManager();
        
        FileObject startDirectory = fsManager.resolveFile( url );
        

        VFSUtils vfsUtils = new DefaultVFSUtils();

        String[] includes = { "**/pom.xml" };

        List<FileObject> fos = vfsUtils.getFileList( startDirectory, includes, null );
        Assert.assertTrue( fos.size() == 1 );

        fos = vfsUtils.getFileList( startDirectory, includes, includes );
        Assert.assertTrue( fos.size() == 0 );

        includes[0] = "pom.xml";
        fos = vfsUtils.getFileList( startDirectory, includes, null );
        Assert.assertTrue( fos.size() == 1 );

        includes[0] = "**/V*UtilsTest.j*va";
        fos = vfsUtils.getFileList( startDirectory, includes, null );
        Assert.assertTrue( fos.size() == 1 );

        includes[0] = "V*UtilsTest.j*va";
        fos = vfsUtils.getFileList( startDirectory, includes, null );
        Assert.assertTrue( fos.size() == 0 );

    }

    @Test
    public void testLocalFileListWithExclude()
        throws Exception
    {
        String url = "file://" + basedir.getCanonicalPath();
        
        FileSystemManager fsManager = VFS.getManager();
        
        FileObject startDirectory = fsManager.resolveFile( url );
        
        VFSUtils vfsUtils = new DefaultVFSUtils();

        String[] excludes = { "**/target/", "**/src/" };

        List<FileObject> fos = vfsUtils.getFileList( startDirectory, null, excludes );
        Assert.assertTrue( fos.size() > 0 );
        for ( FileObject fo: fos ) {
            Assert.assertFalse(  fo.getName().getPath().contains( "/target/" ) );
            Assert.assertFalse(  fo.getName().getPath().contains( "/src/" ) );
        }

    }

}
