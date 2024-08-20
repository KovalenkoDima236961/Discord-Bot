package com.dimon.discord_bot.repository;

import com.dimon.discord_bot.model.BirthdayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BirthdayRepository extends JpaRepository<BirthdayEntity, Long> {
    Optional<BirthdayEntity> findByUserId(Long userId);
    List<BirthdayEntity> findByBirthday(LocalDate date);
}
