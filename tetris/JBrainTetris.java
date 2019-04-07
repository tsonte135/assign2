package tetris;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;

public class JBrainTetris extends JTetris{

	private Brain brain;
	private JCheckBox brainMode;
	private Brain.Move mv;
	private int countPiece;
	private JPanel little;
	private JLabel adversaryLabel;
	private JSlider adversary;
	private Piece newPiece;
	
	JBrainTetris(int pixels) {
		super(pixels);
		
		brain = new DefaultBrain();
	}

	@Override
	public JComponent createControlPanel(){
		JPanel panel = (JPanel)super.createControlPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		panel.add(new JLabel("Brain:"));
		brainMode = new JCheckBox("Brain active");
	    panel.add(brainMode);
	    
	    little = new JPanel();
	    little.add(new JLabel("Adversary:"));
	    adversary = new JSlider(0, 100, 0);// min, max, current 
	    adversary.setPreferredSize(new Dimension(100,15)); 
	    adversaryLabel = new JLabel("Ok");
        little.add(adversaryLabel);
        little.add(adversary);
	    panel.add(little);
	    
	    return panel;
	}
	
	@Override
	public Piece pickNextPiece() {
		if (random.nextInt(99) < adversary.getValue()) {
			
			newPiece = super.pickNextPiece();
			adversaryLabel.setText("*Ok*");
	        double worstScore = 0;
	        for(int i = 0; i < pieces.length; i++){
	        	Piece curPiece = pieces[i];
	        	board.undo();
	            Brain.Move move = brain.bestMove(board, curPiece, board.getHeight(), null);
	            if (move != null && move.score > worstScore)   {
	            	newPiece = curPiece;
	            	worstScore = move.score;
	                }
	            }
	        	
	            return newPiece;
	        }

	        adversaryLabel.setText("Ok");
	        return super.pickNextPiece();
	    }	
	
	
	@Override
	public void tick(int verb){
		if(brainMode.isSelected() && verb == DOWN){	
			if(count != countPiece){
				board.undo();
				mv = brain.bestMove(board, currentPiece, board.getHeight(), mv);
				countPiece = count;
			}
			
			if(mv != null){
				if(mv.x > currentX){
					super.tick(RIGHT);
				}
				if(mv.x < currentX){
					super.tick(LEFT);
				}
				if(!mv.piece.equals(currentPiece)){
					super.tick(ROTATE);
				}
			}
		}
			
		
		super.tick(verb);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		JBrainTetris brTetris = new JBrainTetris(16);
		JFrame frame = JTetris.createFrame(brTetris);
		frame.setVisible(true);

	}

}
