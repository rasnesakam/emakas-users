package com.emakas.userService.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "teams")
public class Team extends BaseEntity{
    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @CollectionTable(name = "team_members", joinColumns = @JoinColumn(name = "member_id"))

    private Collection<User> members;

    @Column
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @CollectionTable(name = "team_leads", joinColumns = @JoinColumn(name = "team_lead_id"))
    private User lead;

    @PrePersist
    public void prePersist(){
        if (lead != null && members != null && !members.contains(lead)) {
            members.add(lead);
        }
    }
}
