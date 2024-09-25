package org.scouts105bentaya.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class PollOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "pollOption", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PollAttachment> attachments;

    @OneToMany(mappedBy = "pollOption", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PollVote> votes;

    @JsonIgnore
    @ManyToOne(optional = false)
    private Poll poll;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PollAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<PollAttachment> attachments) {
        this.attachments = attachments;
    }

    public List<PollVote> getVotes() {
        return votes;
    }

    public void setVotes(List<PollVote> votes) {
        this.votes = votes;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll polls) {
        this.poll = polls;
    }
}
