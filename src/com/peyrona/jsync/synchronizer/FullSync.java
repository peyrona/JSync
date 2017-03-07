package com.peyrona.jsync.synchronizer;

import com.peyrona.jsync.Main;
import com.peyrona.jsync.Utils;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

/**
 *
 * @author peyrona
 */
final class FullSync
{
    private final File       fOrigi;
    private final File       fDesti;
    private final FileFilter filter;

    //----------------------------------------------------------------------------//

    public FullSync( File fOrigi, File fDesti, FileFilter filter )
    {
        this.fOrigi = fOrigi;
        this.fDesti = fDesti;
        this.filter = filter;
    }

    //----------------------------------------------------------------------------//

    void sync()
    {
        try
        {
            Main.info( "Deleting Destination obsolete files -----------------------------------------" );
            traverse2Delete( fDesti.toPath() );

            Main.info( "Updating files from Origin to Destination -----------------------------------" );
            traverse2Copy( fOrigi.toPath() );
        }
        catch( IOException ex )
        {
            Main.log( Level.SEVERE, null, ex );
        }
    }

    //----------------------------------------------------------------------------//

    /**
     * Delete those files in Destination that does not exist any more in Origin.
     * In other words: those that were deleted since last time this tool was
     * executed.
     *
     * @param path Where to start
     */
    private void traverse2Delete( Path path ) throws IOException
    {
        try( DirectoryStream<Path> stream = Files.newDirectoryStream( path ) )
        {
            for( Path pEntry : stream )
            {
                File fEntry      = pEntry.toFile();
                File fEquivalent = Utils.getEquivalent( fDesti, fOrigi, fEntry );

                if( ! fEquivalent.exists() )
                {
                    Utils.delete( fEntry );
                    Main.info( fEntry +" does not exists any more in Origin: file deleted in Destination." );
                }
            }
        }
    }

    /**
     * Copy new created and modified files from origin to destination.
     *
     * @param path Where to start
     * @throws IOException
     */
    private void traverse2Copy( Path path ) throws IOException
    {
        try( DirectoryStream<Path> stream = Files.newDirectoryStream( path ) )
        {
            for( Path pEntry : stream )
            {
                File fEntry = pEntry.toFile();

                if( filter.accept( fEntry ) )
                {
                    File fEquivalent = Utils.getEquivalent( fOrigi, fDesti, fEntry );

                    if( fEntry.isDirectory() )
                    {
                        if( handleDestinFolder( fEquivalent ) )
                        {
                            traverse2Copy( pEntry );
                        }
                    }
                    else
                    {
                        if( Utils.areEquals( fEntry, fEquivalent ) )
                        {
                            Main.info( fEntry +" and "+ fEquivalent +" are equals: nothing to do." );
                        }
                        else
                        {
                            Utils.copy( fEntry, fEquivalent );
                            Main.info( fEntry +" and "+ fEquivalent +" are not equals: updated in Destination." );
                        }
                    }
                }
                else
                {
                    Main.info( fEntry + " --> not accepted.");
                }
            }
        }
    }

    private boolean handleDestinFolder( File fDest )
    {
        if( fDest.exists() )
        {
            Main.info( fDest +" already exists in destiantion: nothing to do." );
            return true;
        }

        if( fDest.mkdirs() )
        {
            Main.info( fDest +" did not exists in destiantion: successfully created." );
            return true;
        }

        Main.log( Level.SEVERE, new IOException( "Can not create folder '"+ fDest +"'\n"+
                                                 "Folder in origin and its files can not be synchronized in destination" ) );
        return false;
    }
}