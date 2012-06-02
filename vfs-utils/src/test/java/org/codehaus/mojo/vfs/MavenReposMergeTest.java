package org.codehaus.mojo.vfs;

import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.codehaus.mojo.vfs.internal.DefaultVfsFileSetManager;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

public class MavenReposMergeTest
    extends AbstractVfsTestCase
{
    private VfsFileSetManager filesetManager = new DefaultVfsFileSetManager();

    @Test
    public void testMavenReposMerge()
        throws Exception
    {

        File testDirectory = new File( builddir, "test-repos" );
        FileUtils.deleteDirectory( testDirectory );

        FileObject scmSourceRepo = VFS.getManager()
            .resolveFile( "file://" + new File( basedir.getAbsoluteFile(), "src/test/data/repos/source" ) );

        FileObject scmTargetRepo = VFS.getManager()
            .resolveFile( "file://" + new File( basedir.getAbsoluteFile(), "src/test/data/repos/target" ) );

        FileObject testSourceRepo = VFS.getManager()
            .resolveFile( "file://" + new File( builddir.getAbsoluteFile(), "test-repos/source" ) );

        FileObject testTargetRepo = VFS.getManager()
            .resolveFile( "file://" + new File( builddir.getAbsoluteFile(), "test-repos/target" ) );

        VfsFileSet fileset = new VfsFileSet();
        String[] allFiles = { "**" };
        fileset.setIncludes( allFiles );
        String[] hiddenFiles = { "**/.*/**" };
        fileset.setExcludes( hiddenFiles );

        //prep source repo
        fileset.setSource( scmSourceRepo );
        fileset.setDestination( testSourceRepo );
        filesetManager.copy( fileset );
        
        //prep target repo
        fileset.setSource( scmTargetRepo );
        fileset.setDestination( testTargetRepo );
        filesetManager.copy( fileset );

    }
}
