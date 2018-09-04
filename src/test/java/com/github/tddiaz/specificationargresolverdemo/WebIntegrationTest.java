package com.github.tddiaz.specificationargresolverdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tddiaz.specificationargresolverdemo.Artist.Genre.HIP_HOP;
import static com.github.tddiaz.specificationargresolverdemo.Artist.Genre.INDIE;
import static com.github.tddiaz.specificationargresolverdemo.Artist.Genre.ROCK;
import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = MainApplication.class)
public class WebIntegrationTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private ArtistRepository artistRepository;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Before
    public void setup() {
        Artist artist1 = new Artist("tony", BigDecimal.valueOf(800_001), INDIE);
        artist1.setAlbums(asList(new Album("tony album", LocalDate.of(2000, 12, 01))));

        Artist artist2 = new Artist("steve", BigDecimal.valueOf(900_000), ROCK);
        artist2.setAlbums(asList(new Album("steve album", LocalDate.of(2005, 5, 01))));

        Artist artist3 = new Artist("peter quill", BigDecimal.valueOf(700_001), HIP_HOP);
        artist3.setAlbums(asList(new Album("peter album", LocalDate.of(2003, 3, 01))));

        Artist artist4 = new Artist("peter parker", BigDecimal.valueOf(1_000_000), INDIE);
        artist4.setAlbums(asList(new Album("scott album", LocalDate.of(1996, 8, 01))));

        artistRepository.saveAll(asList(artist1, artist2, artist3, artist4));
    }

    @After
    public void cleanUp() {
        artistRepository.deleteAll();
    }

    @Test
    public void testFilterByArtistName() throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("artistName", "peter");

        Response response = given()
                .log()
                .all()
                .port(serverPort)
                .params(params)
                .contentType(ContentType.JSON)
                .when()
                .get("/artists").andReturn();

        List<Artist> artists = OBJECT_MAPPER.readValue(response.getBody().asString(), List.class);

        assertThat(artists).hasSize(2);
        assertThat(artists).extracting("artistName").contains("peter quill", "peter parker");
    }

    @Test
    public void testFilterByGenre() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("genre", "INDIE");

        Response response = given()
                .log()
                .all()
                .port(serverPort)
                .params(params)
                .contentType(ContentType.JSON)
                .when()
                .get("/artists").andReturn();

        List<Artist> artists = OBJECT_MAPPER.readValue(response.getBody().asString(), List.class);

        assertThat(artists).hasSize(2);
        assertThat(artists).extracting("artistName").contains("tony", "peter parker");
    }

    @Test
    public void testFilterByNetWorthRange() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("netWorthMin", "700000");
        params.put("netWorthMax", "1000000");

        Response response = given()
                .log()
                .all()
                .port(serverPort)
                .params(params)
                .contentType(ContentType.JSON)
                .when()
                .get("/artists").andReturn();

        List<Artist> artists = OBJECT_MAPPER.readValue(response.getBody().asString(), List.class);

        assertThat(artists).hasSize(4);
    }

    @Test
    public void testFilterByReleaseDateRange() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("albumReleaseDateAfter", "2000-01-01");
        params.put("albumReleaseDateBefore", "2005-01-01");

        Response response = given()
                .log()
                .all()
                .port(serverPort)
                .params(params)
                .contentType(ContentType.JSON)
                .when()
                .get("/artists").andReturn();

        List<Artist> artists = OBJECT_MAPPER.readValue(response.getBody().asString(), List.class);

        assertThat(artists).hasSize(2);
        assertThat(artists).extracting("artistName").contains("tony", "steve", "peter quill");
    }
}
