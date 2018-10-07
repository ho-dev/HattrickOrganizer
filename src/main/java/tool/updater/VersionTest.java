package tool.updater;

import static org.junit.Assert.*;

import org.junit.Test;

public class VersionTest {

	/**
	 * Only one number: not allowed
	 */
	@Test(expected = IllegalArgumentException.class)
	public void constructor_Major() {
		Version version = new Version("1");
	}

	/**
	 * Two numbers will work (backward compatibility, e.g. 1.432)
	 */
	@Test
	public void constructor_MajorMinor() {
		Version version = new Version("1.432");

		assertEquals(1, version.getMajor());
		assertEquals(432, version.getMinor());
		assertEquals(0, version.getMinimus());
	}

	/**
	 * New "three numbers style": 1.5.1
	 */
	@Test
	public void constructor_MajorMinorMinimus() {
		Version version = new Version("1.5.1");

		assertEquals(1, version.getMajor());
		assertEquals(5, version.getMinor());
		assertEquals(1, version.getMinimus());
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_wrongFormat_1() {
		Version version = new Version("d.5.1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_wrongFormat_2() {
		Version version = new Version("1.d.1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_wrongFormat_3() {
		Version version = new Version("1.5.d");
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_wrongFormat_4() {
		Version version = new Version(".5.4");
	}

	/**
	 * do not allow negative numbers
	 */
	@Test(expected = IllegalArgumentException.class)
	public void constructor_wrongFormat_5() {
		Version version = new Version("1.-5.4");
	}

	@Test
	public void testEquals() {
		Version version1 = new Version("12.34.76");
		assertEquals(Boolean.FALSE, version1.equals(null));
		assertEquals(Boolean.FALSE, version1.equals(new Version("12.34.75")));
		assertEquals(Boolean.FALSE, version1.equals(new Version("12.34")));
		assertEquals(Boolean.TRUE, version1.equals(version1));
		assertEquals(Boolean.TRUE, version1.equals(new Version("12.34.76")));
	}

	@Test(expected = NullPointerException.class)
	public void testCompareTo_Null() {
		Version version = new Version("1.2.3");
		version.compareTo(null);
	}
	
	@Test
	public void testCompareTo() {
		Version version = new Version("1.2.3");
		
		assertTrue(version.compareTo(new Version("1.2.4")) < 0);
		assertTrue(version.compareTo(new Version("1.3.3")) < 0);
		assertTrue(version.compareTo(new Version("1.3")) < 0);
		assertTrue(version.compareTo(new Version("1.22")) < 0);
		assertTrue(version.compareTo(new Version("2.0")) < 0);
		assertTrue(version.compareTo(new Version("2.2")) < 0);
		assertTrue(version.compareTo(new Version("2.2.4")) < 0);
		
		assertTrue(version.compareTo(new Version("1.2")) > 0);
		assertTrue(version.compareTo(new Version("1.2.2")) > 0);
		assertTrue(version.compareTo(new Version("1.2.0")) > 0);
		assertTrue(version.compareTo(new Version("1.1")) > 0);
		assertTrue(version.compareTo(new Version("1.1.99")) > 0);
		
		assertTrue(version.compareTo(new Version("1.2.3")) == 0);
		version = new Version("99.7.0");
		assertTrue(version.compareTo(new Version("99.7")) == 0);
		version = new Version("99.7");
		assertTrue(version.compareTo(new Version("99.7")) == 0);
		version = new Version("1.0");
		assertTrue(version.compareTo(new Version("1.0")) == 0);
		assertTrue(version.compareTo(new Version("1.0.0")) == 0);
	}
	
	@Test(expected = NullPointerException.class)
	public void testIsBefore_Null() {
		new Version("1.2.3").isBefore(null);
	}
	
	@Test
	public void testIsBefore() {
		Version version = new Version("1.2.3");
		assertEquals(Boolean.FALSE, version.isBefore(new Version("1.2")));
		assertEquals(Boolean.FALSE, version.isBefore(new Version("1.2.0")));
		assertEquals(Boolean.FALSE, version.isBefore(new Version("1.2.2")));
		assertEquals(Boolean.FALSE, version.isBefore(new Version("1.2.3")));
		
		assertEquals(Boolean.TRUE, version.isBefore(new Version("1.3.3")));
		assertEquals(Boolean.TRUE, version.isBefore(new Version("1.3")));
		assertEquals(Boolean.TRUE, version.isBefore(new Version("1.2.22")));
		assertEquals(Boolean.TRUE, version.isBefore(new Version("2.1.3")));
		assertEquals(Boolean.TRUE, version.isBefore(new Version("2.1")));
		
		version = new Version("1.0");
		assertEquals(Boolean.FALSE, version.isBefore(new Version("1.0.0")));
		assertEquals(Boolean.TRUE, version.isBefore(new Version("1.0.1")));
	}
	
	@Test
	public void testToString() {
		assertEquals("1.2.3", new Version("1.2.3").toString());
		assertEquals("1.2.0", new Version("1.2").toString());
	}

}
