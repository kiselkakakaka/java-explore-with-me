package ru.practicum.ewm.main.compilation;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import ru.practicum.ewm.main.event.Event;

@Entity
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false)
    private boolean pinned = false;

    @ManyToMany
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> events = new HashSet<>();

    public Compilation() {
    }

    public Compilation(Long id, String title, boolean pinned, Set<Event> events) {
        this.id = id;
        this.title = title;
        this.pinned = pinned;
        this.events = events;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isPinned() {
        return pinned;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }
}