/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.miv.graphstream.ui.viewer.test;

import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.MultiGraph;
import org.miv.graphstream.io2.ProxyFilter;
import org.miv.graphstream.io2.thread.ThreadProxyFilter;
import org.miv.graphstream.ui2.swingViewer.Viewer;

/**
 * Test the viewer.
 */
public class TestViewer
{
	public static void main( String args[] )
	{
		new TestViewer();
	}
	
	public TestViewer()
	{
		Graph             graph     = new MultiGraph( "main graph" );
		ThreadProxyFilter toSwing   = new ThreadProxyFilter( graph );
		Viewer            viewer    = new Viewer( toSwing );
		ProxyFilter       fromSwing = viewer.getThreadProxyOnGraphicGraph();
		
		fromSwing.addGraphAttributesListener( graph );
		((ThreadProxyFilter)fromSwing).addAttributesSynchro( graph, toSwing );
		viewer.addDefaultView( true );

		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );

		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		A.addAttribute( "xyz", 0, 1, 0 );
		B.addAttribute( "xyz", 1, 0, 0 );
		C.addAttribute( "xyz",-1, 0, 0 );
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
		
		boolean loop  = true;
		float   color = 0;
		float   dir   = 0.01f;
		
		while( loop )
		{
			try { Thread.sleep( 100 ); } catch( InterruptedException e ) { e.printStackTrace(); }
			
			fromSwing.checkEvents();
			
			if( graph.hasAttribute( "ui.viewClosed" ) )
			{
				loop = false;
			}
			else
			{
				color += dir;
				
				if( color > 1 )
				{
					color = 1;
					dir = -dir;
				}
				else if( color < 0 )
				{
					color = 0;
					dir = -dir;
				}
				
				A.setAttribute( "ui.color", color );
			}
		}
		
		System.out.printf( "Bye bye ...%n" );
		System.exit( 0 );
	}
	
	protected static String styleSheet =
		"graph         { padding : 20px; stroke-width: 0px; }" +
		"node:selected { fill-color:red; fill-mode: plain; }" +
		"node:clicked  { fill-color:blue; fill-mode: plain; }" +
		"node#A        { fill-color: green, yellow, purple; fill-mode: dyn-plain; }";
}