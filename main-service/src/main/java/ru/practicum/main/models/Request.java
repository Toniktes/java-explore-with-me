package ru.practicum.main.models;

import lombok.*;
import ru.practicum.main.enums.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime created;
    private Long event;
    private Long requester;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public Request(LocalDateTime created, Long event, Long requester, RequestStatus status) {
        this.created = created;
        this.event = event;
        this.requester = requester;
        this.status = status;
    }
}
