package org.codehaus.mojo.vfs.internal;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

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

    private static final String[] ALL_FILES = { "**" };

    public void merge( FileObject sourceRepo, FileObject targetRepo )
        throws FileSystemException, IOException
    {
        merge( sourceRepo, targetRepo, null, false );
    }

    public void merge( FileObject sourceRepo, FileObject targetRepo, File stagingDirectory, boolean dryRun )
        throws FileSystemException, IOException
    {
        FileObject stagingRepo = null;
        try
        {
            stagingRepo = this.createStagingRepo( stagingDirectory );

            this.stageSource( sourceRepo, stagingRepo );

            this.mergeTargetMetadataToStageMetaData( targetRepo, stagingRepo );

            if ( !dryRun )
            {
                this.pushStagingToTargetRepo( stagingRepo, targetRepo );
            }

        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Unable to merge repositories.", e );
        }
        finally
        {

            if ( !dryRun )
            {
                if ( stagingRepo != null )
                {
                    FileUtils.deleteDirectory( stagingRepo.getName().getPath() );
                }
            }
        }
    }

    private FileObject createStagingRepo( File stagingDir )
        throws IOException
    {
        if ( stagingDir == null )
        {
            stagingDir = File.createTempFile( "vfs-merge-", null );
        }

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
        fileset.setIncludes( ALL_FILES );

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

    private void pushStagingToTargetRepo( FileObject stagingRepo, FileObject targetRepo )
        throws FileSystemException
    {

        VfsFileSet fileset = new VfsFileSet();
        fileset.setSource( stagingRepo );
        fileset.setIncludes( ALL_FILES );
        fileset.setDestination( targetRepo );

        VfsFileSetManager fileSetManager = new DefaultVfsFileSetManager();
        fileSetManager.copy( fileset );

    }

}
