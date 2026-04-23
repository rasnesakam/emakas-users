package com.emakas.userService.config;

import com.emakas.userService.model.Resource;
import com.emakas.userService.shared.converters.StringToAccessModifierConverter;
import com.emakas.userService.shared.converters.StringToCodeChallengeMethodConverter;
import com.emakas.userService.shared.converters.StringToResourcePermissionConverter;
import com.emakas.userService.shared.enums.DefaultAppResourceKeys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebConfig {

    @Bean
    public StringToResourcePermissionConverter stringToResourcePermissionConverter() {
        return new StringToResourcePermissionConverter();
    }
    @Bean
    public StringToAccessModifierConverter stringToAccessModifierConverter(){
        return new StringToAccessModifierConverter();
    }

    @Bean
    public StringToCodeChallengeMethodConverter stringToCodeChallengeMethodConverter() {
        return new StringToCodeChallengeMethodConverter();
    }

    @Bean
    public Map<String, Resource> getDefaultResourcesMap(@Value("${app.domain}") String appDomainName) {
        Map<String, Resource> resourceMap = new HashMap<>();

        resourceMap.put(DefaultAppResourceKeys.RSC_TEAMS.toString(),
                new Resource("Teams", "The way of grouping people. All users in system must be in a group.", String.format("%s/teams",appDomainName), null));
        resourceMap.put(DefaultAppResourceKeys.RSC_MEMBERS.toString(),
                new Resource("Members", "The users that use resources of system.", String.format("%s/members",appDomainName), null));
        resourceMap.put(DefaultAppResourceKeys.RSC_TEAM_MEMBERS.toString(),
                new Resource("Team Members", "Registry of the group members.", String.format("%s/teamMembers",appDomainName), null));
        resourceMap.put(DefaultAppResourceKeys.RSC_APPS.toString(),
                new Resource("Applications", "The applications that uses resources of system.", String.format("%s/applications",appDomainName), null));
        resourceMap.put(DefaultAppResourceKeys.RSC_RESOURCES.toString(),
                new Resource("Resource", "Consumables by the users or applications.", String.format("%s/resources",appDomainName), null));
        resourceMap.put(DefaultAppResourceKeys.RSC_PERMISSIONS.toString(),
                new Resource("Permissions", "Permissions that will evaluate by app", String.format("%s/permissions",appDomainName), null));
        return resourceMap;
    }
}
