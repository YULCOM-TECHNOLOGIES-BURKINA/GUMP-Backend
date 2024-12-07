package com.yulcomtechnologies.justicems.entities;

import com.yulcomtechnologies.sharedlibrary.services.FileInterface;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
@Data
@Builder
public class File implements FileInterface {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String path;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public File(String label, String path) {
        this.label = label;
        this.path = path;
        createdAt = LocalDateTime.now();
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getId() {
        return id.toString();
    }
}
