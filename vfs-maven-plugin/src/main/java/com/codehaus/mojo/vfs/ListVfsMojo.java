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

import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.mojo.vfs.VfsFileSet;
import org.codehaus.mojo.vfs.VfsFileSetManager;
import org.codehaus.mojo.vfs.VfsUtils;
import org.codehaus.mojo.vfs.internal.DefaultVfsFileSetManager;
import org.codehaus.plexus.util.StringUtils;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

/**
 * Display file list of a virtual file system.
 */
@Mojo( name = "list", requiresProject = false, threadSafe = true )
public class ListVfsMojo
    extends AbstractVfsMojo
{
    /**
     * Source URL
     * @since 1.0 beta 1
     */
    @Parameter( property = "source", required = true )
    private String source;

    /**
     * Maven settings server's source authentication id
     * @since 1.0 beta 1
     */
    @Parameter( property = "sourceId", required = false )
    private String sourceId;

    /**
     * Comma separated ANT include format
     * @since 1.0 beta 1
     */
    @Parameter( property = "includes", required = false )
    private String includes;

    /**
     * Comma separated ANT exclude format
     * @since 1.0 beta 1
     */
    @Parameter( property = "excludes", required = false )
    private String excludes;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( skip )
        {
            this.getLog().info( "Skip VFS list operation" );
            return;
        }

        MojoVfsFileSet fileset = new MojoVfsFileSet();

        fileset.setSource( source );
        fileset.setSourceId( sourceId );

        if ( !StringUtils.isBlank( includes ) )
        {
            fileset.setIncludes( StringUtils.split( includes, "," ) );
        }

        if ( !StringUtils.isBlank( excludes ) )
        {
            fileset.setExcludes( StringUtils.split( excludes, "," ) );
        }

        try
        {
            FileSystemOptions serverOptions = this.getFileSystemOptions( sourceId, source );
            VfsFileSet vfsFileSet = new VfsFileSet();
            vfsFileSet.copyBase( fileset );

            FileObject sourceObj = getFileSystemManager().resolveFile( fileset.getSource(), serverOptions );
            vfsFileSet.setSource( sourceObj );

            VfsFileSetManager fileSetManager = new DefaultVfsFileSetManager();
            List<FileObject> list = fileSetManager.list( vfsFileSet );

            this.getLog().info( "Directory list: " );
            for ( FileObject fo : list )
            {
                this.getLog().info( VfsUtils.getRelativePath( sourceObj, fo ) );
            }
        }
        catch ( FileSystemException e )
        {
            throw new MojoFailureException( "Unable to perform a list operation", e );
        }
        catch ( SecDispatcherException e )
        {
            throw new MojoFailureException( "Unable to perform a list operation", e );
        }
    }
}
