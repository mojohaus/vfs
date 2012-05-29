package com.loglogic.mojo.vfs;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.vfs.VfsFileSet;
import org.codehaus.mojo.vfs.VfsFileSetManager;
import org.codehaus.mojo.vfs.internal.DefaultVfsFileSetManager;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

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
 * @goal copy
 * @requiresProject true
 */
public class CopyVfsMojo
    extends AbstractVfsMojo
{

    /**
     * A single FileSet to manipulate the archive.
     *
     * @parameter
     * @since 1.0
     */
    private MojoVfsFileSet fileset;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        if ( fileset != null )
        {
            try
            {
                FileSystemOptions sourceOpts = this.getFileSystemOptions( fileset.getSourceId(), fileset.getSource() );
                FileSystemOptions destOpts = this.getFileSystemOptions( fileset.getDestinationId(),
                                                                        fileset.getDestination() );

                VfsFileSet vfsFileSet = new VfsFileSet();
                vfsFileSet.copyBase( fileset );

                FileObject sourceObj = VFS.getManager().resolveFile( fileset.getSource(), sourceOpts );
                vfsFileSet.setSource( sourceObj );

                FileObject destObj = VFS.getManager().resolveFile( fileset.getDestination(), destOpts );
                vfsFileSet.setDestination( destObj );

                VfsFileSetManager fileSetManager = new DefaultVfsFileSetManager();
                fileSetManager.copy( vfsFileSet );
            }
            catch ( FileSystemException e )
            {
                throw new MojoFailureException( "Unable to perform copy", e );
            }
            catch ( SecDispatcherException e )
            {
                throw new MojoFailureException( "Unable to perform a list operation", e );
            }
        }

    }
}