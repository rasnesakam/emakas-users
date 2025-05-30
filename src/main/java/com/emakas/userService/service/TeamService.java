package com.emakas.userService.service;

import com.emakas.userService.model.Team;
import com.emakas.userService.model.User;
import com.emakas.userService.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamService extends CoreService<Team, UUID> {
    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        super(teamRepository);
        this.teamRepository = teamRepository;
    }

    public Optional<Team> getByName(String name) {
        return teamRepository.findByName(name);
    }

    public Optional<Team> addMember(UUID teamId, User newMember) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        return teamOptional.map(team -> {
            if (!team.getMembers().contains(newMember))
                team.getMembers().add(newMember);
            return teamRepository.save(team);
        });
    }



    public Optional<Team> removeMember(UUID teamId, User removeUser) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        return teamOptional.map(team -> {
            if (team.getMembers().contains(removeUser))
                team.getMembers().remove(removeUser);
            return teamRepository.save(team);
        });
    }

    public Collection<Team> getUserTeams(User user){
        return teamRepository.findTeamsByMembers(user);
    }
}
