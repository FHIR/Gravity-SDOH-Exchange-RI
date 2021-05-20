# Gravity SDOH Exchange RI
Reference Implementation for the Gravity use cases specified at https://confluence.hl7.org/display/FHIR/2021-01+Gravity+SDOH+Clinical+Care+Track.
## Live Example
- The EHR app is running at https://sdoh-exchange-ri-ehr.herokuapp.com. To log in please request access to the Logica Sandbox at https://sandbox.logicahealth.org/GravitySandboxNew from the Track leads.
- The CP app is running at https://sdoh-exchange-ri-cp.herokuapp.com. To log in please request access to the Logica Sandbox at https://sandbox.logicahealth.org/GravitySDOHCBRO from the Track leads.

## Local Testing
### Configure
To configure both apps to use custom Sandbox URLs - set the following system variables:
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
java -Dserver.port=8082 -jar cp/app/target/app-1.0-SNAPSHOT.jar
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

### Create an EHR Organization resource with the PractitionerRole
Send a `POST` request to `https://{EHR server base}/` with the following content. This will create an EHR Organization entry and link it to the performing Practitioner using the PractitionerRole resource. This practtioner with corresponding PractitionerRole resource and Organization referencing the current EHR, should be launching the EHR app, in other case the launch will fail. Please take into account that **both resources should belong to the US-Core profile**.
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

### Create Organization resources with Endpoints
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
### Create Goals and Conditions
Goals and Conditios must be pre-created manually for now. In future they will be promoted from the Questionnaire results. All examples can be found at http://build.fhir.org/ig/HL7/fhir-sdoh-clinicalcare/artifacts.html#examples. To create a Condition resource for a specific Patient and Food Insecurity category - run the following POST request to `https://{EHR server base}/{Resource Type}`. Please notice, `the profile should be set to  "http://hl7.org/fhir/us/sdoh-clinicalcare/StructureDefinition/SDOHCC-Condition-Base-1"`:
```yaml
{
    "resourceType": "Condition",
    "meta": {
        "profile": [
            "http://hl7.org/fhir/us/sdoh-clinicalcare/StructureDefinition/SDOHCC-Condition-Base-1"
        ]
    },
    "clinicalStatus": {
        "coding": [
            {
                "system": "http://terminology.hl7.org/CodeSystem/condition-clinical",
                "code": "active",
                "display": "Active"
            }
        ]
    },
    "verificationStatus": {
        "coding": [
            {
                "system": "http://terminology.hl7.org/CodeSystem/condition-ver-status",
                "code": "unconfirmed",
                "display": "Unconfirmed"
            }
        ]
    },
    "category": [
        {
            "coding": [
                {
                    "system": "http://hl7.org/fhir/us/core/CodeSystem/condition-category",
                    "code": "problem-list-item",
                    "display": "Problem List Item"
                }
            ]
        },
        {
            "coding": [
                {
                    "system": "http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/sdohcc-temporary-codes",
                    "code": "food-insecurity",
                    "display": "Food Insecurity"
                }
            ]
        }
    ],
    "code": {
        "coding": [
            {
                "system": "http://snomed.info/sct",
                "code": "733423003",
                "display": "Food insecurity"
            }
        ]
    },
    "subject": {
        "reference": "Patient/smart-1288992"
    },
    "onsetPeriod": {
        "start": "2019-08-18T12:31:35.123Z"
    },
    "evidence": [
        {
            "detail": [
                {
                    "reference": "Observation/15349"
                }
            ]
        }
    ]
}
```

Goal resource example:
```yaml
{
    "resourceType": "Goal",
    "meta": {
        "profile": [
            "http://hl7.org/fhir/us/sdoh-clinicalcare/StructureDefinition/SDOHCC-Goal-Base-1"
        ]
    },
    "lifecycleStatus": "active",
    "achievementStatus": {
        "coding": [
            {
                "system": "http://terminology.hl7.org/CodeSystem/goal-achievement",
                "code": "improving",
                "display": "Improving"
            }
        ]
    },
    "category": [
        {
            "coding": [
                {
                    "system": "http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/sdohcc-temporary-codes",
                    "code": "food-insecurity",
                    "display": "Food Insecurity"
                }
            ]
        }
    ],
    "description": {
        "coding": [
            {
                "system": "http://snomed.info/sct",
                "code": "1078229009",
                "display": "Food security"
            }
        ]
    },
    "subject": {
        "reference": "Patient/smart-1288992"
    },
    "target": [
        {
            "measure": {
                "coding": [
                    {
                        "system": "http://loinc.org",
                        "code": "88124-3",
                        "display": "Food insecurity risk [HVS]"
                    }
                ]
            },
            "detailCodeableConcept": {
                "coding": [
                    {
                        "system": "http://loinc.org",
                        "code": "LA19983-8",
                        "display": "No risk"
                    }
                ]
            },
            "dueDate": "2021-08-01"
        }
    ],
    "statusDate": "2021-05-01",
    "addresses": [
        {
            "reference": "Condition/15350"
        }
    ]
}
```
