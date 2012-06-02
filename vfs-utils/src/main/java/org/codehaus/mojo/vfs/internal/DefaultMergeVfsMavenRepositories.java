package org.codehaus.mojo.vfs.internal;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.codehaus.mojo.vfs.MergeVfsMavenRepositories;
import org.codehaus.mojo.vfs.VfsFileSet;
import org.codehaus.mojo.vfs.VfsFileSetManager;
import org.codehaus.mojo.vfs.VfsUtils;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class DefaultMergeVfsMavenRepositories
    implements MergeVfsMavenRepositories
{
    private static final String MAVEN_METADATA = "maven-metadata.xml";

    private static final String IN_PROCESS_MARKER = ".rip";

    public void merge( FileObject sourceRepo, FileObject targetRepo )
        throws FileSystemException, IOException
    {
        FileObject stagingRepo = null;
        try
        {

            stagingRepo = this.createStagingRepo();

            this.stageSource( sourceRepo, stagingRepo );

            this.mergeTargetMetadataToStageMetaData( targetRepo, stagingRepo );

            //this.mergeBackToMainRepo( fromRepe, toRepo, stagingDir );

        }
        catch ( Exception e )
        {
            //throw new MavenRepositoryMergerException( "Unable to merge repositories.", e );
        }
        finally
        {

            if ( stagingRepo != null )
            {
                FileUtils.deleteDirectory( stagingRepo.getName().getPath() );
            }
        }
    }

    private FileObject createStagingRepo()
        throws IOException
    {

        File stagingDir = File.createTempFile( "vfs-merge-", null );
        if ( stagingDir.exists() )
        {
            stagingDir.delete();
        }
        stagingDir.mkdirs();

        return VFS.getManager().resolveFile( stagingDir.getAbsolutePath() );
    }

    private void stageSource( FileObject source, FileObject stagingRepo )
        throws IOException
    {
        VfsFileSet fileset = new VfsFileSet();
        fileset.setSource( source );
        fileset.setDestination( stagingRepo );
        String[] excludes = { ".*/**" }; //exclude repositories runtime file like .nexus .index
        fileset.setExcludes( excludes );

        VfsFileSetManager fileSetManager = new DefaultVfsFileSetManager();
        fileSetManager.copy( fileset );

    }

    private void mergeTargetMetadataToStageMetaData( FileObject targetRepo, FileObject stagingRepo )
        throws IOException, XmlPullParserException
    {

        VfsFileSet fileset = new VfsFileSet();
        fileset.setSource( stagingRepo );
        String[] includes = { "**/" + MAVEN_METADATA };
        fileset.setIncludes( includes );

        VfsFileSetManager fileSetManager = new DefaultVfsFileSetManager();
        List<FileObject> targetMetaFileObjects = fileSetManager.list( fileset );

        // Merge all metadata files
        for ( FileObject sourceMetaFileObject : targetMetaFileObjects )
        {

            String relativeMetaPath = VfsUtils.getRelativePath( stagingRepo, sourceMetaFileObject );

            FileObject targetMetaFile = targetRepo.resolveFile( relativeMetaPath );
            FileObject stagingTargetMetaFileObject = stagingRepo.resolveFile( relativeMetaPath + IN_PROCESS_MARKER );

            try
            {
                stagingTargetMetaFileObject.copyFrom( targetMetaFile, Selectors.SELECT_ALL );
            }
            catch ( FileSystemException e )
            {
                // We don't have an equivalent on the targetRepositoryUrl side because we have something
                // new on the sourceRepositoryUrl side so just skip the metadata merging.
                continue;
            }

            try
            {
                File targetMetaData = new File( stagingTargetMetaFileObject.getName().getPath() );
                File sourceMetaData = new File( sourceMetaFileObject.getName().getPath() );
                MavenMetadataUtils mavenMetadataUtils = new MavenMetadataUtils();
                mavenMetadataUtils.merge( targetMetaData, sourceMetaData );
                targetMetaData.delete();
            }
            catch ( XmlPullParserException e )
            {
                throw new IOException( "Metadata file is corrupt " + sourceMetaFileObject + " Reason: "
                    + e.getMessage() );
            }

        }

    }


}
