package com.loglogic.mojo.vfs;

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

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

/**
 * Provides base functionality for dealing with I/O using VFS.
 * 
 */
public abstract class AbstractVfsMojo
    extends AbstractMojo
{

    /**
     * The current user system settings for use in Maven.
     * 
     * @parameter expression="${settings}"
     * @readonly
     */
    protected Settings settings;

    /**
     * Internal Maven's project
     * 
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * When <code>true</code>, skip the execution.
     * 
     * @parameter expression="${vfs.skip}" default-value="false"
     */
    protected boolean skip = false;

    /**
     * MNG-4384
     * 
     * @component role="hidden.org.sonatype.plexus.components.sec.dispatcher.SecDispatcher"
     * @required
     * @since 1.0
     */
    private SecDispatcher securityDispatcher;

    protected Server getMavenSettingServer( String serverId )
        throws MojoExecutionException, MojoFailureException
    {
        Server server = this.settings.getServer( serverId );

        if ( server != null )
        {
            if ( server.getPassword() != null )
            {
                try
                {
                    server.setPassword( securityDispatcher.decrypt( server.getPassword() ) );
                }
                catch ( SecDispatcherException e )
                {
                    throw new MojoExecutionException( e.getMessage() );
                }
            }
        }

        return server;
    }

    protected FileSystemOptions getAuthenticationOptions( String serverId )
        throws MojoExecutionException, MojoFailureException
    {
        Server server = this.getMavenSettingServer( serverId );

        FileSystemOptions opts = null;

        if ( server != null )
        {
            StaticUserAuthenticator auth = new StaticUserAuthenticator( null, server.getUsername(), server.getPassword() );
            opts = new FileSystemOptions();

            try
            {
                DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator( opts, auth );
            }
            catch ( FileSystemException e )
            {
                throw new MojoFailureException(
                                                "Unable to configure virtual file system authentication options using server id: "
                                                    + serverId, e );
            }
        }

        return opts;
    }

}