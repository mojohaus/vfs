package com.loglogic.mojo.vfs;

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
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.codehaus.plexus.util.StringUtils;

public class FileSystemOptionsFactory
{
    public FileSystemOptions getFileSystemOptions( String url, String username, String password )
        throws FileSystemException
    {

        FileSystemOptions opts = new FileSystemOptions();

        String[] tokens = StringUtils.split( url, ":" );

        String protocol = tokens[0];

        if ( "ftp".equals( protocol ) )
        {
            FtpFileSystemConfigBuilder builder = FtpFileSystemConfigBuilder.getInstance();
            builder.setPassiveMode( opts, true );
            builder.setUserDirIsRoot( opts, false );
        }
        if ( "sftp".equals( protocol ) )
        {
            SftpFileSystemConfigBuilder builder = SftpFileSystemConfigBuilder.getInstance();
            builder.setUserDirIsRoot( opts, false );
        }

        String domain = null;
        tokens = StringUtils.split( "\\" );
        if ( tokens.length == 2 )
        {
            domain = tokens[0];
            username = tokens[1];
        }

        StaticUserAuthenticator auth = new StaticUserAuthenticator( domain, username, password );

        DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator( opts, auth );

        return opts;
    }

}
