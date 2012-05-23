package org.codehaus.mojo.vfs;

import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

public interface VFSUtils
{
    List<FileObject> getFileList( FileObject startDirectory, String[] includes, String[] excludes )
        throws FileSystemException;

}
