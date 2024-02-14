package finalproject.ss3v2.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Entity(name = "posts")
@AllArgsConstructor// from lombok
@NoArgsConstructor // from lombok
@Data // from lombok
@EqualsAndHashCode // from lombok
@SuperBuilder // from lombok
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String postTitle;

    @Column(name = "post_content", columnDefinition = "TEXT", nullable = false)
    private String postContent;

    @CreationTimestamp  // this annotation is to set the createdAt field to the current time when the post is created
    @Column(name = "post_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime postTime;

    @CreationTimestamp
    @Column(name = "post_update_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime postUpdateTime;

    @ManyToOne
    @JoinColumn(name = "user_id")//FetchType.EAGER is the default
    private User user;


    // this two annotations automatically set these timestamps when an entity is created or updated.
    // they serve the same purpose as the @CreationTimestamp annotation in the User class, but they aren't part of
    // Hibernate, so they need to be implemented manually. Use this here to teach myself how to do it. CHAT-GPT-4
    @PrePersist  // this annotation is to set the postTime field to the current time when the post is created
    protected void onCreate() {
        postTime = LocalDateTime.now(); // Or UTC time based on application requirement
    }

    @PreUpdate  //annotations to automatically set these timestamps when an entity is created or updated.
    protected void onUpdate() {
        postUpdateTime = LocalDateTime.now(); // Or UTC time
    }
}
