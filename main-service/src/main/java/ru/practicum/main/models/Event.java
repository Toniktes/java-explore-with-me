package ru.practicum.main.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.Pattern;
import ru.practicum.main.enums.EventState;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Entity
@Table(name = "events")
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 2000)
    private String annotation;
    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    private Long confirmedRequests;
    @Column(name = "created_On")
    private LocalDateTime createdOn;
    @Column(length = 7000)
    private String description;
    private LocalDateTime eventDate;
    @OneToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;
    @Column(columnDefinition = "boolean default false")
    private Boolean paid;
    @Column(columnDefinition = "integer default 0")
    private Integer participantLimit;
    private LocalDateTime publishedOn;
    @Column(columnDefinition = "boolean default true")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private EventState state;
    @Size(min = 3, max = 120)
    private String title;
    private Long views;
    @Transient
    private final String datePattern = Pattern.DATE;
    @Transient
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);

}
