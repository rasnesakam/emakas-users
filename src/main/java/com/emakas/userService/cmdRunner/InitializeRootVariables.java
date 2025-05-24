package com.emakas.userService.cmdRunner;

import com.emakas.userService.model.Resource;
import com.emakas.userService.model.ResourcePermission;
import com.emakas.userService.model.Team;
import com.emakas.userService.model.User;
import com.emakas.userService.service.ResourcePermissionService;
import com.emakas.userService.service.ResourceService;
import com.emakas.userService.service.TeamService;
import com.emakas.userService.service.UserService;
import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.PermissionScope;
import com.emakas.userService.shared.enums.PermissionTargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class InitializeRootVariables implements CommandLineRunner {
    private final UserService userService;
    private final TeamService teamService;
    private final ResourcePermissionService resourcePermissionService;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(InitializeRootVariables.class);
    private final ResourceService resourceService;

    @Autowired
    public InitializeRootVariables(UserService userService, TeamService teamService, ResourcePermissionService resourcePermissionService, PasswordEncoder passwordEncoder, ResourceService resourceService) {
        this.userService = userService;
        this.teamService = teamService;
        this.resourcePermissionService = resourcePermissionService;
        this.passwordEncoder = passwordEncoder;
        this.resourceService = resourceService;
    }

    public String getRandomPasswordText(){
        String alphabet = "abcdefghijklmnoprsqtuvwxyz";
        String numbers = "0123456789";
        String upperCase = alphabet.toUpperCase();
        String specialChars = "!^+%&()=?*";
        String stringPool = alphabet + numbers + upperCase + specialChars;
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 16; i++){
            password.append(stringPool.charAt((int) (Math.random() * stringPool.length())));
        }
        return password.toString();
    }

    public User createAdminUserIfNotExists(){
        User user = userService.getByUserName("admin");
        if (user == null) {
            User adminUser = new User();
            adminUser.setUserName("admin");
            String password = getRandomPasswordText();
            adminUser.setPassword(passwordEncoder.encode(password));
            adminUser = userService.save(adminUser);
            logger.info("Created admin user with username: {}", adminUser.getUserName());
            logger.info("Created admin user with password: {}", password);
            return adminUser;
        }
        System.out.println("Admin user already exists");
        return user;
    }

    public Team createCoreTeamIfNotExists(User lead){
        Optional<Team> coreTeam = teamService.getByName("Core Team");
        if (coreTeam.isEmpty()){
            Team team = new Team();
            team.setName("Core Team");
            team.setLead(lead);
            return teamService.save(team);
        }
        return coreTeam.get();
    }
    public Collection<Resource> createDefaultResources(){
        Stream<Resource> resources = Stream.of(
                new Resource("Teams", "", "users/teams"),
                new Resource("Members", "", "users/members"),
                new Resource("Team Members", "", "users/teamMembers"),
                new Resource("Applications", "", "users/applications"),
                new Resource("Resource", "", "users/resources")
        );
        resources = resources.map(resourceService::save);
        return resources.collect(Collectors.toSet());
    }
    public void assignAdminPermissions(User user, Collection<Resource> resources){
        resources.stream().forEach(resource -> {
            ResourcePermission resourcePermission = new ResourcePermission();
            resourcePermission.setUser(user);
            resourcePermission.setPermissionTargetType(PermissionTargetType.USER);
            resourcePermission.setResource(resource);
            resourcePermission.setPermissionScope(PermissionScope.ALL);
            resourcePermission.setAccessModifier(AccessModifier.READ_WRITE);
            logger.info(String.format("Assigning Role '%s' to user '%s'",resource.getUri(), user.getUserName()));
            resourcePermissionService.save(resourcePermission);
        });
    }
    public void assignAdminPermissionsToTeam(Team team, Collection<Resource> resources){
        resources.parallelStream().forEach(resource -> {
            ResourcePermission resourcePermission = new ResourcePermission();
            resourcePermission.setTeam(team);
            resourcePermission.setPermissionTargetType(PermissionTargetType.TEAM);
            resourcePermission.setResource(resource);
            resourcePermission.setPermissionScope(PermissionScope.ALL);
            resourcePermission.setAccessModifier(AccessModifier.READ_WRITE);
            resourcePermissionService.save(resourcePermission);
        });
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Creating Admin User");
        User admin = createAdminUserIfNotExists();
        logger.info("Creating Core Team");
        Team coreTeam = createCoreTeamIfNotExists(admin);
        logger.info("Creating Default Resources");
        Collection<Resource> appResources = createDefaultResources();
        logger.info("Assigning roles to admin user");
        assignAdminPermissions(admin,appResources);
    }
}
