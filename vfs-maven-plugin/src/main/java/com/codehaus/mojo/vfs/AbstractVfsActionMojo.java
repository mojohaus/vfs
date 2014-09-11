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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractVfsActionMojo
    extends AbstractVfsMojo
{

    /**
     * VFS configuration using single fileset
     *
     * @since 1.0 beta 1
     */
    @Parameter( required = false )
    protected MojoVfsFileSet fileset;

    /**
     * VFS configuration using multiple filesets
     *
     * @since 1.0
     */
    @Parameter( required = false )
    protected List<MojoVfsFileSet> filesets;

    protected boolean initialize()
    {
        if ( this.skip )
        {
            this.getLog().info( "Skip VFS action" );
            return false;
        }

        if ( filesets == null )
        {
            filesets = new ArrayList<MojoVfsFileSet>();
        }

        if ( fileset != null )
        {
            filesets.add( fileset );
        }

        if ( filesets.isEmpty() )
        {
            this.getLog().info( "Skip VFS action due to empty configuration." );
            return false;
        }

        return true;
    }

}