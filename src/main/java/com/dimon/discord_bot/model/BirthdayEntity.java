package com.dimon.discord_bot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class BirthdayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private LocalDate birthday;

    // Constructors, getters, and setters
    public BirthdayEntity() {}

    public BirthdayEntity(Long userId, LocalDate birthday) {
        this.userId = userId;
        this.birthday = birthday;
    }
}
