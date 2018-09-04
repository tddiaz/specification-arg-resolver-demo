package com.github.tddiaz.specificationargresolverdemo;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class Artist extends AbstractEntity {

    private String artistName;

    private BigDecimal netWorth;

    @Enumerated
    private Genre genre;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "artist_id", referencedColumnName = "id")
    private List<Album> albums;

    public Artist(String name, BigDecimal netWorth, Genre genre) {
        this.artistName = name;
        this.netWorth = netWorth;
        this.genre = genre;
    }

    enum Genre {
        INDIE, HIP_HOP, ROCK
    }
}
