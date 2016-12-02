import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import jaco.mp3.player.MP3Player;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.tika.Tika;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

/**
 * the view/controller of the program
 * @author vivianle
 *
 */
public class MusicPlayerController extends JPanel {
	
	/** the logic of the music player **/
	protected MusicPlayer player;
	
	/** artwork of the song currently being played **/
	protected JLabel artWorkLabel;
	
	/** title of the song currently being played **/
	protected JLabel playingSongTitle;
	
	/** artist and album of the song currently being played **/
	protected JLabel playingSongArtist;
	
	/** the play/pause button **/
	protected JButton playBtn;
	
	/** the panel holds all songs **/
	protected JPanel playlistPanel;
	
	/** the panel holds fav songs **/
	protected JPanel favoritePanel;
	
	/** the scroll pane of panel holds all songs **/
	protected JScrollPane favoriteScroll;
	
	/** the scroll pane of panel holds fav songs **/
	protected JScrollPane playlistScroll;
	
	/** array of song panels **/
	protected JPanel[] songPanel;
	
	/** array of fav song panels **/
	protected JPanel[]favSongPanel;
	
	/** the MP3Player to play music **/
	protected MP3Player musicPlayer;
	
	/** the timer to keep track of duration of song played **/
	protected Timer timer;
	
	/** start time of the song **/
	protected long startTime;
	
	/** pause time of the song **/
	protected long pauseTime;
	
	/** duration of song already played **/
	protected long durationPlayed;
	
	public MusicPlayerController() {
		super(new BorderLayout());
		player = new MusicPlayer();
		createTopPanel();
		createPlaylistPanel();
		songPanel = new JPanel[0];
		favSongPanel = new JPanel[0];
		musicPlayer = new MP3Player();
		timer = new Timer();
		durationPlayed = 0;
	}
	
	/**
	 * get the array of song panels 
	 * @param a whether it's playlist or favList
	 * @return an array of song panels
	 */
	public JPanel[] getSongPanelsArray(boolean a) {
		if (a)
			return this.songPanel;
		return this.favSongPanel;
	}
	
	/**
	 * get the panel that holds the playlist
	 * @param a whether it's playlist or favList
	 * @return a JPanel that holds the playlist
	 */
	public JPanel getPlaylistPanel(boolean a) {
		if (a)
			return this.playlistPanel;
		return this.favoritePanel;
	}
	
	/**
	 * get the scrollpane that holds the panel
	 * @param a whether it's playlist or favList
	 * @return a JscrollPane
	 */
	public JScrollPane getScrollPanel(boolean a) {
		if (a)
			return this.playlistScroll;
		return this.favoriteScroll;
	}
	
	/**
	 * create the panel on top of the program
	 */
	public void createTopPanel() {
		
		// initialize a new JPanel
		JPanel topPanel = new JPanel(new BorderLayout()) {
			
			// override the paintComponent method to set background image
			@Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        Image image = new ImageIcon(loadImg("img/background.jpg")).getImage();
		        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
		   }
		};
		
		// make a search field
		JTextField searchField = new JTextField();
		searchField.setPreferredSize(new Dimension(150,21));
		searchField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				// if type in search field, show result for both playlist and favList
				showSearchResult(true, searchField.getText().toLowerCase());
				showSearchResult(false, searchField.getText().toLowerCase());
			}
		});
		
		// make a search Icon
		JLabel searchIcon = new JLabel();
		ImageIcon imageIcon = new ImageIcon(loadImg("img/search.png"));
		Image newImg = (imageIcon.getImage()).getScaledInstance(17, 17, Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(newImg);
		searchIcon.setIcon(imageIcon);
		
		// create a searchPanel and add the search field and the search icon
		JPanel searchPanel = new JPanel(new FlowLayout());
		searchPanel.add(searchIcon);
		searchPanel.add(searchField);
		searchPanel.setOpaque(false);
		
		// create the import button
		JButton importBtn = createImportButton();
		
		// add search panel, search btn, and currentSongPanel to topPanel
		topPanel.add(importBtn, BorderLayout.WEST);
		topPanel.add(searchPanel, BorderLayout.EAST);
		topPanel.add(createCurrentSongPanel(), BorderLayout.SOUTH);
		
		this.add(topPanel, BorderLayout.NORTH);
	}
	
	/**
	 * load images from resource folder
	 * @param path path of image
	 * @return an URL
	 */
	public URL loadImg(String path) {
		URL input = MusicPlayerController.class.getResource(path);
		if (input == null)
			input = MusicPlayerController.class.getResource("/" + path);
		return input;
	}
	
	/**
	 * show the search result of a playlist
	 * @param isPlaylist true if playlist, false if favList
	 * @param text the search text
	 */
	public void showSearchResult(boolean isPlaylist, String text) {
		int number = 0;
		
		// iterate through each song in the playlist
		for (int i = 1; i  <= player.getCurrentList(isPlaylist).getHeapSize(); i++) {
			Song current = player.getSong(i, isPlaylist);
			
			// if its title/artist/album/genre/time display contains the text
			if (current.getTitle().toLowerCase().startsWith(text) ||
			current.getArtist().toLowerCase().startsWith(text) ||
			current.getAlbum().toLowerCase().startsWith(text) ||
			current.getGenre().toLowerCase().startsWith(text) ||
			current.getTimeDisplay().startsWith(text)) {
				
				// set it to be visible
				this.getSongPanelsArray(isPlaylist)[i-1].setVisible(true);
				
				// set the background color depending on its index in the list
				if (number % 2 == 0)
					this.getSongPanelsArray(isPlaylist)[i-1].setBackground(Color.WHITE);
				else
					this.getSongPanelsArray(isPlaylist)[i-1].setBackground(new Color(238,238,238));
				number += 1;
				
				// update the index number at beginning of panel
				Component[] components = this.getSongPanelsArray(isPlaylist)[i-1].getComponents();
				((JLabel)components[0]).setText(Integer.toString(number));
			}
			else
				//otherwise, set it to be invisible
				this.getSongPanelsArray(isPlaylist)[i-1].setVisible(false);
		}
	}
	
	/**
	 * create the button to import songs
	 * @return a JButton
	 */
	public JButton createImportButton() {
		
		// initialize a Jbutton and add action Listener
		JButton importBtn = new JButton("Import");
		importBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// open a multiple file JFileChooser, set the file filter to Mp3
				JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(new FileNameExtensionFilter("MP3 Files", "mp3"));
				chooser.setMultiSelectionEnabled(true);
				
				// get the result files
				int result = chooser.showOpenDialog(MusicPlayerController.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					File[] files = chooser.getSelectedFiles();
					
					// use Tika to filter the files
					Tika tika = new Tika();
					// iterate through each file
					for (int i = 0; i < files.length; i++) {
						try {
							// get the type of file, if it's audio of stereo
							FileInputStream stream = new FileInputStream(files[i].getPath());
							String mediaType = tika.detect(new BufferedInputStream(stream));
							if (mediaType.startsWith("audio") || mediaType.startsWith("stereo")) {
								Mp3File mp3file = new Mp3File(files[i].getPath());
								
								// if it does not already exist in playlist
								if (!player.songExist(files[i].getPath())) {
									Song newSong;
									int[] index = new int[]{player.getCurrentList(true).getHeapSize()+1,-1};
									
									// if it has meta-data
									if (mp3file.hasId3v2Tag()) {
										// get all tags and create new song
										ID3v2 tag = mp3file.getId3v2Tag();
										newSong = new Song(index, tag.getTitle(), tag.getArtist(), 
												tag.getAlbum(), tag.getGenreDescription(), (int)mp3file.getLengthInSeconds(), files[i].getPath(), 
												tag.getAlbumImage(), player);
									}
									else
										// otherwise, create new song with empty meta-data
										newSong = new Song(index, files[i].getName(), "", "", "", (int)mp3file.getLengthInSeconds(), files[i].getPath(), new byte[0], player);
									
									// add song to playlist, create panel to display it
									player.addToList(newSong, true);
									addSongPanel(newSong, true);
								}
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		return importBtn;
	}
	
	/**
	 * panel to show the current song being played
	 * @return a JPanel
	 */
	public JPanel createCurrentSongPanel() {
		
		// title of song being played
		playingSongTitle = new JLabel("", JLabel.LEFT);
		playingSongTitle.setForeground(Color.WHITE);
		playingSongTitle.setFont(new Font("ZapfDingbats", Font.BOLD, 22));
		playingSongTitle.setPreferredSize(new Dimension(320, 36));
		
		// artist/album of song being played
		playingSongArtist = new JLabel("", JLabel.LEFT);
		playingSongArtist.setForeground(Color.WHITE);
		playingSongArtist.setPreferredSize(new Dimension(350, 15));
		
		// add title and artist/album label to a JPanel
		JPanel titlePanel = new JPanel();
		titlePanel.setOpaque(false);
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		titlePanel.add(playingSongTitle);
		titlePanel.add(playingSongArtist);
		titlePanel.add(createButtonPanel());
		
		// add artwork label
		artWorkLabel = new JLabel();
		artWorkLabel.setPreferredSize(new Dimension(120,120));
		
		// add all to currentSongPanel and return
		JPanel currentSongPanel = new JPanel(new GridBagLayout());
		currentSongPanel.setBorder(new EmptyBorder(80,200,12,0));
		currentSongPanel.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.insets = new Insets(0,0,12,24);
		
		currentSongPanel.add(artWorkLabel, c);
		currentSongPanel.add(titlePanel);
		
		// create a slider to control volume
		JSlider volumeSlide = new JSlider(JSlider.HORIZONTAL, 0, 100, 20);
		volumeSlide.setMajorTickSpacing(50);
		volumeSlide.setPaintTicks(true);
		volumeSlide.setBorder(new EmptyBorder(137,0,0,0));
		
		// add action when volume slider is changed
		volumeSlide.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Mixer.Info[] mixers = AudioSystem.getMixerInfo();
				   for(int i=0;i<mixers.length;i++){
				    if (mixers[i].getName().equals("Port Built-in Output")) {
				        Mixer mixer = AudioSystem.getMixer(mixers[i]);
				        Line.Info[] lineinfos = mixer.getTargetLineInfo();
				        for(Line.Info lineinfo : lineinfos){
				            try {
				                Line line = mixer.getLine(lineinfo);
				                line.open();
				                if(line.isControlSupported(FloatControl.Type.VOLUME)){
				                	// update the volume
				                    FloatControl control = (FloatControl) line.getControl(FloatControl.Type.VOLUME);
				                    control.setValue((float)((volumeSlide.getValue()))/100);
				                }
				            } catch (LineUnavailableException a) {
				                a.printStackTrace();
				            }
				        }
				    }
			    }
			}
		});
		
		// create panel to add the volume slider
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(currentSongPanel, BorderLayout.CENTER);
		panel.add(volumeSlide, BorderLayout.EAST);
		panel.setOpaque(false);
		return panel;
	}
	
	/**
	 * update the currentSongLabel each time a different song is played
	 * @param song the song being played
	 */
	public void updateCurrentSongLabel(Song song) {
		Image awImg;
		try {
			// get the artwork image of song
			ImageIcon img = new ImageIcon(ImageIO.read(new ByteArrayInputStream(song.getArtWork())));
			awImg = (img.getImage()).getScaledInstance(120, 120, Image.SCALE_SMOOTH);
		} catch (Exception e) {
			// if song does not have artwork image, get the default image
			awImg = (new ImageIcon(loadImg("img/test.jpg")).getImage()).getScaledInstance(120, 120, Image.SCALE_SMOOTH);
		}	
		// update artwork, title and artist/album
		ImageIcon awIcon = new ImageIcon(awImg);
		artWorkLabel.setIcon(awIcon);
		playingSongTitle.setText(song.getTitle());
    	playingSongArtist.setText(song.getArtist() + " - " + song.getAlbum());
	}
	
	/**
	 * play a song
	 * @param index of song being played
	 * @param isPlaylist whether it's playlist or favList
	 */
	public void playSong(int index, boolean isPlaylist) {
		
		// if there's a song played previous, update that song's status
		if (player.getCurrentSongIndex() != 0)
			this.updateAllSongPanelStatus(0, player.getCurrentSongIndex(), player.getIsPlaylist());
		
		// set new playlist and song index being played
		this.player.setIsPlaylist(isPlaylist);
		this.player.updateCurrentSongIndex(index);
		
		// update status of current song being played
		this.updateAllSongPanelStatus(1, player.getCurrentSongIndex(), isPlaylist);
		
		// update the current song label
		updateCurrentSongLabel(player.getSong(index, isPlaylist));
    	
		// set playBtn to show pause img
    	Image imgPause = (new ImageIcon(loadImg("img/pause_white.png")).getImage()).getScaledInstance(38, 38, Image.SCALE_SMOOTH);
		playBtn.setIcon(new ImageIcon(imgPause));
		
		// stop old music, play new song
		musicPlayer.stop();
		musicPlayer = new MP3Player(new File(player.getSong(index, isPlaylist).getPath()));
		musicPlayer.play();
		
		startTime = System.nanoTime();
		
		// set player to be playing
		if (!player.getIsPlaying())
			player.setIsPlaying(true);
		
		// initialize new timer
		timer = new Timer();
		// schedule to play next song/reset when song is over
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				scheduleNextSong();
			}
			
		}, player.getSong(index, isPlaylist).getTime()*1000);
	}
	
	/**
	 * update each song panel
	 * @param panel the panel being updated
	 * @param songIndex index of song of panel
	 * @param hasStar whether it's in playlist or favList
	 */
	public void updateSongPanel(JPanel panel, Song song, boolean hasStar) {
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		// set index of panel
		JLabel index = new JLabel(Integer.toString(song.getIndex(hasStar)), JLabel.RIGHT);
		index.setMaximumSize(new Dimension(30, 30));
		index.setPreferredSize(new Dimension(30, 30));
		
		// set status of panel
		JLabel status = new JLabel("");
		
		// if it's not playing/paused -> no icon
		if (player.getSong(song.getIndex(hasStar), hasStar).getIsPlaying() == 0)
			status.setIcon(null);
		// if it's playing -> speaker icon
		else if (player.getSong(song.getIndex(hasStar), hasStar).getIsPlaying() == 1) {
			Image speakerImg = (new ImageIcon(loadImg("img/speaker.png")).getImage()).getScaledInstance(17, 17, Image.SCALE_SMOOTH);
			status.setIcon(new ImageIcon(speakerImg));
		}
		else {
			// otherwise, mute speaker icon
			Image speakerImg = (new ImageIcon(loadImg("img/mute.png")).getImage()).getScaledInstance(17, 17, Image.SCALE_SMOOTH);
			status.setIcon(new ImageIcon(speakerImg));
		}
		status.setBorder(new EmptyBorder(0,10,0,12));
		status.setMaximumSize(new Dimension(40, 30));
		status.setPreferredSize(new Dimension(40, 30));
		
		// set title of song
		JLabel title = new JLabel(player.getSong(song.getIndex(hasStar), hasStar).getTitle(), JLabel.LEFT);
		title.setBorder(new EmptyBorder(0,0,0,22));
		title.setMaximumSize(new Dimension(212, 30));
		title.setPreferredSize(new Dimension(212, 30));
		
		// set duration of song
		JLabel duration = new JLabel(player.getSong(song.getIndex(hasStar), hasStar).getTimeDisplay(), JLabel.CENTER);
		duration.setMaximumSize(new Dimension(70, 30));
		duration.setPreferredSize(new Dimension(70, 30));
		duration.setBorder(new EmptyBorder(0,0,0,22));
		
		// set artist of song
		JLabel artist = new JLabel(player.getSong(song.getIndex(hasStar), hasStar).getArtist(), JLabel.LEFT);
		artist.setMaximumSize(new Dimension(145, 30));
		artist.setPreferredSize(new Dimension(145, 30));
		artist.setBorder(new EmptyBorder(0,0,0,22));
		
		// set album of song
		JLabel album = new JLabel(player.getSong(song.getIndex(hasStar), hasStar).getAlbum(), JLabel.LEFT);
		album.setMaximumSize(new Dimension(185, 30));
		album.setPreferredSize(new Dimension(185, 30));
		album.setBorder(new EmptyBorder(0,0,0,22));
		
		// set genre of song
		JLabel genre = new JLabel(player.getSong(song.getIndex(hasStar), hasStar).getGenre(), JLabel.LEFT);
		genre.setMaximumSize(new Dimension(100, 30));
		genre.setPreferredSize(new Dimension(100, 30));
		genre.setBorder(new EmptyBorder(0,0,0,22));
		
		// add all to panel
		panel.add(index);
		panel.add(status);
		panel.add(title);
		panel.add(duration);
		panel.add(artist);
		panel.add(album);
		panel.add(genre);
		
		// the star button
		JButton star = new JButton();
		Image imgstar = (new ImageIcon(loadImg("img/star.png")).getImage()).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		Image imgFav = (new ImageIcon(loadImg("img/fav.png")).getImage()).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		
		// if it's in playlist, if it's not fav -> icon star, otherwise, icon fav
		if (hasStar) {
			if (!player.getSong(song.getIndex(hasStar), hasStar).getIsFav())
				star.setIcon(new ImageIcon(imgstar));
			else
				star.setIcon(new ImageIcon(imgFav));
			star.setBorderPainted(false);
			star.setFocusPainted(false);
			star.setContentAreaFilled(false);
			star.setBorder(new EmptyBorder(4,0,4,20));
			
			// action when star btn if clicked
			star.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// if song is not fav, set fav icon
					if (!player.getSong(song.getIndex(hasStar), hasStar).getIsFav()) {
						star.setIcon(new ImageIcon(imgFav));
						
						// add song to favList and set song to be fav
						player.addToList(player.getSong(song.getIndex(hasStar), hasStar), false);
						addSongPanel(player.getSong(song.getIndex(hasStar), hasStar), false);
					}
					else {
						// otherwise, set star icon, delete song from favList
						star.setIcon(new ImageIcon(imgstar));
						int index = song.getIndex(!hasStar);
						deletePanelFromList(!hasStar, song.getIndex(!hasStar));
						player.deleteSongFromList(!hasStar, song);
						player.updateSongIndex(!hasStar, index);
						if (player.getShuffleOrder().contains(song) && player.getIsPlaylist() == false)
							player.removeFromShuffleList(song);
					}
				}
			});
			panel.add(star);
		}
		else {
			// otherwise, create an empty label
			JLabel starLabel = new JLabel("");
			starLabel.setPreferredSize(new Dimension(40,20));
			starLabel.setBorder(new EmptyBorder(4,0,4,40));
			panel.add(starLabel);
		}
		
		// initialize a delete button
		JButton delete = new JButton();
		Image imgDelete = (new ImageIcon(loadImg("img/x.png")).getImage()).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		delete.setIcon(new ImageIcon(imgDelete));
		delete.setBorderPainted(false);
		delete.setFocusPainted(false);
		delete.setContentAreaFilled(false);
		
		// add action when button is clicked
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// if (song is played/paused and in the playlist being played) or (song in playlist and favList and song is played in favList)
				if (((player.getSong(song.getIndex(hasStar), hasStar).getIsPlaying() != 0) && hasStar == player.getIsPlaylist()) ||
						(hasStar == true && player.getSong(song.getIndex(hasStar), hasStar).getIsFav() && player.getSong(song.getIndex(!hasStar), !hasStar).getIsPlaying() != 0))
					// reset player
					resetPlayer();
				
				// if song in favList, reset icon of song in playlist
				if (hasStar == false) {
					Component[] components = getSongPanelsArray(!hasStar)[song.getIndex(!hasStar)-1].getComponents();
					((JButton)components[7]).setIcon(new ImageIcon(imgstar));
				}
				
				if (player.getShuffleOrder().contains(song))
						player.removeFromShuffleList(song);
				
				// if song in playlist and also in favList, delete song from favList
				if (hasStar == true && player.getSong(song.getIndex(hasStar), hasStar).getIsFav()) {
					int index = song.getIndex(!hasStar);
					deletePanelFromList(!hasStar, song.getIndex(!hasStar));
					player.deleteSongFromList(!hasStar, song);
					player.updateSongIndex(!hasStar, index);
				}
				// delete song from the current list
				int index = song.getIndex(hasStar);
				deletePanelFromList(hasStar, song.getIndex(hasStar));
				player.deleteSongFromList(hasStar, song);
				player.updateSongIndex(hasStar, index);
			}
		});
		
		panel.add(delete);
		panel.setBorder(new EmptyBorder(4,0,4,18));
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		// set panel background
		if (song.getIndex(hasStar) % 2 == 1) {
			panel.setBackground(Color.WHITE);
			panel.setOpaque(true);
		}
		
		// add action when panel is clicked
		panel.addMouseListener(new MouseAdapter() {
            @Override
			public void mouseClicked(MouseEvent evt) {
            	// if double click, cancel the old timer, play that song
                if (evt.getClickCount() == 2) {
                	player.resetShuffleOrder();
                	timer.cancel();
                	playSong(song.getIndex(hasStar), hasStar);
                	if (player.getShuffle()) {
                		player.getShuffleOrder().add(0, song);
                		player.setShuffleSongIndex(player.getShuffleSongIndex() + 1);
                	}
                }
            }
        });
	}
	
	/**
	 * reset the player to the custom status
	 */
	public void resetPlayer() {
		player.setIsPlaying(false);
		this.updateAllSongPanelStatus(0, player.getCurrentSongIndex(), player.getIsPlaylist());
		musicPlayer.stop();
		timer.cancel();
		timer.purge();
		player.updateCurrentSongIndex(0);
		Image imgStop = (new ImageIcon(loadImg("img/play_white.png")).getImage()).getScaledInstance(38, 38, Image.SCALE_SMOOTH);
		playBtn.setIcon(new ImageIcon(imgStop));
		playingSongTitle.setText("");
		playingSongArtist.setText("");
		artWorkLabel.setIcon(null);
		player.resetShuffleOrder();
	}
	
	/**
	 * delete the panel from the array of panel
	 * @param a whether it's playlist or favList
	 * @param songIndex index of the song being deleted
	 */
	public void deletePanelFromList(boolean a, int songIndex) {
		
		// set song panel to be invisible
		this.getSongPanelsArray(a)[songIndex-1].setVisible(false);
		
		// iterate through the array of panel from the song panel
		for (int i = songIndex-1; i < player.getCurrentList(a).getHeapSize()-1; i++) {
			
			// move all panel forward 1 index
			this.getSongPanelsArray(a)[i] = this.getSongPanelsArray(a)[i+1];
			
			// set the background color
			if (i % 2 == 0)
				this.getSongPanelsArray(a)[i].setBackground(Color.WHITE);
			else
				this.getSongPanelsArray(a)[i].setBackground(new Color(238,238,238));
			
			// update the index number at the beginning of the panel
			Component[] components = this.getSongPanelsArray(a)[i].getComponents();
			((JLabel)components[0]).setText(Integer.toString(i+1));
		}
		// update the background color of the panel
		if (player.getCurrentList(a).getHeapSize() %2 == 1)
			this.getPlaylistPanel(a).setBackground(Color.WHITE);
		else
			this.getPlaylistPanel(a).setBackground(new Color(238,238,238));
	}
	
	/**
	 * add song panel to the array
	 * @param song song being added
	 * @param a whether it's playlist or favList
	 */
	public void addSongPanel(Song song, boolean a) {
		// if length of array is less than heapsize
		if (this.getSongPanelsArray(a).length < this.player.getCurrentList(a).getHeapSize()) {
			
			// create a new longer array, update all old panels
			JPanel[] newSongPanel = new JPanel[this.player.getCurrentList(a).getHeapSize()];
			for (int i = 0; i < this.getSongPanelsArray(a).length; i++)
				newSongPanel[i] = this.getSongPanelsArray(a)[i];		
			
			// set it to be new songPanel/favSongPanel
			if (a)
				this.songPanel = newSongPanel;
			else
				this.favSongPanel = newSongPanel;
		}
		
		// update songPanel for the new song
		JPanel panel = new JPanel();
		this.updateSongPanel(panel, song, a);
		
		// add panel to the playlistPanel, update the view
		this.getPlaylistPanel(a).add(panel);
		this.getScrollPanel(a).setViewportView(this.getPlaylistPanel(a));
		this.getPlaylistPanel(a).setBackground(new Color(238,238,238));
		
		// put new panel at the heapsize index in the array
		this.getSongPanelsArray(a)[this.player.getCurrentList(a).getHeapSize()-1] = panel;
	}
	
	/**
	 * update song panel status
	 * @param status status of the song
	 * @param index index of the song
	 * @param isPlaylist whether it's playlist or favList
	 */
	public void updateAllSongPanelStatus(int status, int index, boolean isPlaylist) {
		
		// update status of song in playlist
		this.updateSongStatus(status, index, isPlaylist);
		
		// if it exist in other playlist, update song status in other playlist
		if (player.getSong(index, isPlaylist).getIndex(!isPlaylist) != -1)
			this.updateSongStatus(status, player.getSong(index, isPlaylist).getIndex(!isPlaylist), !isPlaylist);
	}
	
	/**
	 * update the song status
	 * @param status status of song
	 * @param index index of song
	 * @param isPlaylist whether it's in playlist / favList
	 */
	public void updateSongStatus(int status, int index, boolean isPlaylist) {
		
		// set isPlaying status of song
		this.player.getSong(index, isPlaylist).setIsPlaying(status);
		
		// get the status component
		Component[] components = this.getSongPanelsArray(isPlaylist)[index-1].getComponents();
		
		// update the status icon depend on the status
		if (status == 0)
			((JLabel)components[1]).setIcon(null);
		if (status == 1) {
			Image speakerImg = (new ImageIcon(loadImg("img/speaker.png")).getImage()).getScaledInstance(17, 17, Image.SCALE_SMOOTH);
			((JLabel)components[1]).setIcon(new ImageIcon(speakerImg));
		}
		else if (status == 2) {
			Image speakerImg = (new ImageIcon(loadImg("img/mute.png")).getImage()).getScaledInstance(17, 17, Image.SCALE_SMOOTH);
			((JLabel)components[1]).setIcon(new ImageIcon(speakerImg));
		}
	}
	
	/**
	 * the button panel - back, play, next
	 * @return a Jpanel
	 */
	public JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.setOpaque(false);
		
		// create the backBtn
		JButton backBtn = new JButton();
		Image imgBack = (new ImageIcon(loadImg("img/backward.png")).getImage()).getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		backBtn.setIcon(new ImageIcon(imgBack));
		backBtn.setBorderPainted(false);
		backBtn.setFocusPainted(false);
		backBtn.setContentAreaFilled(false);
		
		// add action when clicked
		backBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// if there's song in playlist
				if (player.getCurrentList(true).getHeapSize() >=1) {
					if (!player.getIsPlaying()) {
						
						// if playlist is at custom, play first song in playlist
						if (player.getCurrentSongIndex() == 0)
							playSong(player.getNextSongIndex(true), true);
						else {
							// if playlist is paused, update song status and current song index, show next song
							updateAllSongPanelStatus(0, player.getCurrentSongIndex(), player.getIsPlaylist());
							player.updateCurrentSongIndex(player.getPreviousSongIndex(player.getIsPlaylist()));
							updateAllSongPanelStatus(2, player.getCurrentSongIndex(), player.getIsPlaylist());
							showNextSong(player.getSong(player.getCurrentSongIndex(), player.getIsPlaylist()));
						}
					}
					else {
						// otherwise, play the previous song
						timer.cancel();
						playSong(player.getPreviousSongIndex(player.getIsPlaylist()), player.getIsPlaylist());
					}
				}
			}	
		});
		
		// create playBtn
		playBtn = new JButton();
		Image imgStop = (new ImageIcon(loadImg("img/play_white.png")).getImage()).getScaledInstance(38, 38, Image.SCALE_SMOOTH);
		playBtn.setIcon(new ImageIcon(imgStop));
		playBtn.setBorderPainted(false);
		playBtn.setFocusPainted(false);
		playBtn.setContentAreaFilled(false);
		
		// add action when clicked
		playBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// if there's song in playlist
				if (player.getCurrentList(true).getHeapSize() >=1) {
					if (!player.getIsPlaying()) {
						
						// if playlist is at custom, play first song in playlist
						if (player.getCurrentSongIndex() == 0)
							playSong(player.getNextSongIndex(true), true);
						else {
							// if player is paused, resume music, update startTime
							startTime = System.nanoTime();
							
							// update song status
							updateAllSongPanelStatus(1, player.getCurrentSongIndex(), player.getIsPlaylist());
							musicPlayer.play();
							player.setIsPlaying(true);
							
							// initialize new timer, add action when song is over
							timer = new Timer();
							timer.schedule(new TimerTask() {
								@Override
								public void run() {
									scheduleNextSong();
								}								
							}, ((player.getSong(player.getCurrentSongIndex(), player.getIsPlaylist())).getTime() - durationPlayed)*1000);
						}
						// set playBtn to pause icon
						Image imgPause = (new ImageIcon(loadImg("img/pause_white.png")).getImage()).getScaledInstance(38, 38, Image.SCALE_SMOOTH);
						playBtn.setIcon(new ImageIcon(imgPause));
					}
					else {
						// otherwise if player is playing, pause the music, update the song status
						updateAllSongPanelStatus(2, player.getCurrentSongIndex(), player.getIsPlaylist());
						musicPlayer.pause();
						
						// update pause time, increment the durationPlayed of song
						pauseTime = System.nanoTime();
						durationPlayed += (pauseTime-startTime)/1000000000;
						Image imgPlay = (new ImageIcon(loadImg("img/play.png")).getImage()).getScaledInstance(38, 38, Image.SCALE_SMOOTH);
						playBtn.setIcon(new ImageIcon(imgPlay));
						player.setIsPlaying(false);
						timer.cancel();
					}
					repaint();
				}
			}
		});
		
		// create the nextBtn
		JButton nextBtn = new JButton();
		Image imgNext = (new ImageIcon(loadImg("img/forward.png")).getImage()).getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		nextBtn.setIcon(new ImageIcon(imgNext));
		nextBtn.setBorderPainted(false);
		nextBtn.setFocusPainted(false);
		nextBtn.setContentAreaFilled(false);
		
		// add action when nextBtn is clicked
		nextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// if there's song in playlist
				if (player.getCurrentList(true).getHeapSize() >=1) {
					if (!player.getIsPlaying()) {
						// if playlist is at custom, play first song in playlist
						if (player.getCurrentSongIndex() == 0)
							playSong(player.getNextSongIndex(true), true);
						else {
							// if playlist is paused, update song status and current song index, show next song
							updateAllSongPanelStatus(0, player.getCurrentSongIndex(), player.getIsPlaylist());
							player.updateCurrentSongIndex(player.getNextSongIndex(player.getIsPlaylist()));
							updateAllSongPanelStatus(2, player.getCurrentSongIndex(), player.getIsPlaylist());
							showNextSong(player.getSong(player.getCurrentSongIndex(), player.getIsPlaylist()));							
						}
					}
					else {
						// otherwise, play the next song
						timer.cancel();
						playSong(player.getNextSongIndex(player.getIsPlaylist()), player.getIsPlaylist());
					}
				}
			}		
		});
		
		// create the repeat button
		JButton repeatBtn = new JButton();
		Image imgRepeat = (new ImageIcon(loadImg("img/repeat_enable.png")).getImage()).getScaledInstance(18, 18, Image.SCALE_SMOOTH);
		repeatBtn.setIcon(new ImageIcon(imgRepeat));
		repeatBtn.setBorderPainted(false);
		repeatBtn.setFocusPainted(false);
		repeatBtn.setContentAreaFilled(false);
		repeatBtn.setBorder(new EmptyBorder(7,12,0,0));
		repeatBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		// add action when button is clicked
		repeatBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// depend on repeat, set icon of repeatBtn, reverse value of repeat
				if (player.getRepeat()) {
					Image imgNotRepeat = (new ImageIcon(loadImg("img/repeat_disable.png")).getImage()).getScaledInstance(18, 18, Image.SCALE_SMOOTH);
					repeatBtn.setIcon(new ImageIcon(imgNotRepeat));
				}
				else
					repeatBtn.setIcon(new ImageIcon(imgRepeat));
				player.setRepeat();
			}		
		});
		
		// create the shuffle button
		JButton shuffleBtn = new JButton();
		Image imgNotShuffle = (new ImageIcon(loadImg("img/shuffle_disable.png")).getImage()).getScaledInstance(18, 18, Image.SCALE_SMOOTH);
		shuffleBtn.setIcon(new ImageIcon(imgNotShuffle));
		shuffleBtn.setBorderPainted(false);
		shuffleBtn.setFocusPainted(false);
		shuffleBtn.setContentAreaFilled(false);
		shuffleBtn.setBorder(new EmptyBorder(7,12,0,0));
		shuffleBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		// add action when button is clicked
		shuffleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// depend on shuffle, set icon of shuffleBtn, reverse value of shuffle
				if (player.getShuffle()) {
					shuffleBtn.setIcon(new ImageIcon(imgNotShuffle));
					player.resetShuffleOrder();
				}
				else {
					Image imgShuffle = (new ImageIcon(loadImg("img/shuffle_enable.png")).getImage()).getScaledInstance(18, 18, Image.SCALE_SMOOTH);
					shuffleBtn.setIcon(new ImageIcon(imgShuffle));
				}
				player.setShuffle();
			}		
		});
		
		// add all buttons to panel and return
		buttonPanel.add(backBtn);
		buttonPanel.add(playBtn);
		buttonPanel.add(nextBtn);
		buttonPanel.add(repeatBtn);
		buttonPanel.add(shuffleBtn);
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		return buttonPanel;
	}
	
	/**
	 * schedule the next song to be played when current song is over
	 */
	public void scheduleNextSong() {
		// if player is not shuffle and song is at the end of list and player cannot repeat, reset player
		// or player is shuffle and all songs have been played and player cannot repeat
		if (!player.getRepeat()) {
			if ((!player.getShuffle() && (player.getCurrentSongIndex() == player.getCurrentList(player.getIsPlaylist()).getHeapSize())) ||
					(player.getShuffle() && (player.getShuffleOrder().size() % player.getCurrentList(player.getIsPlaylist()).getHeapSize()) == 0))
				resetPlayer();
		}
		else
			// otherwise, play next song
			playSong(player.getNextSongIndex(player.getIsPlaylist()), player.getIsPlaylist());
	}
	
	/**
	 * show the next song being played when program is paused
	 * @param song the next song being played
	 */
	public void showNextSong(Song song) {
		updateCurrentSongLabel(song);
		musicPlayer = new MP3Player(new File(song.getPath()));
		startTime = System.nanoTime();
		pauseTime = System.nanoTime();	
	}
	
	/**
	 * create the playlist panel to hold all songs panel
	 */
	public void createPlaylistPanel() {
		
		// setup the playlist panel and the playlistscroll
		playlistPanel = new JPanel();
		playlistPanel.setLayout(new BoxLayout(playlistPanel, BoxLayout.Y_AXIS));
		playlistPanel.setBorder(new EmptyBorder(2,0,10,10));
		playlistScroll = new JScrollPane();
		playlistScroll.setBorder(null);
		
		// setup the favList panel and the favScroll
		favoritePanel = new JPanel();
		favoritePanel.setLayout(new BoxLayout(favoritePanel, BoxLayout.Y_AXIS));
		favoritePanel.setBorder(new EmptyBorder(2,0,10,10));
		favoriteScroll = new JScrollPane();
		favoriteScroll.setBorder(null);
		
		// add playlist/playlistscroll to a panel
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		panel1.add(createHeadingPanel(true));
		panel1.add(playlistScroll);
		
		// add favList/favScroll to a panel
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		panel2.add(createHeadingPanel(false));
		panel2.add(favoriteScroll);
		
		// create a tabbedpane and add 2 panels
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Playlist", panel1);
		tabbedPane.addTab("Favorites", panel2);
		this.add(tabbedPane, BorderLayout.CENTER);
	}
	
	/**
	 * reset all headings of the panel
	 * @param title title label
	 * @param time time label
	 * @param artist artist label
	 * @param album album label
	 * @param genre genre label
	 * @param order the label of the sortOrder
	 */
	public void resetHeadings(JLabel title, JLabel time, JLabel artist, JLabel album, JLabel genre, JLabel order) {	
		// set fonts
		title.setFont(new Font("ZapfDingbats", Font.PLAIN, 14));
		time.setFont(new Font("ZapfDingbats", Font.PLAIN, 14));
		artist.setFont(new Font("ZapfDingbats", Font.PLAIN, 14));
		album.setFont(new Font("ZapfDingbats", Font.PLAIN, 14));
		genre.setFont(new Font("ZapfDingbats", Font.PLAIN, 14));
		order.setFont(new Font("ZapfDingbats", Font.CENTER_BASELINE, 14));
	}
	
	/**
	 * create the headings for a playlist panel
	 * @param isPlaylist whether it's playlist / favList
	 * @return a JPanel
	 */
	public JPanel createHeadingPanel(boolean isPlaylist) {
		
		JPanel heading = new JPanel();
		heading.setLayout(new BoxLayout(heading, BoxLayout.X_AXIS));
		
		// title label
		JLabel title = new JLabel("Title", JLabel.CENTER);
		title.setMaximumSize(new Dimension(260, 25));
		title.setMinimumSize(new Dimension(260, 25));
		title.setPreferredSize(new Dimension(260, 25));
		title.setFont(new Font("ZapfDingbats", Font.PLAIN, 14));
		
		// time label
		JLabel time = new JLabel("Time", JLabel.CENTER);
		time.setMaximumSize(new Dimension(75, 25));
		time.setMinimumSize(new Dimension(75, 25));
		time.setPreferredSize(new Dimension(75, 25));
		time.setFont(new Font("ZapfDingbats", Font.PLAIN, 14));
		
		// artist label
		JLabel artist = new JLabel("Artist", JLabel.CENTER);
		artist.setMaximumSize(new Dimension(130, 25));
		artist.setMinimumSize(new Dimension(130, 25));
		artist.setPreferredSize(new Dimension(130, 25));
		artist.setFont(new Font("ZapfDingbats", Font.PLAIN, 14));
		
		// album label
		JLabel album = new JLabel("Album", JLabel.CENTER);
		album.setMaximumSize(new Dimension(170, 25));
		album.setMinimumSize(new Dimension(170, 25));
		album.setPreferredSize(new Dimension(170, 25));
		album.setFont(new Font("ZapfDingbats", Font.PLAIN, 14));
		
		// genre label
		JLabel genre = new JLabel("Genre", JLabel.CENTER);
		genre.setMaximumSize(new Dimension(140, 25));
		genre.setMinimumSize(new Dimension(140, 25));
		genre.setPreferredSize(new Dimension(140, 25));
		genre.setFont(new Font("ZapfDingbats", Font.PLAIN, 14));
		
		// if click on title, sort playlist according to title
		title.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
				sortAndUpdatePanels(0, isPlaylist);
				resetHeadings(title, time, artist, album, genre, title);
            }
		});
		
		// if click on time, sort playlist according to time
		time.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
				sortAndUpdatePanels(1, isPlaylist);
				resetHeadings(title, time, artist, album, genre, time);
            }
		});
		
		// if click on artist, sort playlist according to artist
		artist.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
				sortAndUpdatePanels(2, isPlaylist);
				resetHeadings(title, time, artist, album, genre, artist);
            }
		});
		
		// if click on album, sort playlist according to album
		album.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
				sortAndUpdatePanels(3, isPlaylist);
				resetHeadings(title, time, artist, album, genre, album);
            }
		});
		
		// if click on genre, sort playlist according to genre
		genre.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
				sortAndUpdatePanels(4, isPlaylist);
				resetHeadings(title, time, artist, album, genre, genre);
            }
		});

		// add all to heading panel
		heading.add(title);
		heading.add(time);
		heading.add(artist);
		heading.add(album);
		heading.add(genre);
		
		heading.setBorder(new EmptyBorder(12, 0, 12, 0));
		heading.setAlignmentX(Component.LEFT_ALIGNMENT);
		return heading;
	}
	
	/**
	 * sort and update all song panels
	 * @param sortOrder the order to sort
	 * @param isPlaylist whether it's playlist / favList
	 */
	public void sortAndUpdatePanels(int sortOrder, boolean isPlaylist) {
		// sort the playlist and reset the panel
		player.sortPlaylist(sortOrder, isPlaylist);
		resetPanel(isPlaylist);
		getScrollPanel(isPlaylist).setViewportView(getPlaylistPanel(isPlaylist));
	}
	
	/**
	 * reset the panel
	 * @param a whether it's playlist / favList
	 */
	public void resetPanel(boolean a) {
		// remove all component in playlist panel
		this.getPlaylistPanel(a).removeAll();
		this.getPlaylistPanel(a).setLayout(new BoxLayout(this.getPlaylistPanel(a), BoxLayout.Y_AXIS));
		this.getPlaylistPanel(a).setBorder(new EmptyBorder(2,0,10,10));
		
		// iterate through the playlist / favList
		for (int i = 0; i < this.player.getCurrentList(a).getHeapSize(); i++) {
			// update the each song panel, add it to the array of song panels and playlist panel
			JPanel newSongPanel = new JPanel();
			player.getSong(i+1, a).setIndex(a, i+1);
			this.updateSongPanel(newSongPanel, player.getSong(i+1, a), a);
			this.getSongPanelsArray(a)[i] = newSongPanel;
			this.getPlaylistPanel(a).add(newSongPanel);
			
			// if a song is played / paused and the list is the list being played, update currentSongIndex
			if (a == this.player.getIsPlaylist()) {
				if (this.player.getSong(i+1, a).getIsPlaying() != 0)
					this.player.updateCurrentSongIndex(i+1);
			}
		}
		// update background color
		if (player.getCurrentList(a).getHeapSize() == 0)
			this.getPlaylistPanel(a).setBackground(Color.WHITE);
		else
			this.getPlaylistPanel(a).setBackground(new Color(238,238,238));
	}
}