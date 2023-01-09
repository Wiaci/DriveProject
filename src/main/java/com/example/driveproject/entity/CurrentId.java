package com.example.driveproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "current_ids")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CurrentId {

    @Id
    private String id;

    @Column(name = "current_id")
    private String currentId;

}
