package com.hufs.haigram.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "post_media", indexes = @Index(name = "idx_post_media_post_order", columnList = "post_id,orderIndex"))
public class PostMediaEntity {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Column(length = 16, nullable = false)
    private String type;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String src;

    @Column(columnDefinition = "LONGTEXT")
    private String posterUrl;

    @Column(length = 255, nullable = false)
    private String altText;

    @Column(nullable = false)
    private int orderIndex;

    protected PostMediaEntity() {
    }

    public PostMediaEntity(String id, PostEntity post, String type, String src, String posterUrl, String altText, int orderIndex) {
        this.id = id;
        this.post = post;
        this.type = type;
        this.src = src;
        this.posterUrl = posterUrl;
        this.altText = altText;
        this.orderIndex = orderIndex;
    }

    public String getId() {
        return id;
    }

    public PostEntity getPost() {
        return post;
    }

    public String getType() {
        return type;
    }

    public String getSrc() {
        return src;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getAltText() {
        return altText;
    }

    public int getOrderIndex() {
        return orderIndex;
    }
}
