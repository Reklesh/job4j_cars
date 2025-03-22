package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "auto_user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String login;
    private String password;
}