package it.unibo.oop.lab.streams;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return songs.stream()
                    .map(p->p.getSongName())
                    .sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return albums.keySet().stream();                     
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        Stream<String> s = albums.entrySet().stream()
                              .map(entry -> Map.entry(entry.getKey(),entry.getValue()))
                              .filter(p-> p.getValue()==year)
                              .map(entry->entry.getKey())
                              .map(Object :: toString);
                              
        return s;
    }

    @Override
    public int countSongs(final String albumName) {
        return (int)songs.stream()
                         .filter(p->p.getAlbumName().isPresent())
                         .filter(p->p.getAlbumName().get() ==albumName)
                         .count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int)songs.stream()
                    .filter(p->!p.getAlbumName().isPresent())
                    .count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return songs.stream()
                    .filter(s -> s.getAlbumName().filter(it -> it.equals(albumName)).isPresent())
                    .mapToDouble(Song::getDuration)
                    .average();
    }

    @Override
    public Optional<String> longestSong() {
        return songs.stream()
                    .max(Comparator.comparingDouble(Song::getDuration))
                    .map(Song :: getSongName);
    }

    @Override
    public Optional<String> longestAlbum() {
        return songs.stream()
                    .max(Comparator.comparingDouble(Song::getDuration))
                    .map(Song :: getAlbumName).get();
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}