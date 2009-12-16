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

package org.graphstream.graph.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.graphstream.graph.CompoundAttribute;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.junit.Test;

public class TestElement
{
	@Test
	public void testElementSimpleAttributes()
	{
		Graph graph = new MultiGraph( "g1" );
		
		Node A = graph.addNode( "A" );
		
		assertEquals( "A", A.getId() );
		assertEquals( 0, A.getAttributeCount() );
		
		// Simple attributes.
		
		A.addAttribute( "foo" );
		
		assertEquals( 1, A.getAttributeCount() );
		assertTrue( A.hasAttribute( "foo" ) );
		assertTrue( A.hasAttribute( "foo", Boolean.class ) );
		assertFalse( A.hasLabel( "foo" ) );
		assertFalse( A.hasNumber( "foo" ) );
		assertFalse( A.hasVector( "foo" ) );
		assertFalse( A.hasArray( "foo" ) );
		assertFalse( A.hasHash( "foo" ) );
		assertNotNull( A.getAttribute( "foo" ) );
		assertEquals( true, A.getAttribute( "foo" ) );
		assertEquals( Boolean.TRUE, A.getAttribute( "foo" ) );
		
		// Change.
		
		A.changeAttribute( "foo", false );

		assertEquals( 1, A.getAttributeCount() );
		assertTrue( A.hasAttribute( "foo" ) );
		assertTrue( A.hasAttribute( "foo", Boolean.class ) );
		assertFalse( A.hasLabel( "foo" ) );
		assertFalse( A.hasNumber( "foo" ) );
		assertFalse( A.hasVector( "foo" ) );
		assertFalse( A.hasArray( "foo" ) );
		assertFalse( A.hasHash( "foo" ) );
		assertNotNull( A.getAttribute( "foo" ) );
		assertEquals( false, A.getAttribute( "foo" ) );
		assertEquals( Boolean.FALSE, A.getAttribute( "foo" ) );
		
		// Removal.
		
		A.removeAttribute( "foo" );
		assertEquals( 0, A.getAttributeCount() );
		assertFalse( A.hasAttribute( "foo" ) );
		assertNull( A.getAttribute( "foo" ) );
	}
	
	@Test
	public void testElementValueAttributes()
	{
		Graph graph = new MultiGraph( "g1" );
		
		Node A = graph.addNode( "A" );
		
		assertEquals( "A", A.getId() );
		assertEquals( 0, A.getAttributeCount() );
		
		// Label attributes.
		
		A.addAttribute( "foo", "bar" );
		
		assertEquals( 1, A.getAttributeCount() );
		assertTrue( A.hasAttribute( "foo" ) );
		assertTrue( A.hasAttribute( "foo", String.class ) );
		assertTrue( A.hasLabel( "foo" ) );
		assertFalse( A.hasNumber( "foo" ) );
		assertFalse( A.hasVector( "foo" ) );
		assertFalse( A.hasArray( "foo" ) );
		assertFalse( A.hasHash( "foo" ) );
		assertNotNull( A.getAttribute( "foo" ) );
		assertEquals( "bar", A.getAttribute( "foo" ) );
		
		// Number attributes.
		
		A.addAttribute( "pi", 3.1415 );
		
		assertEquals( 2, A.getAttributeCount() );
		assertTrue( A.hasAttribute( "pi" ) );
		assertTrue( A.hasAttribute( "pi", Number.class ) );
		assertFalse( A.hasLabel( "pi" ) );
		assertTrue( A.hasNumber( "pi" ) );
		assertFalse( A.hasVector( "pi" ) );
		assertFalse( A.hasArray( "pi" ) );
		assertFalse( A.hasHash( "pi" ) );
		assertNotNull( A.getAttribute( "pi" ) );
		assertEquals( 3.1415, A.getAttribute( "pi" ) );
		assertEquals( new Double( 3.1415 ), A.getAttribute( "pi" ) );

		// Vector of numbers.
		
		ArrayList<Number> numbers = new ArrayList<Number>();
		
		numbers.add( 3 );
		numbers.add( 1.4 );
		numbers.add( 1.5f );
		
		A.addAttribute( "v", numbers );
		
		assertEquals( 3, A.getAttributeCount() );
		assertTrue( A.hasAttribute( "v" ) );
		assertTrue( A.hasAttribute( "v", ArrayList.class ) );
		assertFalse( A.hasLabel( "v" ) );
		assertFalse( A.hasNumber( "v" ) );
		assertTrue( A.hasVector( "v" ) );
		assertFalse( A.hasArray( "v" ) );
		assertFalse( A.hasHash( "v" ) );
		assertNotNull( A.getAttribute( "v" ) );
		assertEquals( numbers, A.getAttribute( "v" ) );
		assertEquals( numbers, A.getVector( "v" ) );
		
		// Hashes 1.
		
		HashMap<String,String> map = new HashMap<String,String>();

		map.put( "A", "a" );
		map.put( "B", "b" );
		map.put( "C", "c" );
		
		A.addAttribute( "map", map );
		
		assertEquals( 4, A.getAttributeCount() );
		assertTrue( A.hasAttribute( "map" ) );
		assertTrue( A.hasAttribute( "map", HashMap.class ) );
		assertFalse( A.hasLabel( "map" ) );
		assertFalse( A.hasNumber( "map" ) );
		assertFalse( A.hasVector( "map" ) );
		assertFalse( A.hasArray( "map" ) );
		assertTrue( A.hasHash( "map" ) );
		assertNotNull( A.getAttribute( "map" ) );
		assertEquals( map, A.getAttribute( "map" ) );
		assertEquals( map, A.getHash( "map" ) );
		
		// Hashes 2.
		
		MyAttribute attr = new MyAttribute();
		
		attr.put( "A", "a" );
		attr.put( "B", "b" );
		attr.put( "C", "c" );

		A.addAttribute( "ca", attr );
		
		assertEquals( 5, A.getAttributeCount() );
		assertTrue( A.hasAttribute( "ca" ) );
		assertTrue( A.hasAttribute( "ca", MyAttribute.class ) );
		assertFalse( A.hasLabel( "ca" ) );
		assertFalse( A.hasNumber( "ca" ) );
		assertFalse( A.hasVector( "ca" ) );
		assertFalse( A.hasArray( "ca" ) );
		assertTrue( A.hasHash( "ca" ) );
		assertNotNull( A.getAttribute( "ca" ) );
		assertEquals( attr, A.getAttribute( "ca" ) );
		assertEquals( attr, A.getHash( "ca" ) );
		
		// Clear
		
		A.clearAttributes();
		
		assertEquals( 0, A.getAttributeCount() );
	}
	
	@Test
	public void testElementMultiAttributes()
	{
		Graph graph = new MultiGraph( "g1" );
		
		Node A = graph.addNode( "A" );
		
		assertEquals( "A", A.getId() );
		assertEquals( 0, A.getAttributeCount() );
		
		// Arrays
		
		A.addAttribute( "array", 0, 1.1, 1.3f, "foo" );
		
		Object expected[] = { 0, 1.1, 1.3f, "foo" };
		
		assertEquals( 1, A.getAttributeCount() );
		assertTrue( A.hasAttribute( "array" ) );
		assertTrue( A.hasAttribute( "array", Object[].class ) );
		assertFalse( A.hasLabel( "array" ) );
		assertFalse( A.hasNumber( "array" ) );
		assertFalse( A.hasVector( "array" ) );
		assertTrue( A.hasArray( "array" ) );
		assertFalse( A.hasHash( "array" ) );
		assertArrayEquals( expected, (Object[])A.getAttribute( "array" ) );
		assertArrayEquals( expected, A.getArray( "array" ) );
		assertNotNull( A.getAttribute( "array" ) );
	}
	
	@Test
	public void testElementUtilityMethods()
	{
		Graph graph = new MultiGraph( "g1" );
		
		Node A = graph.addNode( "A" );
		
		assertEquals( "A", A.getId() );
		assertEquals( 0, A.getAttributeCount() );

		// First attribute of.
		
		A.addAttribute( "C", "c" );
		A.addAttribute( "I", "i" );
		A.addAttribute( "Z", "z" );
		
		Object o = A.getFirstAttributeOf( "A", "B", "C", "I", "Z" );
		
		assertNotNull( o );
		assertEquals( "c", o );
		
		// First attribute of 2.
		
		A.addAttribute( "J", 1 );
		A.addAttribute( "X", 2 );
		
		o = A.getFirstAttributeOf( Number.class, "A", "B", "C", "I", "J", "X", "Z" );
		
		assertNotNull( o );
		assertEquals( 1, o );
	}
	
	@Test
	public void testElementIterables()
	{
		Graph graph = new MultiGraph( "g1" );
		
		Node A = graph.addNode( "A" );
		
		assertEquals( "A", A.getId() );
		assertEquals( 0, A.getAttributeCount() );

		// First attribute of.
		
		A.addAttribute( "A", "a" );
		A.addAttribute( "B", "b" );
		A.addAttribute( "C", "c" );
		
		assertEquals( 3, A.getAttributeCount() );
		
		HashSet<String> keys = new HashSet<String>();
		
		for( String key: A.getAttributeKeySet() )
			keys.add( key );
			
		assertEquals( 3, keys.size() );
		assertTrue( keys.contains( "A" ) );
		assertTrue( keys.contains( "B" ) );
		assertTrue( keys.contains( "C" ) );
	}
	
	@Test
	public void testNullAttributes()
	{
		Graph graph = new MultiGraph( "g1" );
		
		graph.addAttribute( "foo" );
		graph.addAttribute( "bar", (Object)null );	// Yes an attribute with a null value, You can !
		
		assertTrue( graph.hasAttribute( "foo" ) );
		assertTrue( graph.hasAttribute( "bar" ) );
		
		graph.removeAttribute( "foo" );
		graph.removeAttribute( "bar" );
		
		assertFalse( graph.hasAttribute( "foo" ) );
		assertFalse( graph.hasAttribute( "bar" ) );
	}
	
	protected static class MyAttribute extends HashMap<String,String> implements CompoundAttribute
	{
        private static final long serialVersionUID = 1L;

		public String getKey()
        {
	        return "MyAttribute";
        }

		public HashMap<?,?> toHashMap()
        {
			return this;
        }
	}
}