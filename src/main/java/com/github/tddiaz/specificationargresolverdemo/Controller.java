package com.github.tddiaz.specificationargresolverdemo;

import lombok.AllArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.DateBetween;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class Controller {

    private final ArtistRepository artistRepository;

    @GetMapping("/artists")
    public ResponseEntity<List<Artist>> getAll(ArtistSpecification specification, Pageable pageable) {
        return ResponseEntity.ok().body(artistRepository.findAll(specification,pageable).getContent());
    }

    @Join(path = "albums", alias = "album")
    @And({
            @Spec(path="artistName", params="artistName", spec= LikeIgnoreCase.class),
            @Spec(path="genre", params="genre", spec= Equal.class),
            @Spec(path="netWorth", params="netWorthMin", spec= GreaterThanOrEqual.class),
            @Spec(path="netWorth", params="netWorthMax", spec= LessThanOrEqual.class),
            @Spec(path = "album.releaseDate", params = {"albumReleaseDateAfter","albumReleaseDateBefore"}, spec = DateBetween.class),
            @Spec(path = "album.name", params = "albumName", spec = LikeIgnoreCase.class)
    })
    public interface ArtistSpecification extends Specification<Artist> {

    }
}
