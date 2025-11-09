package com.jts.movie_ticket_booking_system.repository; // MUST be repositories (plural)

import com.jts.movie_ticket_booking_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;


//import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmailId(String emailId);
    boolean existsByEmailId(String emailId);
    Optional<User> findByMobileNo(String mobileNo);
    // Custom query to get all users sorted by name
//    @Query("SELECT u FROM User u ORDER BY u.name")
//    List<User> findAllUsersOrderByName();
}