# Gravity SDOH Exchange RI
Reference Implementation for the Gravity use cases specified at https://confluence.hl7.org/display/FHIR/2021-01+Gravity+SDOH+Clinical+Care+Track.
## Live Example
- The EHR app is running at https://sdoh-exchange-ri-ehr.herokuapp.com. To log in please request access to the Logica Sandbox at https://sandbox.logicahealth.org/GravitySandboxNew from the Track leads.
- The CP app is running at https://sdoh-exchange-ri-cp.herokuapp.com. To log in please request access to the Logica Sandbox at https://sandbox.logicahealth.org/GravitySDOHCBRO from the Track leads.

## Local Testing
### Configure
To configure both apps to use custom Sandbox URLs - set the following system variables:
Replace **{APP}** with either `ehr` or `cbro`.

| Variable | Description |
| ------ | ------ |
| **{APP}.fhir-server-uri** | Secured URI of the underlying FHIR server. Used for an authorized access for a specific user data (Patient or Practitioner). |
| **{APP}.open-fhir-server-uri** | Open URI of the underlying FHIR server. Used by the system to perform scheduled read/write operations in the background outside of a user context. Usually a 'Client Credentials' flow is used, but it is not supported by the Logica Sandbox. |
| **spring.security.oauth2.client.registration.{APP}-client.client-id** | OAauth2 Client ID. |
| **spring.security.oauth2.client.registration.{APP}-client.client-secret** | OAauth2 Client Secret. Leave empty for public clients. |
| **spring.security.oauth2.client.provider.{APP}-provider.token-uri** | OAuth2 Token endpoint. Defaults to the Logica Token ednpoint. |
| **spring.security.oauth2.client.provider.{APP}-provider.authorization-uri** | OAuth2 Authorization endpoint. Defaults to the Logica Authorization ednpoint. |
| **spring.security.oauth2.client.provider.{APP}-provider.user-info-uri** | OAuth2 UserInfo endpoint. Defaults to the Logica UserInfo ednpoint. |
| **spring.security.oauth2.client.provider.{APP}-provider.jwk-set-uri** | JSON Web Key Set endpoint. Defaults to the Logica JWKS ednpoint. |

### Build
Latest Java and Maven have to be installed. Just run a command from a base directory:
```sh
mvn clean install
```
### Run
```sh
java -Dserver.port=8080 -jar ehr/app/target/app-1.0-SNAPSHOT.jar
```
or
```sh
java -Dserver.port=8082 -jar cbro/app/target/app-1.0-SNAPSHOT.jar
```

Change port `8080` to any other port you want an application to listen to. Specify varaibles before the `-jar` flag in format `-Dvariable=value`. 

## Prepare a Sandbox
### Delete data from the previous runs
Run the following command to delete all data for a specific Resource type:
```sh
DELETE https://{server base}/Task?_profile:contains={SDOH Profile or no string to match all}&_cascade=delete
```
Reource types to delete:
- Task
- ServiceRequest
- Procedure
- Consent

### Create Organization resources with Endpoints
Send a `POST` request to `https://{server base}/` with the following content. This will create a CP Organization entry with a corresponding Endpoint. Please take into account the currently **only OPEN FHIR endpoints are supported**.
```yaml
{
    "resourceType": "Bundle",
    "id": "bundle-transaction",
    "type": "transaction",
    "entry": [
        {
            "fullUrl": "urn:uuid:61ebe359-bfdc-4613-8bf2-c5e300945f0a",
            "resource": {
                "resourceType": "Organization",
                "type": [
                    {
                        "coding": [
                            {
                                "system": "http://hl7.org/gravity/CodeSystem/sdohcc-temporary-organization-type-codes",
                                "code": "cp",
                                "display": "Coordination Platform"
                            }
                        ]
                    }
                ],
                "name": "Good Health Clinic",
                "endpoint": [
                    {
                        "reference": "urn:uuid:05efabf0-4be2-4561-91ce-51548425acb9"
                    }
                ]
            },
            "request": {
                "method": "POST",
                "url": "Organization"
            }
        },
        {
            "fullUrl": "urn:uuid:05efabf0-4be2-4561-91ce-51548425acb9",
            "resource": {
                "resourceType": "Endpoint",
                "status": "active",
                "connectionType": {
                    "system": "http://terminology.hl7.org/CodeSystem/endpoint-connection-type",
                    "code": "hl7-fhir-rest"
                },
                "name": "Health Intersections CarePlan Hub",
                "managingOrganization": {
                    "reference": "urn:uuid:61ebe359-bfdc-4613-8bf2-c5e300945f0a"
                },
                "contact": [
                    {
                        "system": "email",
                        "value": "endpointmanager@example.org",
                        "use": "work"
                    }
                ],
                "period": {
                    "start": "2014-09-01"
                },
                "address": "https://sandbox.logicahealth.org/GravitySDOHCBRO/open"
            },
            "request": {
                "method": "POST",
                "url": "Endpoint"
            }
        }
    ]
}
```
### Create Goals and Conditions
TODO
