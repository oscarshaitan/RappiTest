package Retro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class Genre {

    @SerializedName("id")
    @Expose
    @Id
    private Integer id;
    @SerializedName("name")
    @Expose
    @NotNull
    private String name;

    @Generated(hash = 676789056)
    public Genre(Integer id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    @Generated(hash = 235763487)
    public Genre() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
