package com.manywho.services.sharepoint.users;

import org.apache.olingo.client.api.domain.ClientEntity;

public class UserMapper {

    public User buildManyWhoUserObject(ClientEntity userEntity) {
        User user = new User();

        user.setId( userEntity.getProperty("id").getValue().toString());
        user.setDisplayName(userEntity.getProperty("displayName").getValue().toString());
        user.setGivenName(userEntity.getProperty("givenName").getValue().toString());
        user.setJobTitle(userEntity.getProperty("jobTitle").getValue().toString());
        user.setMail(userEntity.getProperty("mail").getValue().toString());
        user.setMobilePhone(userEntity.getProperty("mobilePhone").getValue().toString());
        user.setOfficeLocation(userEntity.getProperty("officeLocation").getValue().toString());
        user.setPreferredLanguage(userEntity.getProperty("preferredLanguage").getValue().toString());
        user.setSurname(userEntity.getProperty("surname").getValue().toString());
        user.setUserPrincipalName(userEntity.getProperty("userPrincipalName").getValue().toString());

        return user;
    }
}
