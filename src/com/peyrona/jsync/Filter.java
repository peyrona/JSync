
package com.peyrona.jsync;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author peyrona
 */
final class Filter implements FileFilter
{
    private final List<String> lstFileExt = new ArrayList<>();    // FileExtensions to be ignored
    private final List<String> lstFolder  = new ArrayList<>();    // Folders to be ignored (as absolute path)
    private       long         nMaxSize   = 0;                    // 0 == no restriction

    //----------------------------------------------------------------------------//
    // BY IMPLEMENTING FileFilter

    @Override
    public boolean accept( final File file )    // Check if file is contained in any of folders to be ignored
    {
        if( file.isDirectory() )
        {
            for( String ignore : lstFolder )
            {
                if( file.getName().equalsIgnoreCase( ignore ) )
                {
                    return false;
                }
            }
        }
        else                                    // Everything is not a dir will be treated as a file
        {
            if( (nMaxSize > 0) && file.length() > nMaxSize )
            {
                return false;
            }

            if( lstFileExt.contains( getFileExtension( file ) ) )
            {
                return false;
            }
        }

        return true;
    }

    //----------------------------------------------------------------------------//

    void setFileExtensionsToIgnore( Set<String> lst )
    {
        lstFileExt.clear();
        lstFileExt.addAll( lst );
    }

    void addFileExtension( String ext )
    {
        if( (ext != null) && (! ext.isEmpty()) )
        {
            lstFileExt.add( ext.trim().toLowerCase() );
        }
    }

    void setFolderNamesToIgnore( Set<String> lst )
    {
        lstFolder.clear();
        lstFolder.addAll( lst );
    }

    void addFolderName( String name )
    {
        if( (name != null) && (! name.isEmpty()) )
        {
            lstFolder.add( name.trim().toLowerCase() );
        }
    }

    void setMaxSize( long size )
    {
        nMaxSize = size;
    }

    //----------------------------------------------------------------------------//

    private String getFileExtension( final File file )
    {
        final String name  = file.getName();
        final int    index = name.lastIndexOf( '.' );

        return (((index == -1) || name.endsWith( "." )) ? ""
                                                        : name.substring( index+1 )).trim().toLowerCase();
    }
}