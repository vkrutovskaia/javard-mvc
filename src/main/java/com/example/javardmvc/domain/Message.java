package com.example.javardmvc.domain;

import static java.util.Objects.nonNull;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String text;
  private String tag;

  @ManyToOne(fetch = EAGER)
  @JoinColumn(name = "user_id")
  private User author;

  public Message() {
  }

  public Message(String text, String tag, User user) {
    this.text = text;
    this.tag = tag;
    this.author = user;
  }

  public String getAuthorName() {
    return nonNull(author) ? author.getUsername() : "<none>";
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }
}
