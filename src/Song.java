/**
 * represents an MP3 file
 * @author vivianle
 *
 */
public class Song implements Comparable<Song> {
	
	/** indice of the song in the playlists **/
	protected int[] index;
	
	/** title of song **/
	protected String title;
	
	/** artist of song **/
	protected String artist;
	
	/** album of song **/
	protected String album;
	
	/** genre of song **/
	protected String genre;
	
	/** path to file **/
	protected String path;
	
	/** artwork of song **/
	protected byte[] artWork;
	
	/** the playlist the song is in **/
	protected MusicPlayer player;
	
	/** duration of the song in seconds **/
	protected int time;
	
	/** duration of song in form m:ss **/
	protected String timeDisplay;
	
	/** whether the song is played, paused or stopped **/
	protected int isPlaying;
	
	/**
	 * initialize the song
	 * @param title of song
	 * @param artist of song
	 * @param album of song
	 * @param genre of song
	 * @param time duration of song
	 * @param path to song
	 * @param artWork of song
	 * @param player playlist song is in
	 */
	public Song(int[] index, String title, String artist, String album, String genre, int time, String path, byte[] artWork, MusicPlayer player) {
		
		// initialize all instance variables
		this.index = index;
		this.title = title;
		this.artist = artist;
		this.album = album;
		
		if (genre != null)
			this.genre = genre;
		else
			this.genre = "";
		
		this.path = path;
		this.artWork = artWork;
		this.player = player;
		this.time = time;
		this.isPlaying = 0;
		
		if (time % 60 >= 10)
			this.timeDisplay = Integer.toString(time/60) + ":" + Integer.toString(time%60);
		else
			this.timeDisplay = Integer.toString(time/60) + ":0" + Integer.toString(time%60);
	}
	
	/**
	 * get the index of the song in playlist
	 * @param isPlaylist playlist or favList
	 * @return an index
	 */
	public int getIndex(boolean isPlaylist) {
		if (isPlaylist)
			return index[0];
		return index[1];
	}
	
	/**
	 * set index of the song in playlist
	 * @param isPlaylist playlist or favList
	 * @param newIndex new index
	 */
	public void setIndex(boolean isPlaylist, int newIndex) {
		if (isPlaylist)
			index[0] = newIndex;
		else
			index[1] = newIndex;
	}
	
	/**
	 * get the status of the song
	 * @return an int - 0 if stop, 1 if playing, 2 if pause
	 */
	public int getIsPlaying() {
		return this.isPlaying;
	}
	
	/**
	 * set status of song
	 * @param i 0 if stop, 1 if playing, 2 if pause
	 */
	public void setIsPlaying(int i) {
		this.isPlaying = i;
	}
	
	/**
	 * get the title of song
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * get the artist of song
	 * @return the artist
	 */
	public String getArtist() {
		return this.artist;
	}
	
	/**
	 * get the album of song
	 * @return the album
	 */
	public String getAlbum() {
		return this.album;
	}
	
	/**
	 * get the path to song
	 * @return the path
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * get the artwork of song
	 * @return the artwork
	 */
	public byte[] getArtWork() {
		return this.artWork;
	}
	
	/**
	 * get the genre of song
	 * @return the genre
	 */
	public String getGenre() {
		return this.genre;
	}
	
	/**
	 * get the time display of song
	 * @return the time display
	 */
	public String getTimeDisplay() {
		return this.timeDisplay;
	}
	
	/**
	 * get the duration of song
	 * @return the time
	 */
	public int getTime() {
		return this.time;
	}
	
	/**
	 * get whether song is in fav list
	 * @return isFav
	 */
	public boolean getIsFav() {
		if (index[1] != -1)
			return true;
		return false;
	}
	
//	/**
//	 * set isFav to be the opposite value
//	 */
//	public void setIsFav() {
//		this.isFav = !this.isFav;
//	}
	
	/**
	 * override the compare method to compare song
	 */
	@Override
	public int compareTo(Song o) {
		// compare title -> artist -> album
		if (player.getSortOrder() == 0) {
			if (this.title.equals(o.getTitle())) {
				if (this.artist.equals(o.getArtist())) {
					return this.album.compareTo(o.getAlbum());
				}
				return this.artist.compareTo(o.getArtist());
			}
			return this.title.compareTo(o.getTitle());
		}
		// compare time -> title -> artist -> album
		else if (player.getSortOrder() == 1) {
			if (this.time == o.getTime()) {
				if (this.title.equals(o.getTitle())) {
					if (this.artist.equals(o.getArtist())) {
						return this.album.compareTo(o.getAlbum());
					}
					return this.artist.compareTo(o.getArtist());
				}
				return this.title.compareTo(o.getTitle());
			}
			return ((Integer)(this.time)).compareTo((Integer)(o.getTime()));
		}
		// compare artist -> album -> title
		else if (player.getSortOrder() == 2) {
			if (this.artist.equals(o.getArtist())) {
				if (this.album.equals(o.getAlbum())) {
					return this.title.compareTo(o.getTitle());
				}
				return this.album.compareTo(o.getAlbum());
			}
			return this.artist.compareTo(o.getArtist());
		}
		// compare album -> artist -> title
		else if (player.getSortOrder() == 3) {
			if (this.album.equals(o.getAlbum())) {
				if (this.artist.equals(o.getArtist())) {
					return this.title.compareTo(o.getTitle());
				}
				return this.artist.compareTo(o.getArtist());
			}
			return this.album.compareTo(o.getAlbum());
		}
		// compare genre -> title -> artist -> album
		else {
			if (this.genre.equals(o.getGenre())) {
				if (this.title.equals(o.getTitle())) {
					if (this.artist.equals(o.getArtist())) {
						return this.album.compareTo(o.getAlbum());
					}
					return this.artist.compareTo(o.getArtist());
				}
				return this.title.compareTo(o.getTitle());
			}
			return this.genre.compareTo(o.getGenre());
		}
	}
}
