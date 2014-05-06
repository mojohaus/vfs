package com.codehaus.mojo.vfs;

public class FtpSettings
{
    /**
     * enter into passive mode.
     *
     * @param opts The FileSystemOptions.
     * @param passiveMode true if passive mode should be used.
     */
    private boolean passiveMode = true;
    
 
    public boolean isPassiveMode()
    {
        return passiveMode;
    }

    public void setPassiveMode( boolean passiveMode )
    {
        this.passiveMode = passiveMode;
    }
    
    /**
     * use user directory as root (do not change to fs root).
     *
     * @param opts The FileSystemOptions.
     * @param userDirIsRoot true if the user directory should be treated as the root.
     */    
    private boolean userDirIsRoot = true;

    public boolean isUserDirIsRoot()
    {
        return userDirIsRoot;
    }

    public void setUserDirIsRoot( boolean userDirIsRoot )
    {
        this.userDirIsRoot = userDirIsRoot;
    }

    
}