package com.dimon.discord_bot.repository;

import com.dimon.discord_bot.model.Joke;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JokeRepository extends JpaRepository<Joke, Long> {
    @Query(value = "SELECT * FROM joke ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Joke findRandomJoke();
}
