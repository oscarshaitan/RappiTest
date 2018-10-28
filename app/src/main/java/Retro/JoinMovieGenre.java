package Retro;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class JoinMovieGenre {
    @Id
    private Long id;
    private Long movieId;
    private Long genreId;
    @Generated(hash = 1026984139)
    public JoinMovieGenre(Long id, Long movieId, Long genreId) {
        this.id = id;
        this.movieId = movieId;
        this.genreId = genreId;
    }
    @Generated(hash = 2106346425)
    public JoinMovieGenre() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getMovieId() {
        return this.movieId;
    }
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
    public Long getGenreId() {
        return this.genreId;
    }
    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }
}
