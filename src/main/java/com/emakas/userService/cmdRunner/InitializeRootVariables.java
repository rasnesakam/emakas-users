package com.emakas.userService.cmdRunner;

import com.emakas.userService.model.*;
import com.emakas.userService.service.*;
import com.emakas.userService.shared.StringUtils;
import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.PermissionScope;
import com.emakas.userService.shared.enums.PermissionTargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
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
    private final String appDomainName;
    private final String appRedirectUri;
    private final Map<String, Resource> resourceMap;
    private final ApplicationService applicationService;

    @Autowired
    public InitializeRootVariables(
            UserService userService,
            TeamService teamService,
            ResourcePermissionService resourcePermissionService,
            PasswordEncoder passwordEncoder,
            ResourceService resourceService,
            @Value("${app.domain}") String appDomainName,
            @Value("${app.redirect_uri}") String appRedirectUri,
            Map<String, Resource> resourceMap,
            ApplicationService applicationService) {
        this.userService = userService;
        this.teamService = teamService;
        this.resourcePermissionService = resourcePermissionService;
        this.passwordEncoder = passwordEncoder;
        this.resourceService = resourceService;
        this.appDomainName = appDomainName;
        this.resourceMap = resourceMap;
        this.applicationService = applicationService;
        this.appRedirectUri = appRedirectUri;
    }

    public User createAdminUserIfNotExists(){
        Optional<User> user = userService.getByUserName("admin");
        if (user.isEmpty()) {
            User adminUser = new User();
            adminUser.setUserName("admin");
            adminUser.setName("Admin");
            adminUser.setSurname("User");
            // String password = StringUtils.getRandomString(16);
            String password = "1234";
            adminUser.setPassword(passwordEncoder.encode(password));
            adminUser = userService.save(adminUser);
            logger.info("Created admin user with username: {}", adminUser.getUserName());
            logger.info("Created admin user with password: {}", password);
            return adminUser;
        }
        System.out.println("Admin user already exists");
        return user.get();
    }

    public Application createFirstPartyAppIfNotExists() {
        Optional<Application> firstPartyApp = applicationService.getByUri(this.appDomainName);
        if (firstPartyApp.isEmpty()) {
            Application app = new Application();
            app.setName("First Party Application");
            app.setUri(this.appDomainName);
            app.setRedirectUri(this.appRedirectUri);
            app.setDescription("First party application. Manage account settings trough this application");
            app = applicationService.save(app);
            logger.info("Created first party application with client id: {}", app.getId());
            return app;
        }
        return firstPartyApp.get();
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
        Stream<Resource> resources = resourceMap.values().stream();
        resources = resources.map(resourceService::save);
        return resources.collect(Collectors.toSet());
    }
    public void assignAdminPermissions(User user, Collection<Resource> resources){
        resources.forEach(resource -> {
            ResourcePermission resourcePermission = new ResourcePermission();
            resourcePermission.setUser(user);
            resourcePermission.setPermissionTargetType(PermissionTargetType.USER);
            resourcePermission.setResource(resource);
            resourcePermission.setPermissionScope(PermissionScope.GLOBAL);
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
            resourcePermission.setPermissionScope(PermissionScope.GLOBAL);
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
        logger.info("Creating first party App");
        Application app = createFirstPartyAppIfNotExists();
        logger.info("Assigning roles to admin user");
        assignAdminPermissions(admin,appResources);
    }
}
