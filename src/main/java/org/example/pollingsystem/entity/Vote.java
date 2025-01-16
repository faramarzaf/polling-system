package org.example.pollingsystem.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String voteOption;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getVoteOption() {
        return voteOption;
    }

    public void setVoteOption(String voteOption) {
        this.voteOption = voteOption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(id, vote.id) && Objects.equals(poll, vote.poll) && Objects.equals(user, vote.user) && Objects.equals(voteOption, vote.voteOption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, poll, user, voteOption);
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", poll=" + poll +
                ", user=" + user +
                ", voteOption='" + voteOption + '\'' +
                '}';
    }
}
