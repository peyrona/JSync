/*
 * Copyright (C) 2017 Francisco José Morero Peyrona. All Rights Reserved.
 *
 * GNU Classpath is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the free
 * Software Foundation; either version 3, or (at your option) any later version.
 *
 * This app is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this software; see the file COPYING.  If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.peyrona.jsync;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Determines if a file or folder is excluded or not based on configuration file
 * definitions.
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
            if( lstFolder.contains( file.getName().toLowerCase() ) )
            {
                return false;
            }
        }
        else                                    // Everything that is not a dir will be treated as a file
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

        for( String ext : lst )
        {
            addFileExtension( ext );
        }
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

        for( String name : lst )
        {
            addFolderName( name );
        }
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

        return ((index == -1) ? ""
                              : name.substring( index ).toLowerCase());
    }
}