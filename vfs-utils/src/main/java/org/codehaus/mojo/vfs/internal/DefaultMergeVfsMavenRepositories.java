package org.codehaus.mojo.vfs.internal;

import java.io.File;
import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.codehaus.mojo.vfs.MergeVfsMavenRepositories;
import org.codehaus.mojo.vfs.VfsFileSet;
import org.codehaus.mojo.vfs.VfsFileSetManager;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class DefaultMergeVfsMavenRepositories
    implements MergeVfsMavenRepositories
{
    private static final String MAVEN_METADATA = "maven-metadata.xml";

    private static final String IN_PROCESS_MARKER = ".rip";

    private static final String MD5 = "md5";

    private static final String SHA1 = "sha1";

    public void merge( FileObject fromRepo, FileObject toRepo )
        throws FileSystemException, IOException
    {
        File stagingDir = null;
        try
        {

            stagingDir = this.createStagingDir();

            this.stageSourceMetadata( fromRepo, stagingDir );

            this.mergeTargetMetadataToStageMetaData( toRepo, stagingDir );

            //this.mergeBackToMainRepo( fromRepe, toRepo, stagingDir );

        }
        catch ( Exception e )
        {
            //throw new MavenRepositoryMergerException( "Unable to merge repositories.", e );
        }
        finally
        {

            if ( stagingDir != null )
            {
                FileUtils.deleteDirectory( stagingDir );
            }
        }
    }

    private File createStagingDir()
        throws IOException
    {

        File stagingDir = File.createTempFile( "merge-", null );
        if ( stagingDir.exists() )
        {
            stagingDir.delete();
        }
        stagingDir.mkdirs();

        return stagingDir;
    }

    private void stageSourceMetadata( FileObject source, File stagingDir )
        throws IOException
    {
        FileObject destination = VFS.getManager().resolveFile( stagingDir.getAbsolutePath() );
        
        VfsFileSet fileset = new VfsFileSet();
        fileset.setSource( source );
        fileset.setDestination( destination );
        String [] includes =  { "**/" + MAVEN_METADATA };
        fileset.setIncludes( includes );
        
        VfsFileSetManager fileSetManager = new DefaultVfsFileSetManager();
        fileSetManager.copy( fileset );

    }
    
    private void mergeTargetMetadataToStageMetaData( FileObject targetRepo, File stagingDir )
        throws IOException, XmlPullParserException {

        
        // Merge all metadata files
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir( stagingDir );
        String[] includes = { "**/" + MAVEN_METADATA };
        scanner.setIncludes( includes );
        scanner.scan();
        String[] files = scanner.getIncludedFiles();

        for ( int i = 0; i < files.length; ++i ) {

            File srcMetadaFile = new File( stagingDir, files[i] + IN_PROCESS_MARKER );

            File destMetadataFile = new File( stagingDir, files[i] );
            if ( destMetadataFile.exists() ) {
                FileUtils.copyFile( destMetadataFile, srcMetadaFile );
            }

            //mergeMetadata( srcMetadaFile );

        }
        
    }

}
