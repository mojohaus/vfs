package com.loglogic.mojo.vfs;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.codehaus.plexus.util.StringUtils;

public class FileSystemOptionsFactory
{
    public  FileSystemOptions getFileSystemOptions( String url, String username, String password )
        throws FileSystemException
    {
        
        FileSystemOptions opts = new FileSystemOptions();     
        
        String [] tokens = StringUtils.split( url, ":" );
        
        String protocol = tokens[0];
        
        if ( "ftp".equals( protocol ) ) {
            FtpFileSystemConfigBuilder builder = FtpFileSystemConfigBuilder.getInstance();
            builder.setPassiveMode( opts, true );
        }

        String domain = null;
        tokens = StringUtils.split( "\\" );
        if ( tokens.length == 2 ) {
            domain = tokens[0];
            username = tokens[1];
        }
        
        StaticUserAuthenticator auth = new StaticUserAuthenticator( domain, username, password );

        DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator( opts, auth );
        
        return opts;
    }
}