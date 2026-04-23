# emakas-users

 User authentication api made with love and Spring Framework in java
--------------------------------------------------------------------

`emakas-users` is an identity-authorization module that stores user datas, handles their authentications and authorizations according to [OAuth 2.0](https://datatracker.ietf.org/doc/html/rfc6749) specification.  
Also whith this module, you can restrict your users (members) by that which services (resources) they can use and how can they use it. In addition to this, you can create secrets for your applications that uses spesific resources.

## Table of Contents
- [Overview](#overview)
  - [Run The Application](#run-the-application)
  - [Claims Of Jwt](#claims-of-jwt)
- [Entity Schemas](#entity-schemas)
  - [Fields of Users](#fields-of-users)
  - [Fields of Teams](#fields-of-teams)
  - [Fields of Applications](#fields-of-applications)
  - [Fields of Resources](#fields-of-resources)
  - [Fields of Resource Permissions](#fields-of-resource-permissions)
- [Enum Schema](#enum-schema)
  - [Values of Permission Target Type](#values-of-permission-target-type)
  - [Values of Permission Scopes](#values-of-permission-scopes)
  - [Values of Access Modifiers](#values-of-access-modifiers)
- [Endpoint Documentation](#endpoint-documentation)
  - [Application](#application)
  - [Auth](#auth)
  - [Members](#members)
  - [OAuth](#oauth)
  - [Pages](#pages)
  - [Resource Permissions](#resource-permissions)
  - [Resources](#resources)
  - [Teams](#teams)
  - [User](#user)

## Overview

### Run The Application

Before run the application, you need to generate two files in order to configure the app
1. Create file named `application.yml` under `src/main/resources/` directory and fill it as follows:
   ```yaml
    app:
      domain: iam.emakas.net
    java-jwt:
      secret: YOUR SECRET HERE
      issuer: YOUR ISSUER HERE
      expiration: EXPIRATION TIMES IN SECONDS
    spring:
      datasource:
        url: jdbc:postgresql://DB_URL/DB_NAME
        username: DB_USERNAME
        password: DB_PASSWORD
      jpa:
        hibernate:
          ddl-auto: none
        properties:
          hibernate:
            dialect: org.hibernate.dialect.PostgreSQLDialect
   ```
2. Create file named `.env` inside root directory of project in order to configure database
    ```.dotenv
    DB_PASSWORD=DB_PASSWORD
    DB_NAME=DB_NAME
    DB_USER=DB_USER_NAME
    ```

3. You can create docker image with command:
    ```bash
   docker build -t emakas-users .
    ```
4. After creation, you can start

### Claims of Jwt

- iss: Issuer of this token
- sub: Unified Unique Id for user that requested
- exp: Timestamp of expiration date 
- aud: [Audience](https://datatracker.ietf.org/doc/html/rfc7519#section-4.1.3) claim that describes which domains will use this token

An example Jwt is as folows
```json
{
  "sub": "usr:2dc77664-03fc-4bf9-a46f-e97dd1d8c752",
  "aud": "emakas.net",
  "scope": [
    "global:read_write:iam.emakas.net/resources",
    "team:read:iam.emakas.net/members",
    "self:write:iam.emakas.net/teams"
  ],
  "iss": "emakas-user",
  "exp": 1748544968,
  "iat": 1748544668
}
```
## Entity Schemas

### Fields of Users

|    Field    |     Type      |                          Description                          |
|:-----------:|:-------------:|:-------------------------------------------------------------:|
|     id      |     UUID      | Unified Unique Identifier that describes user in the database |
|    uname    |    String     | A chosen unique username that describes user in the database  |
|    email    |    String     |                   Contact mail of the user                    |
|  password   | Bcrypt String |        Hashed pass code that using for authentication         |
|    name     |    String     |                   Name that describes user                    |
|   surname   |    String     |                  Surname that describes user                  |
| createdTime |   Date Time   |               The time when this record created               |
| updatedTime |   Date Time   |            The last time when this record updated             |


### Fields of Teams

|    Field    |               Type               |                          Description                          |
|:-----------:|:--------------------------------:|:-------------------------------------------------------------:|
|     id      |               UUID               | Unified Unique Identifier that describes team in the database |
|    name     |              String              |      A chosen name that represents team in the database       |
| description |              String              |            A brief description that describes team            |
|     uri     |              String              |          Unique name that is for internal operations          |
| parentTeam  |     [Team](#fields-of-teams)     |               Hierarchical parent of this team                |
|  subTeams   | List of [Team](#fields-of-teams) |              Hierarchical children of this team               |
|   members   | List of [User](#fields-of-users) |             Members of this team (Lead included)              |
|    lead     |     [User](#fields-of-users)     |                     The lead of the team                      |
| createdTime |            Date Time             |               The time when this record created               |
| updatedTime |            Date Time             |            The last time when this record updated             |


### Fields of Applications

|    Field    |   Type    |                         Description                          |
|:-----------:|:---------:|:------------------------------------------------------------:|
|     id      |   UUID    | Unified Unique Identifier that describes app in the database |
|    name     |  String   |      A chosen name that represents app in the database       |
| description |  String   |            A brief description that describes app            |
|     uri     |  String   |         Unique name that is for internal operations          |
| createdTime | Date Time |              The time when this record created               |
| updatedTime | Date Time |            The last time when this record updated            |


### Fields of Resources

|    Field    |   Type    |                            Description                            |
|:-----------:|:---------:|:-----------------------------------------------------------------:|
|     id      |   UUID    | Unified Unique Identifier that describes resource in the database |
|    name     |  String   |      A chosen name that represents resource in the database       |
| description |  String   |            A brief description that describes resource            |
|     uri     |  String   |            Unique name that is for internal operations            |
| createdTime | Date Time |                 The time when this record created                 |
| updatedTime | Date Time |              The last time when this record updated               |


### Fields of Resource Permissions

|        Field         |                           Type                            |                                           Description                                            |
|:--------------------:|:---------------------------------------------------------:|:------------------------------------------------------------------------------------------------:|
|          id          |                           UUID                            |                Unified Unique Identifier that describes resource in the database                 |
|       resource       |             [Resource](#fields-of-resources)              |                           A resource that is the subject of permission                           |
|         user         |                 [User](#fields-of-users)                  |                               The intended user of this permission                               |
|         team         |                 [Team](#fields-of-teams)                  |                               The intended team of this permission                               |
|     application      |          [Application](#fields-of-applications)           |                           The intended application of this permission                            |
| permissionTargetType | [PermissionTargetType](#values-of-permission-target-type) |          This defines whether the permission is for a user, a team, or an application.           |
|   permissionScope    |      [PermissionScope](#values-of-permission-scopes)      | This defines whether the permission will be used for the user themselves, their team, or global. |
|    accessModifier    |       [AccessModifier](#values-of-access-modifiers)       |               Defines whether the permission allows read, write, or both actions.                |
|     createdTime      |                         Date Time                         |                                The time when this record created                                 |
|     updatedTime      |                         Date Time                         |                              The last time when this record updated                              |

## Enum Schema

### Values of Permission Target Type

| Value |                                      Description                                      |
|:-----:|:-------------------------------------------------------------------------------------:|
| USER  |      Means that permission is intended for user. So you should operate with user      |
|  APP  | Means that permission is intended application. So you should operate with application |
| TEAM  |      Means that permission is intended for team. So you should operate with team      |


### Values of Permission Scopes

| Value  |                             Description                              |
|:------:|:--------------------------------------------------------------------:|
|  SELF  |    Grants access only to the user's own resources, not to others.    |
|  TEAM  |           Grants access to the resources of his / her team           |
| GLOBAL | Means that permission is intended for all. Kind of admin privileges  |

### Values of Access Modifiers

|   Value    |                                                   Description                                                    |
|:----------:|:----------------------------------------------------------------------------------------------------------------:|
|    READ    |            Grants access to only read operations. This allows users to retrieve data from a resource             |
|   WRITE    |         Grants access to only write operations. This allows users to create or delete data on a resource         |
| READ_WRITE | Grants access to both write and read operations. This allows users to operate full CRUD operations on a resource |

## Endpoint Documentation

### Application
Applications are way to consume service resources through non-user interactions.  
You may want to use your bots, apps or websites to consume your resources such as blog posts, commercial products and so on.  
To accomplish this securely, you need to register your apps, bots etc. to this service as **Application** and give them permissions
and restrictions to define which services they can use
#### Registering Applications

### Auth

#### Signing In
`POST` `/api/auth/sign-in`

This endpoint is using for signin users in. It should not be use by applications directly. It has XSS protection.
To sign users into your systems, you need to redirect users to [User Sign Page](#login-page)

### Members
### OAuth
### Pages
#### Login Page
`GET` `/page/auth/login?public_key=string&redirect=base64_string`

This is the static page that users can log in and redirect to their systems. You need to insert these query parameters
in order to use it for your external services. If you don't, user will redirect to dashboard page.

| Query Parameter |  Type  |                                  Description                                   |
|:---------------:|:------:|:------------------------------------------------------------------------------:|
|   public key    | string |          Public key is unique value for each services you registered           |
|    redirect     | string | The URL that user will be redirected to. It must encrypted with Base 64 format |


### Resource Permissions
### Resources
### Teams
### User


