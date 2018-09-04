package com.github.tddiaz.specificationargresolverdemo;

import lombok.Data;

import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@Data
public class Album extends AbstractEntity {
    private String name;
    private LocalDate releaseDate;

    public Album(String name, LocalDate releaseDate) {
        this.name = name;
        this.releaseDate = releaseDate;
    }
}
