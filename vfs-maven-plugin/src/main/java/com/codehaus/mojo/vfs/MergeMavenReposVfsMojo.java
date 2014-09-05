package com.codehaus.mojo.vfs;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.mojo.vfs.MergeVfsMavenRepositories;
import org.codehaus.mojo.vfs.internal.DefaultMergeVfsMavenRepositories;

/**
 * Merge Maven repository from one VFS to another VFS
 */
@Mojo( name = "merge-maven-repositories", requiresProject = false, threadSafe = true )
public class MergeMavenReposVfsMojo
    extends AbstractVfsMojo
{

    /**
     * Source URL
     *
     * @since 1.0
     */
    @Parameter( property = "source", required = true )
    private String source;

    /**
     * Maven settings server's source authentication id
     */
    @Parameter( property = "sourceId", required = false )
    private String sourceId;

    /**
     * Destination URL
     *
     * @since 1.0
     */
    @Parameter( property = "destination", required = true )
    private String destination;

    /**
     * Maven settings server's source authentication id
     */
    @Parameter( property = "destinationId", required = false )
    private String destinationId;

    /**
     * Staging directory to do the merging works. If not given, and random directory is used
     */
    @Parameter( property = "stagingDirectory", required = false, defaultValue = "${project.build.directory}/merge-staging" )
    private File stagingDirectory;

    /**
     * Option not to push merged content to destination repository so that you can review the merge content under
     * ${stagingDirectory}
     */
    @Parameter( property = "dryRun", required = false, defaultValue = "false" )
    private boolean dryRun = false;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        if ( !this.skip )
        {
            try
            {
                FileSystemOptions sourceOpts = this.getFileSystemOptions( sourceId, source );
                FileSystemOptions destOpts = this.getFileSystemOptions( destinationId, destination );

                FileObject sourceRepo = getFileSystemManager().resolveFile( source, sourceOpts );

                FileObject destRepo = getFileSystemManager().resolveFile( destination, destOpts );

                MergeVfsMavenRepositories repoMerger = new DefaultMergeVfsMavenRepositories();

                repoMerger.merge( sourceRepo, destRepo, stagingDirectory, dryRun );

                if ( dryRun )
                {
                    this.getLog().info( "Merging operetion stopped before pushing the final contents at "
                                            + this.stagingDirectory + destination );
                }

            }
            catch ( Exception e )
            {
                throw new MojoFailureException( "Unable to perform a repositories merge operation", e );
            }
        }
        else
        {
            this.getLog().info( "Skip VFS Maven repositories merge operation" );
        }

    }
}