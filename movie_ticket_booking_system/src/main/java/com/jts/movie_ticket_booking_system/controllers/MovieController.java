
package com.jts.movie_ticket_booking_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jts.movie_ticket_booking_system.entity.Movie;
import com.jts.movie_ticket_booking_system.request.MovieRequest;
import com.jts.movie_ticket_booking_system.services.MovieService;

import java.util.*;

@Controller
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;
    // In MovieController.java

    @GetMapping
    public String getAllMovies(Model model) {
        try {
            List<Movie> movies = movieService.getAllMovies();

            // Pre-format dates to avoid Thymeleaf Temporals issues
            for (Movie movie : movies) {
                if (movie.getReleaseDate() != null) {
                    // Convert to java.util.Date for Thymeleaf compatibility
                    movie.setReleaseDate(new java.sql.Date(movie.getReleaseDate().getTime()));
                }
            }

            model.addAttribute("movies", movies);
            model.addAttribute("pageTitle", "All Movies - Movie Ticket Browser");
            return "movies";
        } catch (Exception e) {
            System.err.println("ERROR in MovieController: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading movies: " + e.getMessage());
            model.addAttribute("movies", new ArrayList<Movie>());
            return "movies";
        }
    }
    @GetMapping("/test")
    @ResponseBody
    public String testMovies() {
        try {
            List<Movie> movies = movieService.getAllMovies();
            return "Movies in database: " + movies.size() +
                    "<br>Movies: " + movies.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
//    @GetMapping
//    public String getAllMovies(Model model) {
//        List<Movie> movies = movieService.getAllMovies(); // Actually call service
//        model.addAttribute("movies", movies);
//        model.addAttribute("pageTitle", "All Movies - Movie Ticket Browser");
//        return "movies";
//    }
@GetMapping("/simple")
public String getSimpleMovies(Model model) {
    List<Movie> movies = movieService.getAllMovies();
    model.addAttribute("movies", movies);
    return "movies-simple";
}


    @GetMapping("/{id}")
    public String getMovieDetails(@PathVariable Integer id, Model model) {
        // Movie details would be fetched from service
        model.addAttribute("pageTitle", "Movie Details - Movie Ticket Browser");
        return "movie-details";
    }

    @GetMapping("/admin/add")
    public String showAddMovieForm(Model model) {
        model.addAttribute("movieRequest", new MovieRequest());
        model.addAttribute("pageTitle", "Add Movie - Admin");
        return "admin/add-movie";
    }

    @PostMapping("/admin/add")
    public String addMovie(@ModelAttribute MovieRequest movieRequest,
                          RedirectAttributes redirectAttributes) {
        try {
            String result = movieService.addMovie(movieRequest);
            redirectAttributes.addFlashAttribute("successMessage", result);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/movies/admin/add";
    }
    @PostMapping("/api/admin/add")
    @ResponseBody
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> addMovieApi(@RequestBody MovieRequest movieRequest) {
        try {
            String result = movieService.addMovie(movieRequest);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", result);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    @GetMapping("/api/details/{id}")
    @ResponseBody
    public ResponseEntity<?> getMovieById(@PathVariable Integer id) {
        try {
            // For now, return placeholder
            Map<String, String> response = new HashMap<>();
            response.put("message", "Movie details endpoint - implement getMovieById in service");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}