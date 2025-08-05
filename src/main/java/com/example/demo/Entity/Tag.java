//package com.example.demo.Entity;
//
//import jakarta.persistence.*;
//import java.util.HashSet;
//import java.util.Set;
//
//@Entity
//public class Tag {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    private String name;
//
//    @ManyToMany(mappedBy = "tags")
//    private Set<Article> articles = new HashSet<>();
//
//    public Tag() {}
//
//    public Tag(String name) {
//        this.name = name;
//    }
//
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//
//    public Set<Article> getArticles() { return articles; }
//    public void setArticles(Set<Article> articles) { this.articles = articles; }
//}
