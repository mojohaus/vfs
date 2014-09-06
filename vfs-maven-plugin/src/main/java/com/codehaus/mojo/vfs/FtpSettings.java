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
