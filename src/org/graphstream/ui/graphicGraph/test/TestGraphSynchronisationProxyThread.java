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

package org.graphstream.ui.graphicGraph.test;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.junit.Test;

/**
 * Test the bases of the viewer.
 */
public class TestGraphSynchronisationProxyThread {
	@Test
	public void testGraphSynchronisation() {
		// Here a Graph is created in this thread and another thread is created
		// with a GraphicGraph.
		// The two graphs being in separate threads we use thread proxies
		// filters to pass
		// informations between the two. Once again we will use synchronisation
		// (the two graphs
		// listen at each other). In the direction Graph -> GraphicGraph the
		// graphic graph listens
		// at ALL the events (elements + attributes). In the direction
		// GraphicGraph -> Graph, the
		// graph only listen at attributes since we do not intend to add
		// elements directly in the
		// graphic graph.

		Graph main = new MultiGraph("main");
		ThreadProxyPipe toGraphic = new ThreadProxyPipe(main);
		InTheSwingThread viewerThread = new InTheSwingThread(toGraphic);
		ThreadProxyPipe toMain = viewerThread.getProxy();

		toMain.addAttributeSink(main); // Get the graphic graph proxy.

		// Now launch the graphic graph in the Swing thread using a Swing Timer.

		viewerThread.start();

		// We modify the graph in the main thread.

		Node A = main.addNode("A");
		Node B = main.addNode("B");
		Node C = main.addNode("C");
		main.addEdge("AB", "A", "B");
		main.addEdge("BC", "B", "C");
		main.addEdge("CA", "C", "A");

		SpriteManager sman = new SpriteManager(main);
		Sprite S1 = sman.addSprite("S1");
		Sprite S2 = sman.addSprite("S2");
		Sprite S3 = sman.addSprite("S3");

		S3.setPosition(1, 2, 2);
		S3.setPosition(2, 3, 2);
		S3.setPosition(3, 2, 1);

		A.addAttribute("ui.foo", "bar");
		B.addAttribute("ui.bar", "foo");
		C.addAttribute("truc"); // Not prefixed by UI, will not pass.
		S1.addAttribute("ui.foo", "bar");
		main.stepBegins(1);

		toMain.pump();

		// We ask the Swing thread to modify the graphic graph.

		main.stepBegins(2);
		main.addAttribute("ui.EQUIP"); // Remember GraphicGraph filters
										// attributes.

		// Wait and stop.

		toMain.pump();
		sleep(1000);
		toMain.pump();

		main.addAttribute("ui.STOP");

		toMain.pump();
		sleep(1000);
		toMain.pump();

		// ****************************************************************************************
		// Now we can begin the real test. We ensure the timer in the Swing
		// graph stopped and check
		// If the two graphs (main and graphic) synchronised correctly.

		GraphicGraph graphic = viewerThread.graphic;

		assertTrue(viewerThread.isStopped());
		assertFalse(main.hasAttribute("ui.EQUIP"));
		assertFalse(graphic.hasAttribute("ui.EQUIP"));
		assertTrue(main.hasAttribute("ui.STOP"));
		assertTrue(graphic.hasAttribute("ui.STOP"));

		assertEquals(3, graphic.getStep());
		assertEquals(2, main.getStep()); // We do not listen at elements events
											// the step 3
											// of the graphic graph did not
											// reached us.
		// Assert all events passed toward the graphic graph.

		assertEquals(3, graphic.getNodeCount());
		assertEquals(3, graphic.getEdgeCount());
		assertEquals(3, graphic.getSpriteCount());
		assertNotNull(graphic.getNode("A"));
		assertNotNull(graphic.getNode("B"));
		assertNotNull(graphic.getNode("C"));
		assertNotNull(graphic.getEdge("AB"));
		assertNotNull(graphic.getEdge("BC"));
		assertNotNull(graphic.getEdge("CA"));
		assertNotNull(graphic.getSprite("S1"));
		assertNotNull(graphic.getSprite("S2"));
		assertEquals("bar", graphic.getNode("A").getAttribute("ui.foo"));
		assertEquals("foo", graphic.getNode("B").getAttribute("ui.bar"));
		// assertNull( graphic.getNode("C").getAttribute( "truc" ) ); // Should
		// not pass the attribute filter.
		assertEquals("bar", graphic.getSprite("S1").getAttribute("ui.foo"));
		assertEquals("bar", sman.getSprite("S1").getAttribute("ui.foo"));

		// Assert attributes passed back to the graph from the graphic graph.

		Object xyz1[] = { new Float(4), new Float(3), new Float(2) };
		Object xyz2[] = { new Float(2), new Float(1), new Float(0) };
		Object xyz3[] = { new Float(3), new Float(2), new Float(1) };

		assertArrayEquals(xyz1, (Object[]) main.getNode("A")
				.getAttribute("xyz"));
		assertArrayEquals(xyz2, (Object[]) main.getNode("B")
				.getAttribute("xyz"));
		assertArrayEquals(xyz3, (Object[]) main.getNode("C")
				.getAttribute("xyz"));

		assertEquals("foobar", S2.getAttribute("ui.foobar"));

		GraphicSprite gs3 = graphic.getSprite("S3");

		assertEquals(0.5f, S1.getX());
		assertEquals(0, S1.getY());
		assertEquals(0, S1.getZ());
		assertEquals(1, S2.getX());
		assertEquals(2, S2.getY());
		assertEquals(3, S2.getZ());

		assertEquals(3, gs3.getX());
		assertEquals(2, gs3.getY());
		assertEquals(1, gs3.getZ());
	}

	protected void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * The graphic graph in the Swing thread.
	 */
	public static class InTheSwingThread implements ActionListener {
		protected ThreadProxyPipe fromMain;

		protected GraphicGraph graphic;

		protected Timer timer;

		public InTheSwingThread(ThreadProxyPipe input) {
			fromMain = input;
			graphic = new GraphicGraph("gg");
			timer = new Timer(40, this);

			timer.setRepeats(true);
			timer.setCoalesce(true);
			input.addSink(graphic);
		}

		public void start() {
			timer.start();
		}

		public boolean isStopped() {
			return (!timer.isRunning());
		}

		public void actionPerformed(ActionEvent e) {
			fromMain.pump();

			// We wait for some attributes to be added. Such events trigger
			// actions that modify
			// the graphic graph and should be propagated (synchronised) to the
			// main graph.
			// When we encounter the "ui.STOP" event we stop the timer.

			if (graphic.hasAttribute("ui.EQUIP")) {
				Node A = graphic.getNode("A");
				Node B = graphic.getNode("B");
				Node C = graphic.getNode("C");

				if (A != null)
					A.addAttribute("xyz", 4, 3, 2);
				if (B != null)
					B.addAttribute("xyz", 2, 1, 0);
				if (C != null)
					C.addAttribute("xyz", 3, 2, 1);

				GraphicSprite S1 = graphic.getSprite("S1");
				GraphicSprite S2 = graphic.getSprite("S2");

				if (S2 != null) {
					S2.addAttribute("ui.foobar", "foobar");
					S2.setPosition(1, 2, 3, Style.Units.GU);
				}

				if (S1 != null)
					S1.setPosition(0.5f);

				graphic.removeAttribute("ui.EQUIP");
				graphic.stepBegins(3);
			} else if (graphic.hasAttribute("ui.STOP")) {
				timer.stop();
				// System.err.printf( "STOP!%n" );
			}
		}

		public ThreadProxyPipe getProxy() {
			ThreadProxyPipe toMain = new ThreadProxyPipe(graphic);

			// fromMain.synchronizeWith( toMain, graphic );

			return toMain;
		}
	}
}