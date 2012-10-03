package com.loglogic.mojo.vfs;

public class SftpSettings
{
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
