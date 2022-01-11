# Gravity SDOH Exchange RI
Reference Implementation for the Gravity use cases specified at https://confluence.hl7.org/display/FHIR/2021-01+Gravity+SDOH+Clinical+Care+Track.
Current supported IG version is 1.1.0 (STU 2 ballot): http://hl7.org/fhir/us/sdoh-clinicalcare/2022Jan/
## Live Example
All reference implementations are usually available for testing during connectathons. There is a high chance at any other time that the apps have been undeployed to reduce costs. If so - try to deploy them locally.
- The EHR app is running at https://sdoh-exchange-ri-ehr.herokuapp.com. To log in please request access to the Logica Sandbox at https://sandbox.logicahealth.org/GravitySandboxNew from the Track leads.
- The CP app is running at https://sdoh-exchange-ri-cp.herokuapp.com. To log in please request access to the Logica Sandbox at https://sandbox.logicahealth.org/GravitySDOHCBRO from the Track leads.
- The CBO app is running at https://sdoh-exchange-ri-cbo.herokuapp.com. Creentials: user/user.

## Local Testing
### Configure
To configure the EHR and CP apps to use custom Sandbox URLs - set the following system variables:
Replace **{APP}** with either `ehr` or `cp`.

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

In CBO app the source FHIR Servers are added dynamically through UI, so no any configuration is needed on start.
### Build
Latest Java and Maven have to be installed. Just run a command from a base directory:
```sh
mvn clean install
```
### Run
```sh
java -Dserver.port=8080 -jar ehr/app/target/app-1.0-SNAPSHOT.jar
```
```sh
java -Dserver.port=8082 -jar cp/app/target/app-1.0-SNAPSHOT.jar
```
```sh
#The -Dapp.url parameter value should match the identifier of the CBO organization resource stored at CP FHIR server.
java -Dserver.port=8084 -Dapp.url=http://localhost:8084 -jar cbo/app/target/app-1.0-SNAPSHOT.jar
```

Change port `8080` to any other port you want an application to listen to. Specify varaibles before the `-jar` flag in format `-Dvariable=value`.
EHR run command example:
```sh
java -Dserver.port=8080 -Dehr.fhir-server-uri=https://api.logicahealth.org/GravitySandboxNew/data -Dehr.open-fhir-server-uri=https://api.logicahealth.org/GravitySandboxNew/open -Dspring.security.oauth2.client.registration.ehr-client.client-id=1c4d149f-9995-4c5c-ac42-018150437355 -Dspring.security.oauth2.client.registration.ehr-client.client-secret=secret -jar ehr/app/target/app-1.0-SNAPSHOT.jar
```

## Prepare a Sandbox
## Delete data from the previous runs
This section is applicable for both EHR and CP apps.
Run the following command to delete all data for a specific Resource type belonging to your test Patient resource:
```sh
DELETE https://{server base}/Task?patient={patient id}&_profile:contains={SDOH Profile or no string to match all}&_cascade=delete
```
All SDOH profiles can be found in the official SDOH IG documetnation at http://build.fhir.org/ig/HL7/fhir-sdoh-clinicalcare

Recource types to delete:
- Task (this includes both Referrals and Patient Tasks)
- ServiceRequest
- Procedure
- Consent
- Condition
- Goal
- QuestionnaireResponse

Optional (if these resources have been updated in the IG artifacts page):
- Hunger Vital Sign Questionnaire
- Hunger Vital Sign StructureMap

## Initialize EHR Sandbox

### Create an EHR Organization resource with the PractitionerRole
Send a `POST` request to `https://{EHR server base}/` with the following content. This will create an EHR Organization entry and link it to the performing Practitioner using the PractitionerRole resource. This practitioner with corresponding PractitionerRole resource and Organization referencing the current EHR, should be launching the EHR app, in other case the launch will fail. Please take into account that **both resources should belong to the US-Core profile**.
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
                "meta": {
                    "profile": [
                        "http://hl7.org/fhir/us/core/StructureDefinition/us-core-organization"
                    ]
                },
                "active": true,
                "name": "Good EHR"
            },
            "request": {
                "method": "POST",
                "url": "Organization"
            }
        },
        {
            "fullUrl": "urn:uuid:05efabf0-4be2-4561-91ce-51548425acb9",
            "resource": {
                "resourceType": "PractitionerRole",
                "id": "14604",
                "meta": {
                    "profile": [
                        "http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitionerrole"
                    ]
                },
                "active": true,
                "practitioner": {
                    "reference": "Practitioner/smart-Practitioner-71482713",
                    "type": "Practitioner",
                    "display": "Susan Clark"
                },
                "organization": {
                    "reference": "urn:uuid:61ebe359-bfdc-4613-8bf2-c5e300945f0a",
                    "display": "Good EHR"
                },
                "code": [
                    {
                        "coding": [
                            {
                                "system": "http://terminology.hl7.org/CodeSystem/practitioner-role",
                                "code": "doctor"
                            }
                        ]
                    }
                ],
                "specialty": [
                    {
                        "coding": [
                            {
                                "system": "http://snomed.info/sct",
                                "code": "408443003",
                                "display": "General medical practice"
                            }
                        ]
                    }
                ],
                "availableTime": [
                    {
                        "daysOfWeek": [
                            "mon",
                            "tue",
                            "wed"
                        ],
                        "availableStartTime": "09:00:00",
                        "availableEndTime": "16:30:00"
                    },
                    {
                        "daysOfWeek": [
                            "thu",
                            "fri"
                        ],
                        "availableStartTime": "09:00:00",
                        "availableEndTime": "12:00:00"
                    }
                ],
                "notAvailable": [
                    {
                        "description": "Susan will be on extended leave during May 2021",
                        "during": {
                            "start": "2021-05-01",
                            "end": "2021-05-20"
                        }
                    }
                ],
                "availabilityExceptions": "Susan is generally unavailable on public holidays and during the Christmas/New Year break"
            },
            "request": {
                "method": "POST",
                "url": "PractitionerRole"
            }
        }
    ]
}
```

### Create CP Organization resources with Endpoints
Send a `POST` request to `https://{EHR server base}/` with the following content. This will create a CP Organization entry with a corresponding Endpoint. Please take into account that currently **only OPEN FHIR endpoints are supported**.
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
                "address": "https://api.logicahealth.org/GravitySDOHCBRO/open"
            },
            "request": {
                "method": "POST",
                "url": "Endpoint"
            }
        }
    ]
}
```
### Create Health Concerns
Health Concern Observation resources are expected to be generated by the Hunger Vital Sign StructureMap from a QuestionnaireResponse resource. This step is automated and available as a REASTful API call:
```sh
curl --location --request POST 'https://sdoh-exchange-ri-ehr.herokuapp.com/administration/$extract' \
--header 'Content-Type: application/json' \
--data-raw '{QuestionnaireResponse resource}'
```
All examples can be found at http://build.fhir.org/ig/HL7/fhir-sdoh-clinicalcare/artifacts.html#examples.
A Health Concern should refererence a QuestionnaireResponse resource it is derived from. And a QuestionnaireResponse resource will contain a reference to Group Observation resource tree (find examples in the IG Artifacts page).

## Initialize CP Sandbox
### Create a CP Organization resource with the PractitionerRole
Instuctions are the same as for the [EHR](https://github.com/FHIR/Gravity-SDOH-Exchange-RI/blob/main/README.md#create-an-ehr-organization-resource-with-the-practitionerrole). Just put the proper CP organization name, and set type to:
```yaml
"type": [
    {
        "coding": [
            {
                "code": "cp",
                "display": "Community Platform",
                "system": "http://hl7.org/gravity/CodeSystem/sdohcc-temporary-organization-type-codes"
            }
        ]
    }
]
```

### Create CBO Organization resources with Endpoints
Send a `POST` request to `https://{CP server base}/Organization` with the following content. This will create a CBO Organization entry with a corresponding name and identifier.

> :warning: **The identifier should match the -Dapp.url parameter specified at the CBO application start.**
```yaml
{
    "id": "16180",
    "identifier": [
        {
            "system": "urn:ietf:rfc:3986",
            "value": "https://sdoh-exchange-ri-cbo.herokuapp.com"
        }
    ],
    "meta": {
        "lastUpdated": "2021-12-02T13:45:18.000+00:00",
        "profile": [
            "http://hl7.org/fhir/us/core/StructureDefinition/us-core-organization"
        ],
        "source": "#mmPINEJ9JJ0CRPPV",
        "versionId": "1"
    },
    "name": "Social Alliance",
    "resourceType": "Organization",
    "type": [
        {
            "coding": [
                {
                    "code": "cbo",
                    "display": "Community Based Organization",
                    "system": "http://hl7.org/gravity/CodeSystem/sdohcc-temporary-organization-type-codes"
                }
            ]
        }
    ]
}
```
