package com.tarento.upsmf.userManagement.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.tarento.upsmf.userManagement.config.KeycloakConfig;
import com.tarento.upsmf.userManagement.model.KeyCloakUserDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
public class KeyCloakUsers {

    @Autowired
    private KeycloakConfig keycloakConfig;


    public javax.ws.rs.core.Response createUser(final KeyCloakUserDTO keyCloakUserDTO){
        Keycloak keycloakInstance = keycloakConfig.getKeycloakInstance();
        UserRepresentation newUser = createUserRepresentation(keyCloakUserDTO);
        UsersResource usersResource = keycloakInstance.realm("sunbird-rc").users();
        return usersResource.create(newUser);
    }

    public void updateUser(final KeyCloakUserDTO keyCloakUserDTO)  {
        Keycloak keycloakInstance = keycloakConfig.getKeycloakInstance();
        UserResource userResource = keycloakInstance.realm("sunbird-rc").users().get(keyCloakUserDTO.getUsername());
        UserRepresentation user = userResource.toRepresentation();
        /*
        * update user information here
        * */
        userResource.update(user);
    }

    public List<UserRepresentation> listUser() {
        Keycloak keycloakInstance = keycloakConfig.getKeycloakInstance();
        return keycloakInstance.realm("sunbird-rc").users().list();
    }

    public void activateUser(final String userName){
        Keycloak keycloakInstance = keycloakConfig.getKeycloakInstance();
        UserResource userResource = keycloakInstance.realm("sunbird-rc").users().get(userName);
        UserRepresentation user = userResource.toRepresentation();
        user.setEnabled(true);
    }

    public void deactivateUser(final String userName) {
        Keycloak keycloakInstance = keycloakConfig.getKeycloakInstance();
        UserResource userResource = keycloakInstance.realm("sunbird-rc").users().get(userName);
        UserRepresentation user = userResource.toRepresentation();
        user.setEnabled(false);
    }

    public UserRepresentation createUserRepresentation(final KeyCloakUserDTO keyCloakUserDTO) {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setEnabled(true);
        newUser.setEmail(keyCloakUserDTO.getEmail());
        newUser.setEmailVerified(true);
        newUser.setUsername(keyCloakUserDTO.getEmail());
        newUser.setFirstName(keyCloakUserDTO.getName());
        newUser.setLastName(keyCloakUserDTO.getLastName());
        List<String> roles = new ArrayList<>();
        roles.add(keyCloakUserDTO.getRole());
        newUser.setRealmRoles(roles);
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(keyCloakUserDTO.getPassword());
        credentialRepresentation.setType("password");
        newUser.setCredentials(Collections.singletonList(credentialRepresentation));
        return newUser;
    }

}
