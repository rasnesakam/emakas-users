package com.emakas.userService.model;

import com.emakas.userService.shared.StringUtils;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "teams")
public class Team extends BaseEntity{
    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column(unique = true)
    private String uri;

    @ManyToOne
    @JoinColumn(name = "parent_team_id")
    private Team parentTeam;

    @OneToMany(mappedBy = "parentTeam", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Collection<Team> childTeams;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "team_members", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Collection<User> members;

    @JoinColumn(name = "team_lead_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private User lead;

    @PrePersist
    public void prePersist(){
        if (lead != null && members != null && !members.contains(lead)) {
            members.add(lead);
        }
        if (Objects.isNull(members) || members.isEmpty()) {
            members = new ArrayList<>();
            members.add(lead);
        }
        if (StringUtils.isNullOrEmpty(uri)) {
            uri = StringUtils.normalizeString(name);
        }
    }
}
