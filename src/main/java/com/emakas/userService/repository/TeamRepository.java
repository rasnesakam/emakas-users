package com.emakas.userService.repository;

import com.emakas.userService.model.Team;
import com.emakas.userService.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends CoreRepository<Team, UUID> {

    @Query("SELECT t FROM Team t WHERE :member IN elements(t.members)")
    Collection<Team> findTeamsByMembers(@Param("member") User member);

    Optional<Team> findByName(String name);

    Collection<Team> findByLeadId(UUID leadId);

    Optional<Team> findByUri(String uri);

}
