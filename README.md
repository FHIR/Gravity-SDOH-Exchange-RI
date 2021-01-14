# Gravity SDOH Exchange RI
Reference Implementation for the Gravity use cases specified at https://confluence.hl7.org/display/FHIR/2021-01+Gravity+SDOH+Clinical+Care+Track.
## Live Example
Currently only an EHR App API is implemented. The app is running at https://sdoh-exchange-ri-ehr.herokuapp.com.

Since it is bound to a Logica Sandbox at https://sandbox.logicahealth.org/GravitySandboxNew - to log in please request access to this sandbox first from the Track leads.
## Local Testing
### Configure
Go to `ehr-app/src/main/resources/application.yaml` and update Sandbox URL, security details and other settings. Description of every setting will be present soon.
### Build
Latest Java and Maven have to be installed. Just run a command from a base directory:
```sh
mvn clean install
```
### Run
```sh
java -Dserver.port=8080 -jar ehr-app/target/ehr-app-1.0-SNAPSHOT.jar
```

Change port `8080` to any other port you want an application to listen to.
