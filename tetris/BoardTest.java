package tetris;

import static org.junit.Assert.*;

import org.junit.*;

public class BoardTest {
	Board b;
	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece s, sRotated;
	private Piece stick1, stick2;
	private Piece L_l1, L_l2, L_l3, L_l4;
	private Piece square;
	// This shows how to build things in setUp() to re-use
	// across tests.
	
	// In this case, setUp() makes shapes,
	// and also a 3X6 board, with pyr placed at the bottom,
	// ready to be used by tests.
	@Before
	public void setUp() throws Exception {
		b = new Board(3, 6);
		
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
		
		b.place(pyr1, 0, 0);
	}
	
	// Check the basic width/height/max after the one placement
	@Test
	public void testSample1() {
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(2, b.getMaxHeight());
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
	}
	
	// Place sRotated into the board, then check some measures
	@Test
	public void testSample2() {
		b.commit();
		int result = b.place(sRotated, 1, 1);
		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());
	}
	
	@Test
	public void testSamplePlace(){
		b.commit();
		int result1 = b.place(stick1, 1, 1);
		assertEquals(Board.PLACE_BAD, result1);
		
		b.undo();
		int result2 = b.place(stick1, 0, 1);
		assertEquals(Board.PLACE_OK, result2);
		
		b.commit();
		int result3 = b.place(sRotated, 1, 1);
		assertEquals(Board.PLACE_ROW_FILLED, result3);
		
		b.commit();
		int result4 = b.place(square, 4, 5);
		assertEquals(Board.PLACE_OUT_BOUNDS, result4);
	}
	
	@Test
	public void testSampleClearRows(){
		b.commit();
		b.place(stick1, 0, 1);
		
		b.commit();
		b.place(sRotated, 1, 1);
		
		int numOfCleared = b.clearRows();
		assertEquals(3, numOfCleared);
		
		b.commit();
		b.place(sRotated, 1, 0);
		assertEquals(2, b.clearRows());
	}
	
	@Test
	public void testSample3(){
		b.undo();
		
		b.commit();
		b.place(stick1, 0, 0);
		
		b.commit();
		b.place(stick1, 1, 0);
		
		b.commit();
		b.place(stick1, 2, 0);
		
		int numOfCleared = b.clearRows();
		assertEquals(4, numOfCleared);
	}
	
	@Test
	public void testSample4(){
		b.undo();
		assertEquals(0, b.dropHeight(stick1, 0));
		assertEquals(0, b.getRowWidth(0));
		assertEquals(0, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
		
		b.commit();
		b.place(stick1, 0, 0);
		assertEquals(4, b.dropHeight(s, 0));
		assertEquals(1, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(4, b.getColumnHeight(0));
		
	}
}

