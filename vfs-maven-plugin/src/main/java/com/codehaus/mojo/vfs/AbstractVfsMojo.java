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

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.FileProvider;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

/**
 * Provides base functionality for dealing with I/O using VFS.
 */
public abstract class AbstractVfsMojo
    extends AbstractMojo
{

    /**
     * ftp connection specific settings
     */
    @Parameter( required = false )
    protected FtpSettings  ftpSettings = new FtpSettings();

    /**
     * sftp connection specific settings
     */
    @Parameter( required = false )
    protected SftpSettings  sftpSettings = new SftpSettings();


    /**
     * Current user system settings for use in Maven.
     */
    @Parameter( defaultValue = "${settings}", readonly = true )
    protected Settings settings;

    /**
     * Internal Maven's project
     */
    @Parameter( defaultValue = "${project}", readonly = true )
    protected MavenProject project;

    /**
     * When <code>true</code>, skip the execution.
     *
     */
    @Parameter( property = "skip", defaultValue="false")
    protected boolean skip = false;

    /**
     * MNG-4384
     *
     * @component role="hidden.org.sonatype.plexus.components.sec.dispatcher.SecDispatcher"
     * @required
     * @since 1.0
     */
    private SecDispatcher securityDispatcher;

    protected FileSystemOptionsFactory fileSystemOptionsFactory = new FileSystemOptionsFactory();

    protected FileSystemOptions getFileSystemOptions( String serverId, String sourceUrl )
        throws SecDispatcherException, FileSystemException
    {
        Server serverSettings = this.getServerSettings( serverId );

        this.fileSystemOptionsFactory.setFtpSettings( ftpSettings );
        this.fileSystemOptionsFactory.setSftpSettings( sftpSettings );
        return fileSystemOptionsFactory.getFileSystemOptions( sourceUrl, serverSettings.getUsername(),
                                                              serverSettings.getPassword() );
    }

    private Server getServerSettings( String serverId )
        throws SecDispatcherException
    {
        Server server = this.settings.getServer( serverId );

        if ( server != null )
        {
            if ( server.getPassword() != null )
            {
                server.setPassword( securityDispatcher.decrypt( server.getPassword() ) );
            }
        }
        else
        {
            server = new Server();

            // convenient built-in setting to allow ftp anonymous access
            if ( "ftp.anonymous".equals( "serverId" ) )
            {
                server.setId( "ftp.anonymous" );
                server.setUsername( "anonymous" );
                server.setPassword( "anonymous@anonymous.com" );
            }
        }

        return server;
    }

    private StandardFileSystemManager fileSystemManager;

    protected synchronized FileSystemManager getFileSystemManager()
        throws FileSystemException
    {

        if ( fileSystemManager != null )
        {
            return fileSystemManager;
        }

        fileSystemManager = new StandardFileSystemManager();

        try
        {
            Class<?> smbProviderClass = Class.forName( "org.apache.commons.vfs2.provider.smb.SmbFileProvider" );
            fileSystemManager.addProvider( "smb", (FileProvider) smbProviderClass.newInstance() );
        }
        catch ( Exception e )
        {
            this.getLog().info( "VFS smb/cifs provider not available" );
        }

        fileSystemManager.init();

        return fileSystemManager;
    }

}