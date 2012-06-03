package org.codehaus.mojo.vfs;

import java.io.File;

import junit.framework.Assert;

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

    public void runTestMavenReposMerge( boolean dryRun )
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

        repoMerger.merge( testSourceRepo, testTargetRepo, stagingDir, dryRun );

    }

    @Test
    public void testMavenReposMerge()
        throws Exception
    {
        this.runTestMavenReposMerge( false );

        File targetDir = new File( builddir, "test-repos/target" );

        Assert.assertTrue( new File( targetDir, "com/company/package/artifact1/3.0/artifact1-3.0.pom" ).exists() );
        Assert.assertTrue( new File( targetDir, "com/company/package/artifact1/maven-metadata.xml" ).exists() );
        Assert.assertTrue( new File( targetDir, "com/company/package/artifact1/maven-metadata.xml.md5" ).exists() );
        Assert.assertTrue( new File( targetDir, "com/company/package/artifact1/maven-metadata.xml.sha1" ).exists() );

        Assert.assertTrue( new File( targetDir, "com/company/package/artifact3/1.0/artifact3-1.0.pom" ).exists() );
        Assert.assertTrue( new File( targetDir, "com/company/package/artifact3/maven-metadata.xml" ).exists() );

        //should not see hashfile since it is a brand new artifacts
        Assert.assertFalse( new File( targetDir, "com/company/package/artifact3/maven-metadata.xml.md5" ).exists() );
        Assert.assertFalse( new File( targetDir, "com/company/package/artifact3/maven-metadata.xml.sha1" ).exists() );

        File stagingDir = new File( builddir, "test-repos/staging" );
        Assert.assertFalse( stagingDir.exists() );

    }

    @Test
    public void testMavenReposMergeWithDryrun()
        throws Exception
    {
        this.runTestMavenReposMerge( true );

        File stagingDir = new File( builddir, "test-repos/staging" );
        Assert.assertTrue( stagingDir.exists() );

    }

}
