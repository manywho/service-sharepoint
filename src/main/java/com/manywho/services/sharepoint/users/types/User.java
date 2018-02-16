package com.manywho.services.sharepoint.users.types;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "User", summary = "Details about a User")
public class User {

    public final static String NAME = "User";

    @Type.Identifier
    @Type.Property(name = "ID", contentType = ContentType.String)
    private String id;

    @Type.Property(name = "Display Name", contentType = ContentType.String)
    private String displayName;

    @Type.Property(name = "Given Name", contentType = ContentType.String)
    private String givenName;

    @Type.Property(name = "Job Title", contentType = ContentType.String)
    private String jobTitle;

    @Type.Property(name = "Mail", contentType = ContentType.String)
    private String mail;

    @Type.Property(name = "Mobile Phone", contentType = ContentType.String)
    private String mobilePhone;

    @Type.Property(name = "Office Location", contentType = ContentType.String)
    private String officeLocation;

    @Type.Property(name = "Preferred Language", contentType = ContentType.String)
    private String preferredLanguage;

    @Type.Property(name = "Surname", contentType = ContentType.String)
    private String surname;

    @Type.Property(name = "User Principal Name", contentType = ContentType.String)
    private String userPrincipalName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }
}

