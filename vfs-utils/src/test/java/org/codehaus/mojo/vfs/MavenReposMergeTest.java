package org.codehaus.mojo.vfs;

import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.codehaus.mojo.vfs.internal.DefaultMergeVfsMavenRepositories;
import org.codehaus.mojo.vfs.internal.DefaultVfsFileSetManager;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

public class MavenReposMergeTest
    extends AbstractVfsTestCase
{
    private VfsFileSetManager filesetManager = new DefaultVfsFileSetManager();

    private MergeVfsMavenRepositories repoMerger = new DefaultMergeVfsMavenRepositories();
    
    @Test
    public void testMavenReposMerge()
        throws Exception
    {

        basedir = basedir.getAbsoluteFile();
        builddir = builddir.getAbsoluteFile();

        File testDirectory = new File( builddir, "test-repos" );
        FileUtils.deleteDirectory( testDirectory );

        FileObject scmSourceRepo = VFS.getManager()
            .resolveFile( "file://" + new File( basedir, "src/test/data/repos/source" ) );

        FileObject scmTargetRepo = VFS.getManager()
            .resolveFile( "file://" + new File( basedir, "src/test/data/repos/target" ) );

        FileObject testSourceRepo = VFS.getManager()
            .resolveFile( "file://" + new File( builddir, "test-repos/source" ) );

        FileObject testTargetRepo = VFS.getManager()
            .resolveFile( "file://" + new File( builddir, "test-repos/target" ) );

        VfsFileSet fileset = new VfsFileSet();
        String[] allFiles = { "**" };
        fileset.setIncludes( allFiles );
        String[] hiddenFiles = { "**/.*/**" }; //dont want local svn metadata file sneaking in
        fileset.setExcludes( hiddenFiles );

        //prep source repo
        fileset.setSource( scmSourceRepo );
        fileset.setDestination( testSourceRepo );
        filesetManager.copy( fileset );

        //prep target repo
        fileset.setSource( scmTargetRepo );
        fileset.setDestination( testTargetRepo );
        filesetManager.copy( fileset );
        
        File stagingDir = new File( builddir, "test-repos/staging" );
        
        repoMerger.merge( testSourceRepo, testTargetRepo, stagingDir, true );

    }
}
