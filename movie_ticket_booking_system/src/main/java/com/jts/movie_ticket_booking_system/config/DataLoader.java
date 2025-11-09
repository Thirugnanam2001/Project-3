package com.jts.movie_ticket_booking_system.config;

import com.jts.movie_ticket_booking_system.entity.*;
import com.jts.movie_ticket_booking_system.enums.Genre;
import com.jts.movie_ticket_booking_system.enums.Language;
import com.jts.movie_ticket_booking_system.enums.SeatType;
import com.jts.movie_ticket_booking_system.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.jts.movie_ticket_booking_system.enums.Gender;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private TheaterSeatRepository theaterSeatRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("DataLoader: Starting data initialization...");

        try {
            // Wait a bit to ensure tables are created
            Thread.sleep(2000);

            // Create users only if table exists and is empty
            createUsers();

            // Create movies only if table is empty
            createMovies();

            // Create theaters with seats only if table is empty
            createTheatersWithSeats();

            // Create shows with show seats only if table is empty
            List<Movie> movies = movieRepository.findAll();
            List<Theater> theaters = theaterRepository.findAll();
            createShowsWithSeats(movies, theaters);

            System.out.println("DataLoader: Data initialization completed successfully");
        } catch (Exception e) {
            System.err.println("DataLoader: Error during initialization - " + e.getMessage());
            e.printStackTrace();
            // Don't crash the application - just log the error
        }
    }

//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("DataLoader: Starting data initialization...");
//
//        // Create users
//        createUsers();
//
//        // Create movies
//        List<Movie> movies = createMovies();
//
//        // Create theaters with seats
//        List<Theater> theaters = createTheatersWithSeats();
//
//        // Create shows with show seats
//        createShowsWithSeats(movies, theaters);
//
//        System.out.println("DataLoader: Data initialization completed");
//    }

    private void createUsers() {
        // Create admin user if not exists
        if (userRepository.findByEmailId("gnanamdon2001@gmail.com").isEmpty()) {
            User admin = User.builder()
                    .name("System Administrator")
                    .emailId("gnanamdon2001@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .roles("ROLE_ADMIN,ROLE_USER")
                    .gender(Gender.OTHER)
                    .mobileNo("7604848659")
                    .address("System Address")
                    .age(25)
                    .build();
            userRepository.save(admin);
            System.out.println("✓ Admin user created: gnanamdon2001@gmail.com / admin123");
        }

        // Create sample user if not exists
        if (userRepository.findByEmailId("user@example.com").isEmpty()) {
            User user = User.builder()
                    .name("John Doe")
                    .emailId("user@example.com")
                    .password(passwordEncoder.encode("user1234"))
                    .roles("ROLE_USER")
                    .gender(Gender.MALE)
                    .mobileNo("8888888888")
                    .address("123 Main Street, City")
                    .age(25)
                    .build();
            userRepository.save(user);
            System.out.println("✓ Sample user created: user@example.com / user1234");
        }
    }

    private List<Movie> createMovies() {
        List<Movie> movies = new ArrayList<>();

        if (movieRepository.count() == 0) {
            System.out.println("Adding sample movies to database...");

            Movie movie1 = Movie.builder()
                    .movieName("Avatar: The Way of Water")
                    .genre(Genre.ACTION)
                    .language(Language.ENGLISH)
                    .rating(4.5)
                    .duration(192)
                    .releaseDate(Date.valueOf("2025-10-25"))
                    .build();

            Movie movie2 = Movie.builder()
                    .movieName("Pathaan")
                    .genre(Genre.ACTION)
                    .language(Language.HINDI)
                    .rating(4.2)
                    .duration(146)
                    .releaseDate(Date.valueOf("2025-10-25"))
                    .build();

            Movie movie3 = Movie.builder()
                    .movieName("Kantara")
                    .genre(Genre.DRAMA)
                    .language(Language.KANNADA)
                    .rating(4.8)
                    .duration(148)
                    .releaseDate(Date.valueOf("2025-10-10"))
                    .build();

            Movie movie4 = Movie.builder()
                    .movieName("Drishyam 2")
                    .genre(Genre.THRILLER)
                    .language(Language.HINDI)
                    .rating(4.3)
                    .duration(140)
                    .releaseDate(Date.valueOf("2025-10-25"))
                    .build();

            Movie movie5 = Movie.builder()
                    .movieName("Vikram")
                    .genre(Genre.ACTION)
                    .language(Language.TAMIL)
                    .rating(4.7)
                    .duration(175)
                    .releaseDate(Date.valueOf("2025-09-15"))
                    .build();

            Movie movie6 = Movie.builder()
                    .movieName("Ponniyin Selvan: Part 1")
                    .genre(Genre.HISTORICAL)
                    .language(Language.TAMIL)
                    .rating(4.6)
                    .duration(170)
                    .releaseDate(Date.valueOf("2025-08-20"))
                    .build();

//            Movie movie7 = Movie.builder()
//                    .movieName("Master")
//                    .genre(Genre.ACTION)
//                    .language(Language.TAMIL)
//                    .rating(4.4)
//                    .duration(179)
//                    .releaseDate(Date.valueOf("2025-01-13"))
//                    .build();
//
//            Movie movie8 = Movie.builder()
//                    .movieName("Leo")
//                    .genre(Genre.ACTION)
//                    .language(Language.TAMIL)
//                    .rating(4.5)
//                    .duration(164)
//                    .releaseDate(Date.valueOf("2025-10-19"))
//                    .build();
//
//            Movie movie9 = Movie.builder()
//                    .movieName("Vada Chennai")
//                    .genre(Genre.DRAMA)
//                    .language(Language.TAMIL)
//                    .rating(4.8)
//                    .duration(164)
//                    .releaseDate(Date.valueOf("2025-10-17"))
//                    .build();
//
//            Movie movie10 = Movie.builder()
//                    .movieName("Jailer")
//                    .genre(Genre.ACTION)
//                    .language(Language.TAMIL)
//                    .rating(4.6)
//                    .duration(168)
//                    .releaseDate(Date.valueOf("2025-08-10"))
//                    .build();

            movies.add(movieRepository.save(movie1));
            movies.add(movieRepository.save(movie2));
            movies.add(movieRepository.save(movie3));
            movies.add(movieRepository.save(movie4));
            movies.add(movieRepository.save(movie5));
            movies.add(movieRepository.save(movie6));
//            movies.add(movieRepository.save(movie7));
//            movies.add(movieRepository.save(movie8));
//            movies.add(movieRepository.save(movie9));
//            movies.add(movieRepository.save(movie10));

            System.out.println("✓ Added 4 sample movies to database");
        } else {
            movies = movieRepository.findAll();
            System.out.println("✓ Database already contains " + movies.size() + " movies");
        }

        return movies;
    }

    private List<Theater> createTheatersWithSeats() {
        List<Theater> theaters = new ArrayList<>();

        if (theaterRepository.count() == 0) {
            System.out.println("Creating theaters with seats...");

            // Theater 1: PVR Cinemas
            Theater theater1 = Theater.builder()
                    .name("PVR Cinemas - Phoenix Marketcity")
                    .address("Phoenix Marketcity, Mahadevpura, Bangalore")
                    .theaterSeatList(new ArrayList<>())
                    .showList(new ArrayList<>())
                    .build();
            theater1 = theaterRepository.save(theater1);
            createTheaterSeats(theater1, 8, 12, 2); // 8 rows, 12 seats per row, 2 premium rows
            theaters.add(theater1);

            // Theater 2: INOX
            Theater theater2 = Theater.builder()
                    .name("INOX - Orion Mall")
                    .address("Orion Mall, Brigade Gateway, Bangalore")
                    .theaterSeatList(new ArrayList<>())
                    .showList(new ArrayList<>())
                    .build();
            theater2 = theaterRepository.save(theater2);
            createTheaterSeats(theater2, 10, 14, 1); // 7 rows, 10 seats per row, 1 premium row
            theaters.add(theater2);

            // Theater 3: Cinepolis
            Theater theater3 = Theater.builder()
                    .name("Cinepolis - Forum Mall")
                    .address("Forum Mall, Koramangala, Bangalore")
                    .theaterSeatList(new ArrayList<>())
                    .showList(new ArrayList<>())
                    .build();
            theater3 = theaterRepository.save(theater3);
            createTheaterSeats(theater3, 7, 14, 2); // 6 rows, 14 seats per row, 2 premium rows
            theaters.add(theater3);


            Theater theater4 = Theater.builder()
                    .name("Carnival Cinemas - Elements Mall")
                    .address("Elements Mall, Nagarbhavi, Bangalore")
                    .theaterSeatList(new ArrayList<>())
                    .showList(new ArrayList<>())
                    .build();
            theater4 = theaterRepository.save(theater4);
            createTheaterSeats(theater4, 9, 15, 3); // 9 rows, 18 seats per row, 3 premium rows
            theaters.add(theater4);


//            Theater theater5 = Theater.builder()
//                    .name("Gopalan Cinemas - Mall of Mysore")
//                    .address("Mall of Mysore, Mysore Road, Bangalore")
//                    .theaterSeatList(new ArrayList<>())
//                    .showList(new ArrayList<>())
//                    .build();
//            theater5 = theaterRepository.save(theater5);
//            createTheaterSeats(theater5, 10, 10, 1); // 5 rows, 10 seats per row, 1 premium row
//            theaters.add(theater5);


//            Theater theater6 = Theater.builder()
//                    .name("Innovative Multiplex - Marathahalli")
//                    .address("Innovative Multiplex, Marathahalli, Bangalore")
//                    .theaterSeatList(new ArrayList<>())
//                    .showList(new ArrayList<>())
//                    .build();
//            theater6 = theaterRepository.save(theater6);
//            createTheaterSeats(theater6, 12, 15, 2); // 8 rows, 15 seats per row, 2 premium rows
//            theaters.add(theater6);

            System.out.println("✓ Created 3 theaters with seating arrangements");
        } else {
            theaters = theaterRepository.findAll();
            System.out.println("✓ Database already contains " + theaters.size() + " theaters");
        }

        return theaters;
    }

    private void createTheaterSeats(Theater theater, int rows, int seatsPerRow, int premiumRows) {
        List<TheaterSeat> seats = new ArrayList<>();
        char seatLetter = 'A';

        for (int row = 1; row <= rows; row++) {
            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                String seatNo = row + String.valueOf(seatLetter);

                TheaterSeat theaterSeat = TheaterSeat.builder()
                        .seatNo(seatNo)
                        .seatType(row <= premiumRows ? SeatType.PREMIUM : SeatType.CLASSIC)
                        .theater(theater)
                        .build();

                seats.add(theaterSeat);
                seatLetter++;
            }
            seatLetter = 'A'; // Reset for next row
        }

        theaterSeatRepository.saveAll(seats);
        // Ensure theaterSeatList is initialized
        if (theater.getTheaterSeatList() == null) {
            theater.setTheaterSeatList(new ArrayList<>());
        }
        theater.getTheaterSeatList().addAll(seats);
        theaterRepository.save(theater);
//        theater.setTheaterSeatList(seats);
//        theaterRepository.save(theater);

        System.out.println("✓ Created " + seats.size() + " seats for " + theater.getName());
    }

    private void createShowsWithSeats(List<Movie> movies, List<Theater> theaters) {
        if (showRepository.count() == 0) {
            System.out.println("Creating shows with show seats...");

            // Show dates (next 7 days)
            Date[] showDates = {
                    Date.valueOf("2025-10-30"),
                    Date.valueOf("2025-10-31"),
                    Date.valueOf("2025-11-01"),
                    Date.valueOf("2025-11-02"),
                    Date.valueOf("2025-11-03"),
                    Date.valueOf("2025-11-04"),
                    Date.valueOf("2025-11-05"),
                    Date.valueOf("2025-11-06"),
                    Date.valueOf("2025-11-07"),
                    Date.valueOf("2025-11-08"),
                    Date.valueOf("2025-11-09"),
                    Date.valueOf("2025-11-10"),
                    Date.valueOf("2025-11-11"),
                    Date.valueOf("2025-11-12"),
                    Date.valueOf("2025-11-13"),
                    Date.valueOf("2025-11-14"),
                    Date.valueOf("2025-11-15"),
                    Date.valueOf("2025-11-16"),
                    Date.valueOf("2025-11-17"),
                    Date.valueOf("2025-11-18"),
                    Date.valueOf("2025-11-19"),
                    Date.valueOf("2025-11-20"),
                    Date.valueOf("2025-11-21"),
                    Date.valueOf("2025-11-22")
//                    Date.valueOf("2025-11-23"),
//                    Date.valueOf("2025-11-24"),
//                    Date.valueOf("2025-11-25"),
//                    Date.valueOf("2025-11-26"),
//                    Date.valueOf("2025-11-27"),
//                    Date.valueOf("2025-11-28"),
//                    Date.valueOf("2025-11-29"),
//                    Date.valueOf("2025-11-30")
            };

            // Show times
            Time[] showTimes = {
                    Time.valueOf("10:00:00"),
                    Time.valueOf("11:00:00"),
                    Time.valueOf("13:30:00"),
                    Time.valueOf("16:00:00"),
//                    Time.valueOf("15:00:00"),
//                    Time.valueOf("16:00:00"),
                    Time.valueOf("18:00:00"),
                    Time.valueOf("19:00:00"),
                    Time.valueOf("20:00:00"),
                    Time.valueOf("21:30:00"),
                    Time.valueOf("22:30:00")
            };

            int showCount = 0;

            // Create shows for each movie in each theater for different dates and times
            for (Movie movie : movies) {
                for (Theater theater : theaters) {
                    for (Date showDate : showDates) {
                        for (Time showTime : showTimes) {
                            Show show = Show.builder()
                                    .time(showTime)
                                    .date(showDate)
                                    .movie(movie)
                                    .theater(theater)
                                    .showSeatList(new ArrayList<>())
                                    .ticketList(new ArrayList<>())
                                    .build();

                            show = showRepository.save(show);
                            createShowSeats(show, theater);
                            showCount++;
                        }
                    }
                }
            }

            System.out.println("✓ Created " + showCount + " shows with show seats");
        } else {
            System.out.println("✓ Database already contains " + showRepository.count() + " shows");
        }
    }

    private void createShowSeats(Show show, Theater theater) {
        List<ShowSeat> showSeats = new ArrayList<>();
//        List<TheaterSeat> theaterSeats = theater.getTheaterSeatList();
        List<TheaterSeat> theaterSeats = theater.getTheaterSeatList() != null ?
                theater.getTheaterSeatList() : new ArrayList<>();

        for (TheaterSeat theaterSeat : theaterSeats) {
            ShowSeat showSeat = ShowSeat.builder()
                    .seatNo(theaterSeat.getSeatNo())
                    .seatType(theaterSeat.getSeatType())
                    .price(theaterSeat.getSeatType() == SeatType.PREMIUM ? 350 : 200)
                    .isAvailable(true)
                    .isFoodContains(false)
                    .show(show)
                    .build();

            showSeats.add(showSeat);
        }

        showSeatRepository.saveAll(showSeats);
        if (show.getShowSeatList() == null) {
            show.setShowSeatList(new ArrayList<>());
        }
        show.getShowSeatList().addAll(showSeats);
        showRepository.save(show);
//        show.setShowSeatList(showSeats);
//        showRepository.save(show);
    }
}