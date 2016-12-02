import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * the logic of the Music Player
 * contains the playlists and the status of the program
 * @author vivianle
 *
 */
public class MusicPlayer {
	
	/** the playlist to store all songs **/
	protected BinaryHeap<Song> playlist;
	
	/** playlist to store favorite songs **/
	protected BinaryHeap<Song> favoritelist;
	
	/** the order to sort the playlist **/
	protected int sortOrder;
	
	/** whether a song is being played **/
	protected boolean isPlaying;
	
	/** the index of song being played **/
	protected int currentSongIndex;
	
	/** the playlist being played **/
	protected boolean isPlaylist;
	
	/** whether program allows repeat **/
	protected boolean repeat;
	
	/** whether program is in shuffle mode **/
	protected boolean shuffle;
	
	/** a list that keep track of the shuffle order of the songs **/
	protected ArrayList<Song> shuffleOrder;
	
	/** index of the song in the ArrayList being played in shuffle mode **/
	protected int shuffleSongIndex;
	
	/**
	 * Constructor to initialize the program
	 */
	public MusicPlayer() {
		// initialize all instance variables
		playlist = new BinaryHeapA(new Song[0]);
		favoritelist = new BinaryHeapA(new Song[0]);
		isPlaylist = true;
		sortOrder = 0;
		isPlaying = false;
		currentSongIndex = 0;
		repeat = true;
		shuffle = false;
		shuffleOrder = new ArrayList<Song>();
		shuffleSongIndex = -1;
	}
	
	/**
	 * get the current playlist being played
	 * @param a true if playlist, false if favList
	 * @return a playlist
	 */
	public BinaryHeap<Song> getCurrentList(boolean a) {
		if (a)
			return this.playlist;
		return this.favoritelist;
	}
	
	/**
	 * update indice of songs when a song is deleted
	 * @param hasStar playlist or favList
	 * @param index index of song being deleted
	 */
	public void updateSongIndex(boolean hasStar, int index) {
		
		// iterate through the songs after the song being deleted and decrement indice
		for (int i = index; i <= this.getCurrentList(hasStar).getHeapSize(); i++) {
			this.getSong(i, hasStar).setIndex(hasStar, this.getSong(i, hasStar).getIndex(hasStar)-1);
			
			// update currentSongIndex if need
			if ((this.getSong(i, hasStar).getIsPlaying() != 0) && (hasStar == this.getIsPlaylist()))
				this.currentSongIndex -= 1;
		}
	}
	
	/**
	 * delete a song from playlist
	 * @param a whether song is in playlist or favList
	 * @param songIndex index of song being deleted
	 */
	public void deleteSongFromList(boolean a, Song song) {
		// iterate through the list from songIndex, move all songs forward 1 index
		for (int i = song.getIndex(a)-1; i < this.getCurrentList(a).getHeapSize()-1; i++)
			this.getCurrentList(a).getInternalArray()[i] = this.getCurrentList(a).getInternalArray()[i+1];
		// decrease heapsize
		this.getCurrentList(a).setHeapSize(this.getCurrentList(a).getHeapSize() - 1);
		song.setIndex(a, -1);
	}
	
	/**
	 * get the next song index
	 * @param isPlaylist playlist being played
	 * @return an int - songIndex
	 */
	public int getNextSongIndex(boolean isPlaylist) {
		if (!shuffle) {
			// if currentSongIndex is the last in the list, return 1
			if (currentSongIndex == getCurrentList(isPlaylist).getHeapSize())
				return 1;
			else
				// otherwise, return increment of currentSongIndex
				return currentSongIndex + 1;
		}
		else {
			this.shuffleSongIndex += 1;
			if (this.shuffleSongIndex < this.shuffleOrder.size())
				return this.shuffleOrder.get(this.shuffleSongIndex).getIndex(isPlaylist);

			//generate random number
			while (true) {
				int random = 1 + new Random().nextInt((this.getCurrentList(isPlaylist).getHeapSize() - 1) + 1);
				
				// case 1: not everyone in playlist has been played
				if (this.shuffleOrder.size() < this.getCurrentList(isPlaylist).getHeapSize()) {
					if (!shuffleOrder.contains(this.getSong(random, isPlaylist))) {
						shuffleOrder.add(this.getSong(random, isPlaylist));
						return random;
					}
				}
				else {
					List<Song> a;
					// case 2: playlist is being repeated more than 1
					if (Math.ceil(this.shuffleOrder.size() / this.getCurrentList(isPlaylist).getHeapSize()) > 1) {
						int rounds = (int)(Math.floor(this.shuffleOrder.size() / this.getCurrentList(isPlaylist).getHeapSize()));
						a = (List<Song>) shuffleOrder.subList(this.getCurrentList(isPlaylist).getHeapSize()*rounds, this.shuffleOrder.size());
					}
					else
						// case 3: playlist is being repeated the first time
						a = (List<Song>) shuffleOrder.subList(this.getCurrentList(isPlaylist).getHeapSize(), this.shuffleOrder.size());		
					if (!a.contains(this.getSong(random, isPlaylist))) {
						shuffleOrder.add(this.getSong(random, isPlaylist));
						return random;
					}
				}
			}
		}
	}
	
	/**
	 * reset the shuffle order
	 */
	public void resetShuffleOrder() {
		shuffleOrder = new ArrayList<Song>();
		this.shuffleSongIndex = -1;
	}
	
	/**
	 * get the shuffle order
	 * @return the shuffle order
	 */
	public ArrayList<Song> getShuffleOrder() {
		return this.shuffleOrder;
	}
	
	/**
	 * increment the shuffle song index
	 * @param index new shuffle song index
	 */
	public void setShuffleSongIndex(int index) {
		this.shuffleSongIndex = index;
	}
	
	/**
	 * get the shuffle song index
	 * @return shuffle song index
	 */
	public int getShuffleSongIndex() {
		return this.shuffleSongIndex;
	}
	
	/**
	 * get the previous song index
	 * @param isPlaylist playlist being played
	 * @return an int - songIndex
	 */
	public int getPreviousSongIndex(boolean isPlaylist) {
		if (!shuffle) {
			// if currentSongIndex is the first in list, return heapsize of list
			if (currentSongIndex == 1)
				return getCurrentList(isPlaylist).getHeapSize();
			else
				// otherwise, return decrement of currentSongIndex
				return currentSongIndex - 1;
		}
		else {
			if (this.shuffleSongIndex > 0) {
				this.shuffleSongIndex -= 1;
				return this.shuffleOrder.get(this.shuffleSongIndex).getIndex(isPlaylist);
			}
			//generate random number
			while (true) {
				int random = 1 + new Random().nextInt((this.getCurrentList(isPlaylist).getHeapSize() - 1) + 1);
				// case 1: not everyone in playlist has been played
				if (this.shuffleOrder.size() < this.getCurrentList(isPlaylist).getHeapSize()) {
					if (!shuffleOrder.contains(this.getSong(random, isPlaylist))) {
						shuffleOrder.add(0, this.getSong(random, isPlaylist));
						return random;
					}
				}
				else {
					List<Song> a;
					// case 2: playlist is being repeated more than 1
					if (Math.ceil(this.shuffleOrder.size() / this.getCurrentList(isPlaylist).getHeapSize()) > 1) {
						int rounds = (int)(Math.floor(this.shuffleOrder.size() / this.getCurrentList(isPlaylist).getHeapSize()));
						a = (List<Song>) shuffleOrder.subList(0, this.shuffleOrder.size()-this.getCurrentList(isPlaylist).getHeapSize()*rounds);
					}
					else
						// case 3: playlist is being repeated the first time
						a = (List<Song>) shuffleOrder.subList(0, this.shuffleOrder.size()-this.getCurrentList(isPlaylist).getHeapSize());
					if (!a.contains(this.getSong(random, isPlaylist))) {
						shuffleOrder.add(0,this.getSong(random, isPlaylist));
						return random;
					}
				}
			}
		}
	}
	
	/**
	 * update the shuffle list when a song is deleted
	 * @param value index of song being deleted
	 */
	public void removeFromShuffleList(Song song) {
		// iterate through the shuffle list, mark all deleted songs
		for (int i = shuffleOrder.size() - 1; i >= 0; i--) {
			if (shuffleOrder.get(i).equals(song)) {
				shuffleOrder.remove(i);
				if (i < this.shuffleSongIndex)
					shuffleSongIndex -= 1;
			}
		}
	}
	
	/**
	 * get the song object
	 * @param index of song in list
	 * @param a the playlist song is in
	 * @return a Song
	 */
	public Song getSong(int index, boolean a) {
		return (Song)this.getCurrentList(a).getNodeValue(index);
	}
	
	/**
	 * add Song to the list
	 * @param song being added
	 * @param a the playlist song is added to
	 */
	public void addToList(Song song, boolean a) {
		this.getCurrentList(a).heapInsert(song);
		song.setIndex(a, this.getCurrentList(a).getHeapSize());
	}
	
	/**
	 * get the index of song being played
	 * @return an int - currentSongIndex
	 */
	public int getCurrentSongIndex() {
		return this.currentSongIndex;
	}
	
	/**
	 * update the index of song being played
	 * @param index index of new song being played
	 */
	public void updateCurrentSongIndex(int index) {
		this.currentSongIndex = index;
	}
	
	/**
	 * get the sortOrder
	 * @return the sortOrder
	 */
	public int getSortOrder() {
		return this.sortOrder;
	}
	
	/**
	 * get whether the music player is playing song
	 * @return true if song is being played, false if not
	 */
	public boolean getIsPlaying() {
		return this.isPlaying;
	}
	
	/**
	 * set whether the music player is playing
	 * @param a true if song is being played, false if not
	 */
	public void setIsPlaying(boolean a) {
		this.isPlaying = !this.isPlaying;
	}
	
	/**
	 * set the playlist currently being played
	 * @param a true if playlist, false if FavList
	 */
	public void setIsPlaylist(boolean a) {
		this.isPlaylist = a;
	}
	
	/**
	 * get the current playlist being played
	 * @return true if playlist, false if FavList
	 */
	public boolean getIsPlaylist() {
		return this.isPlaylist;
	}
	
	/**
	 * get whether music player allows repeat
	 * @return repeat
	 */
	public boolean getRepeat() {
		return this.repeat;
	}
	
	/**
	 * reverse value of repeat
	 */
	public void setRepeat() {
		this.repeat = !this.repeat;
	}
	
	/**
	 * get whether music player is in shuffle mode
	 * @return shuffle
	 */
	public boolean getShuffle() {
		return this.shuffle;
	}
	
	/**
	 * reverse value of shuffle
	 */
	public void setShuffle() {
		this.shuffle = !this.shuffle;
	}
	
	/**
	 * sort the playlist
	 * @param order the sortOrder
	 * @param isPlaylist the playlist being sorted
	 */
	public void sortPlaylist(int order, boolean isPlaylist) {
		// update sortOrder
		this.sortOrder = order;
		
		// if playlist has song
		if (this.getCurrentList(isPlaylist).getHeapSize() > 1) {
			
			// remember the original heapsize then sort the playlist
			int ori_heapsize = this.getCurrentList(isPlaylist).getHeapSize();
			this.getCurrentList(isPlaylist).heapSort();
			
			// restore original heapsize
			this.getCurrentList(isPlaylist).setHeapSize(ori_heapsize);
		}
	}
	
	/**
	 * whether the song exist in playlist
	 * @param path to the song
	 * @return true if song exist, false if not
	 */
	public boolean songExist(String path) {
		// iterate through the playlist, if there's a song with same path, return true
		for (int i = 1; i <= this.playlist.getHeapSize(); i++) {
			if (this.getSong(i, true).getPath().equals(path))
				return true;
		}
		return false;
	}
}

