package com.loglogic.mojo.vfs;

import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.vfs.VfsFileSet;
import org.codehaus.mojo.vfs.VfsFileSetManager;
import org.codehaus.mojo.vfs.VfsUtils;
import org.codehaus.mojo.vfs.internal.DefaultVfsFileSetManager;
import org.codehaus.plexus.util.StringUtils;

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
 * List files on one or more VFS systems
 * 
 * @goal list
 * @requiresProject false
 */
public class ListVfsMojo
    extends AbstractVfsMojo
{
    /**
     * Source URL
     * @parameter expression = "${vfs.source}"
     * @required
     * @since 1.0
     */
    private String source;

    /**
     * Maven settings server's source authentication id
     * @parameter expression = "${vfs.sourceId}"
     */
    private String sourceId;

    /**
     * Comma separated ANT include format
     * @parameter expression = "${vfs.includes}"
     */
    private String includes;

    /**
     * Comma separated ANT exclude format
     * @parameter expression = "${vfs.excludes}"
     */
    private String excludes;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

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

        FileSystemOptions sourceAuthOptions = this.getAuthenticationOptions( fileset.getSourceId() );

        FtpFileSystemConfigBuilder.getInstance().setPassiveMode( sourceAuthOptions, true );

        try
        {
            VfsFileSet vfsFileSet = new VfsFileSet();
            vfsFileSet.copyBase( fileset );

            FileObject sourceObj = VFS.getManager().resolveFile( fileset.getSource(), sourceAuthOptions );

            vfsFileSet.setSource( sourceObj );

            VfsFileSetManager fileSetManager = new DefaultVfsFileSetManager();
            List<FileObject> list = fileSetManager.list( vfsFileSet );

            for ( FileObject fo : list )
            {
                this.getLog().info( VfsUtils.getRelativePath(  sourceObj, fo ) );
            }
        }
        catch ( FileSystemException e )
        {
            throw new MojoFailureException( "Unable to perform a list operation", e );
        }

    }
}