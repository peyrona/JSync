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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

/**
 * Few utility static methods to make code more readable.
 *
 * @author peyrona
 */
public class Utils
{
    /**
     * Check if passed files have or not the same contents.<br>
     * Returns true when (OR applied):
     * <ul>
     *    <li> Second passed file does not exists.
     *    <li> Both files have different length.
     *    <li> Both files have different 'lastmodified' (rounding to 1 minute).
     * </ul>
     *
     * @param fOrig
     * @param fDest
     * @return true if both files are different.
     */
    public static boolean areEquals( final File fOrig, final File fDest )
    {
        return (fDest.exists())
               &&
               (fOrig.length() == fDest.length())
               &&
               (isEqualLastModified( fOrig.lastModified(), fDest.lastModified() ));
    }

    /**
     * Returns the equivalent file in Destination folder or passed file in
     * Origin folder.
     *
     * @param fOrig
     * @param fDest
     * @param fEntry
     * @return
     */
    public static File getEquivalent( final File fOrig, final File fDest, File fEntry )
    {
        String sOrigPath = fOrig.getAbsolutePath();
        String sFilePath = fEntry.getAbsolutePath();
        String sNewPath  = sFilePath.substring( sOrigPath.length() );

        return new File( fDest, sNewPath );
    }

    public static void copy( File fOri, File fDes )
    {
        try
        {
            Files.copy( fOri.toPath(),
                        fDes.toPath(),
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES );

            Main.info( fOri + " --> copied");
        }
        catch( IOException ex )
        {
            Main.log( Level.WARNING, "Error copying from: '"+ fOri +"' to '"+ fDes +"'", ex );
        }
    }

    /**
     * Deletes a file or a folder with all its contents.
     *
     * @param f File or folder to delete.
     */
    public static void delete( File f )
    {
        if( f.isDirectory() )
        {
            for( File c : f.listFiles() )
            {
                delete( c );
            }
        }
        else if( ! f.delete() )
        {
            Main.log( Level.WARNING, new IOException( "Can not delete: "+ f ) );
        }
    }

    //----------------------------------------------------------------------------//

    private Utils()
    {
        // Avoid creating instances of this class
    }

    //----------------------------------------------------------------------------//

    // Rounded to minute
    private static boolean isEqualLastModified( long n, long m )
    {
        return (n / (1000*60)) == (m / (1000*60));
    }
}