# emakas-users
## User authentication api made with love and Spring Framework in java
User authentication service simply operates user actions such as authentication and authorization
It produces Json Web Tokens for successfull operations. 

Run the Application
-------------------
Before run the application, you need to generate two files in order to configure the app
1. Create file named `application.yml` under `src/main/resources/` directory and fill it as follows:
    ```yaml
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

JWT has this claims
-------------------
- iss: Issuer of this token
- sub: Unified Unique Id for user that requested
- exp: Timestamp of expiration date 
- aud: [Audience](https://datatracker.ietf.org/doc/html/rfc7519#section-4.1.3) claim that describes which domains will use this token

Simple user has these informations in the database
--------------------------------------------------
- id:						Unified Unique Identifier that describes user in the database
- uname:				A choosen unique username that describes user in the database
- email:				Contact mail of the user
- password:			Hashed pass code that using for authentication
- name:					Name that describes user
- surname:			Surname that describes user
- createdTime:	The time when user created
- updatedTime:	The last time when datas of the user updated


Endpoints and responses
=======================
## Users
### `POST:` `/users/sign-up` Register new User
#### Body:
```json
{
	"uname": "string",
	"password": "string",
	"eMail": "string",
	"name": "string",
	"surname": "string"
}
```
#### Response
| Response code |          Description          |
|:-------------:|:-----------------------------:|
|      201      |   User created successfully   |
|      400      | Wrong parameters in post body |

----

### `DELETE:` `/users/delete/` Delete a user
This request does not need a body
Header param:
- Authorization: Bearer jwt token that contains id of the user to be deleted

#### Response
| Response code |         Description         |
|:-------------:|:---------------------------:|
|      200      |  User deleted successfully  |
|      400      | Wrong parameters in request |
|      404      |   User cannot be founded    |

---


