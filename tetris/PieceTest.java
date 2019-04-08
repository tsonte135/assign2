package tetris;

import static org.junit.Assert.*;
import java.util.*;

import org.junit.*;

/*
  Unit test for Piece class -- starter shell.
 */
public class PieceTest {
	// You can create data to be used in the your
	// test cases like this. For each run of a test method,
	// a new PieceTest object is created and setUp() is called
	// automatically by JUnit.
	// For example, the code below sets up some
	// pyramid and s pieces in instance variables
	// that can be used in tests.
	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece s, sRotated;
	private Piece stick1, stick2;
	private Piece L_l1, L_l2, L_l3, L_l4;
	private Piece square;

	@Before
	public void setUp() throws Exception {
		
		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();
		
		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();
		
		L_l1 = new Piece(Piece.L1_STR);
		L_l2 = L_l1.computeNextRotation();
		L_l3 = L_l2.computeNextRotation();
		L_l4 = L_l3.computeNextRotation();
		
		stick1 = new Piece(Piece.STICK_STR);
		stick2 = stick1.computeNextRotation();
		
		square = new Piece(Piece.SQUARE_STR);
	}
	
	// Here are some sample tests to get you started
	
	@Test
	public void testSampleSize() {
		// Check size of pyr piece
		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());

		// Now try after rotation
		// Effectively we're testing size and rotation code here
		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());
		
		// Now try with some other piece, made a different way
		assertEquals(1, stick1.getWidth());
		assertEquals(4, stick2.getWidth());
		
		assertEquals(2, L_l1.getWidth());
		assertEquals(3, L_l2.getWidth());
		assertEquals(2, L_l3.getWidth());
		assertEquals(3, L_l4.getWidth());
		
		assertEquals(2, s.getHeight());
		assertEquals(3, sRotated.getHeight());
		
		assertEquals(2, square.getHeight());
		assertEquals(2, square.computeNextRotation().getHeight());
	}
	
	
	// Test the skirt returned by a few pieces
	@Test
	public void testSampleSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyr1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyr3.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0, 0, 1}, s.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, sRotated.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0}, stick1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0}, L_l1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, L_l2.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0}, square.getSkirt()));
	}
	
	
	@Test 
	public void testSampleEqualls(){
		
		assertTrue(L_l1.equals((new Piece("0 0  1 0  0 1  0 2"))));
		assertTrue(L_l2.equals((new Piece("0 0  1 0  2 0  2 1"))));
		
		assertTrue(stick1.equals((new Piece("0 0  0 1  0 2  0 3"))));
		assertTrue(stick2.equals((new Piece("0 0  1 0  2 0  3 0"))));
		
		assertTrue(square.equals((new Piece("0 0  0 1  1 0  1 1"))));
		
		assertTrue(s.equals((new Piece("0 0 1 0  1 1  2 1"))));
		assertTrue(sRotated.equals((new Piece("0 1  1 0  1 1  0 2"))));	
	}
	
	
	
	@Test
	public void testSampleNextRotation(){
		
		assertTrue(L_l1.equals(L_l4.computeNextRotation()));
		assertTrue(L_l2.equals(L_l1.computeNextRotation()));
		
		assertTrue(square.equals(square.computeNextRotation()));
		assertTrue(stick1.equals(stick2.computeNextRotation()));
		
		assertTrue(s.equals(sRotated.computeNextRotation()));
		
		assertTrue(pyr1.equals(pyr4.computeNextRotation()));
		assertTrue(pyr4.equals(pyr3.computeNextRotation()));
	}
	
	@Test
	public void testSamplFastRotation(){
		
		Piece[] testPieces = Piece.getPieces();
		L_l1 = testPieces[Piece.L1];
		assertTrue(L_l1.computeNextRotation().equals(L_l1.fastRotation()));
		L_l4 = L_l1.fastRotation().fastRotation().fastRotation();
		assertTrue(L_l1.equals(L_l4.fastRotation()));
		
		square = testPieces[Piece.SQUARE];
		assertTrue(square.computeNextRotation().equals(square.fastRotation()));
		
		stick1 = testPieces[Piece.STICK];
		stick2 = stick1.fastRotation();
		assertTrue(stick1.equals(stick2.fastRotation()));
		
		s = testPieces[Piece.STICK];
		sRotated = s.fastRotation();
		assertTrue(s.equals(sRotated.fastRotation()));
		
		pyr1 = testPieces[Piece.PYRAMID];
		pyr3 = pyr1.fastRotation().fastRotation();
		pyr4 = pyr3.fastRotation();
		assertTrue(pyr1.equals(pyr4.fastRotation()));
		assertTrue(pyr4.equals(pyr3.fastRotation()));
		
		L_l3 = L_l1.fastRotation().fastRotation();
		L_l4 = L_l3.fastRotation();
		assertEquals(false, L_l3.equals(L_l1.fastRotation()));
		assertEquals(false, L_l3.equals(L_l4.fastRotation()));
		assertEquals(false, stick1.equals(stick1.fastRotation()));
		
	}
	
}
