Cloud Foundry Java Client [SeaClouds] 
==================

![SeaClouds Project][SeaClouds-banner]
[SeaClouds-banner]: http://www.seaclouds-project.eu/img/header_test.jpg  "SeaClouds Project"

==================
This project has been developed as a proof of concept to check the CloudFoundry Java API within the  SeaClouds' context.

It is based on  the the [cf-java-client][1] project, which has been extending with a simple database management feature. The real goal of this work was:

 - Deploying an application in a CloudFoundry platform using the available PaaS services (Platform-as-a-Services).
 - Creating and binding a [database service][2].
 - Connecting the deployed application and the bound service using the [environment variables][3].

Usage
-------------------
It provides a [simple example][9] which describes how an application can be deployed:

> src/test/java/BrooklynChatTest.java

You need configure the application deployment specifying the Cloud Foundry PaaS target and the credentials. For example.

```
String target = "https://api.run.pivotal.io";
String user = "user@seaclouds.com";
String password = "pass";
String organization = "user";
String space = "development";
```

In the previous example the [Pivotal Web Services Platform][4] is used as the target[^PWS]. PWS is An instance of Pivotal Cloud Foundry which is hosted on AWS. You can [sing up][5] and the platform will be ready to be used.

Moreover, it is necessary configure the database services for hosting the expected database. The service creation needs a service type and the service name. Therefore, the service provides several plans which specifies the different usage agreement. Below, it shows a service configuration example that configure the [ClearDatabase][6] which provide a free database management using the plan **spark**.

```
String databaseServie = "cleardb";
String serviceName = "mycleardb";
String servicePlan ="spark";
boolean freePlan = true;
```

The explained example deploy the [Simple Web Application][6] application of [Brooklyn project][7]. The original application has been [adapted][8] 
 to be deployed in PWS using the environment variables to manage the database connections.

  [1]: https://github.com/cloudfoundry/cf-java-client
  [2]: http://docs.cloudfoundry.org/devguide/services/
  [3]: http://docs.cloudfoundry.org/devguide/deploy-apps/environment-variable.html
  [4]: http://www.pivotal.io/platform-as-a-service/pivotal-cloud-foundry
  [5]: http://run.pivotal.io/
  [6]: https://github.com/apache/incubator-brooklyn/tree/master/examples/webapps
  [7]: https://brooklyn.incubator.apache.org/
  [8]: https://github.com/kiuby88/Chat-Application-Pivotal-Example
  [9]: https://github.com/SeaCloudsEU/cloudfoundry-java-client/blob/master/src/test/java/BrooklynChatTest.java
