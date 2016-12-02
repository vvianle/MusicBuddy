import javax.swing.JFrame;

/**
 * MusicPlayerApplication wraps a MusicPlayer JPanel for the Music Player program
 * @author vivianle
 *
 */
public class MusicPlayerApplication {
	
	/**
	 * main function to run Slide show program
	 * @param args arguments
	 */
	public static void main( String[] args )
	{	
		// set up frames and add contents
		JFrame slideFrame = new JFrame( "Music Buddy" );
		slideFrame.getContentPane().add( new MusicPlayerController() );
		slideFrame.setSize(900, 670);
		slideFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		slideFrame.setVisible( true );	
	}
}
