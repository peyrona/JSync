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

package com.peyrona.jsync.config;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Instances of this class hold the information needed to define synchronization
 * tasks.
 *
 * @author peyrona
 */
public final class Task
{
    private String sOriginFolder;
    private String sDestinFolder;
    private long   nMaxFileSize = 0;   // 0 == no limit

    private final Set<String> lstIgnoreFileExts    = new HashSet<>();
    private final Set<String> lstIgnoreFolderNames = new HashSet<>();

    //----------------------------------------------------------------------------//

    public static Set<Task> loadTasks( File fConfig )
           throws ParserConfigurationException, SAXException, IOException
    {
        Set<Task> lstTasks = new HashSet<>();
        NodeList  nlTasks  = XMLHelper.getRoot( fConfig ).getElementsByTagName( "task" );

        for( int n = 0; n < nlTasks.getLength(); n++ )
        {
            Node node = nlTasks.item( n );

            lstTasks.add( configureTask( new Task(), node.getChildNodes() ) );
        }

        return lstTasks;
    }

    //----------------------------------------------------------------------------//

    public String getOriginFolder() { return sOriginFolder; }
    public String getDestinFolder() { return sDestinFolder; }
    public long getMaxFileSize()    { return nMaxFileSize;  }

    public Set<String> getIgnoreFileExts() { return lstIgnoreFileExts;    }
    public Set<String> getFolderNames()    { return lstIgnoreFolderNames; }

    //----------------------------------------------------------------------------//

    private static Task configureTask( Task task, NodeList childs )
    {
        for( int n = 0; n < childs.getLength(); n++ )
        {
            Node node = childs.item( n );

            if( node.getNodeType() == Node.ELEMENT_NODE )
            {
                String sNodeName = node.getNodeName().trim().toLowerCase();

                if( "origin".equals( sNodeName ) )
                {
                    task.sOriginFolder = XMLHelper.getNodeValue( node );
                }
                else if( "destination".equals( sNodeName ) )
                {
                    task.sDestinFolder = XMLHelper.getNodeValue( node );
                }
                else if( "max_file_size".equals( sNodeName ) )
                {
                    task.nMaxFileSize = Long.parseLong( XMLHelper.getNodeValue( node ) );
                }
                else if( "ignore_file_ext".equals( sNodeName ) )
                {
                    task.lstIgnoreFileExts.add( XMLHelper.getNodeValue( node ) );
                }
                else if( "ignore_folder".equals( sNodeName ) )
                {
                    task.lstIgnoreFolderNames.add( XMLHelper.getNodeValue( node ) );
                }
            }
        }

        return task;
    }
}