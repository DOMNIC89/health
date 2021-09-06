package com.example.health.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Blob;
import java.util.List;

@Entity
public class Thumbnail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String size;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "thumbnail_image")
    private byte[] image;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "video_id")
    private Video video;

    public Video getVideos() {
        return video;
    }

    public void setVideos(Video video) {
        this.video = video;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Thumbnail(String size, byte[] image) {
        this.size = size;
        this.image = image;
    }

    public Thumbnail() {
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
