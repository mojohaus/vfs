package com.loglogic.mojo.vfs;

import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.vfs.MergeVfsMavenRepositories;
import org.codehaus.mojo.vfs.internal.DefaultMergeVfsMavenRepositories;

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

/**
 * Copy files from one VFS to another VFS
 * 
 * @goal merge-maven-repositories
 * @requiresProject false
 */
public class MergeMavenReposVfsMojo
    extends AbstractVfsMojo
{

    /**
     * Source URL
     * @parameter expression = "${source}"
     * @required
     * @since 1.0
     */
    private String source;

    /**
     * Maven settings server's source authentication id
     * @parameter expression = "${sourceId}"
     */
    private String sourceId;

    /**
     * Destination URL
     * @parameter expression = "${destination}"
     * @required
     * @since 1.0
     */
    private String destination;

    /**
     * Maven settings server's source authentication id
     * @parameter expression = "${destinationId}"
     */
    private String destinationId;

    /**
     * Staging directory when the merge content happens
     * @parameter expression = "${stagingDirectory}" default-value="${project.build.directory}/merge-staging"
     */
    private File stagingDirectory;

    /**
     * Option not to push merged content to destination repository so that you can review the merge content under ${stagingDirectory}
     * @parameter expression = "${dryRun}" default-value="false"
     */
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

                FileObject sourceRepo = VFS.getManager().resolveFile( source, sourceOpts );

                FileObject destRepo = VFS.getManager().resolveFile( destination, destOpts );

                MergeVfsMavenRepositories repoMerger = new DefaultMergeVfsMavenRepositories();

                repoMerger.merge( sourceRepo, destRepo, stagingDirectory, dryRun );

            }
            catch ( Exception e )
            {
                throw new MojoFailureException( "Unable to perform a Repositories merge operation", e );
            }
        }
        else
        {
            this.getLog().info( "Skip VFS Maven Repositories merge operation" );
        }

    }
}