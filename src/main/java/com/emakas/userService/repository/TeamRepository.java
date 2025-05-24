package com.emakas.userService.repository;

import com.emakas.userService.model.Team;
import com.emakas.userService.model.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends CoreRepository<Team, UUID> {
    Collection<Team> findTeamsByMembers(User member);
    Optional<Team> findByName(String name);

}
