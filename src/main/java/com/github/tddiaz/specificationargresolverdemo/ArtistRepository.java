package com.github.tddiaz.specificationargresolverdemo;

import net.kaczmarzyk.spring.data.jpa.domain.*;
import net.kaczmarzyk.spring.data.jpa.utils.QueryContext;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface ArtistRepository extends JpaRepository<Artist, Long>, JpaSpecificationExecutor<Artist> {

    @Join(path = "albums", alias = "album")
    @And({
            @Spec(path="artistName", params="artistName", spec= LikeIgnoreCase.class),
            @Spec(path="genre", params="genre", spec= Equal.class),
            @Spec(path="netWorth", params="netWorthMin", spec= GreaterThanOrEqual.class),
            @Spec(path="netWorth", params="netWorthMax", spec= LessThanOrEqual.class),
            @Spec(path = "album.releaseDate", params = {"albumReleaseDateAfter","albumReleaseDateBefore"},
                    spec = LocalDateBetween.class),
            @Spec(path = "album.name", params = "albumName", spec = LikeIgnoreCase.class)
    })
    interface ArtistSpecification extends Specification<Artist> {

    }

    class LocalDateBetween<T> extends PathSpecification<T> {

        private LocalDate lowerBound;
        private LocalDate upperBound;

        public LocalDateBetween(QueryContext queryContext, String path, String[] args) throws ParseException {
            super(queryContext, path);
            if (args == null || args.length != 2) {
                throw new IllegalArgumentException("expected 2 http params (date boundaries), but was: " + args);
            }
            String afterDateStr = args[0];
            String beforeDateStr = args[1];

            this.lowerBound = LocalDate.parse(afterDateStr, DateTimeFormatter.ISO_DATE);
            this.upperBound = LocalDate.parse(beforeDateStr, DateTimeFormatter.ISO_DATE);
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.between(this.<LocalDate>path(root), lowerBound, upperBound);
        }
    }
}
