package ru.trkpo.common.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "clients")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @Column(name = "patronymic", length = 50)
    private String patronymic;

    @Column(name = "age")
    private Integer age;

    @Column(name = "birthday")
    private LocalDate birthday;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private PhoneNumber phoneNumber;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private ClientDetails clientDetails;
}
