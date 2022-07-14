package com.grispi.bootcamp.restservice.controller;

import com.grispi.bootcamp.restservice.model.Movie;
import com.grispi.bootcamp.restservice.model.Player;
import com.grispi.bootcamp.restservice.repository.MovieRepository;
import com.grispi.bootcamp.restservice.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/movies")
@Transactional
public class MovieController {

    @Autowired
    private final MovieRepository movieRepository;
    @Autowired
    private final PlayerRepository playerRepository;

    public MovieController(MovieRepository movieRepository, PlayerRepository playerRepository) {
        this.movieRepository = movieRepository;
        this.playerRepository = playerRepository;
    }

    @GetMapping
    public List<Movie> getMovies(){

        return (List<Movie>) movieRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie){

        movieRepository.save(movie);
//        movies.add(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(movie);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getById(@PathVariable Long id){
        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(optionalMovie.get());
        }
        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        if (movieRepository.existsById(id)){
            movieRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movie){
        Optional<Movie> movie1 = movieRepository.findById(id);
        if (movie1.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        movie1.get().setGenreList(movie.getGenreList());

        //movie1.setPlayerList(movie.getPlayerList());
        movieRepository.save(movie1.get());
        return ResponseEntity.status(HttpStatus.OK).body(movie1.get());

    }

    @PostMapping("/{id}/players")
    public ResponseEntity<?> createPlayersOfMovie(@PathVariable Long id, @RequestBody List<Player> players){
        if (!movieRepository.existsById(id))
            return ResponseEntity.notFound().build();
        Optional<Movie> movie = movieRepository.findById(id);
        players.forEach(player -> {
            if (player.getId() == null){
                playerRepository.save(player);
                movie.get().getPlayerList().add(player);
            } else if (playerRepository.existsById(id)) {
                movie.get().getPlayerList().add(player);
            }
        });
        movieRepository.save(movie.get());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/{id}/players")
    public List<Player> playersOfMovie(@PathVariable Long id){
        return movieRepository.findById(id).get().getPlayerList();
    }

}
