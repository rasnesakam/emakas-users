# emakas-users
## User authentication api made with love and Spring Framework in java
User authentication service simply operates user actions such as authentication and authorization
It produces Json Web Tokens for successfull operations. 

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
|Response code|          Description          |
|:-----------:|:-----------------------------:|
|     201     |   User created successfully   |
|     400     | Wrong parameters in post body |

----

### `DELETE:` `/users/delete/` Delete a user
This request does not need a body
Header param:
- Authorization: Bearer jwt token that contains id of the user to be deleted

#### Response
|Response code|          Description          |
|:-----------:|:-----------------------------:|
|     200     |   User deleted successfully   |
|     400     | Wrong parameters in request   |
|     404     |     User cannot be founded    |

---


