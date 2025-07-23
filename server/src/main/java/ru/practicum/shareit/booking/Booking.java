package ru.practicum.shareit.booking;

import lombok.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @ManyToOne()
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;
    @ManyToOne()
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker;
    @Enumerated(EnumType.STRING)
    private Status status;
}