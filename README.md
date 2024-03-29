# indico-client-java

## Intro

This is the Java SDK used to access Indico Data's platform. As of `4.12.0`, the Java library has been rewritten and released using Kotlin as the base language. You can find out more about our decision [here](https://indicodata.ai/blog/announcing-kotlin/). *The minimum version of the JDK we support remains Java 8.* [See the release notes](https://github.com/IndicoDataSolutions/indico-client-java/releases/tag/4.12.0) for additional information on minor API changes in v4.12 on.

## Installation

### Gradle :
 You can use gradle or maven to reference the indico library. The latest version is on the releases page located at the right side of the main repository page.

 in build.gradle : 
```
    repositories {
        mavenCentral()
    }

    dependencies {
        implementation 'com.indico:indico-client-java:${indico-version}'
    }
```

### Maven :
 in pom.xml : 
```
<dependency>
  <groupId>com.indico</groupId>
  <artifactId>indico-client-java</artifactId>
  <version>${indico-version}</version>
</dependency>
```

## Authentication

The Indico Platform and Client Libraries use JSON Web Tokens (JWT) for user authentication. You can download a token 
from the account page in the [indico app](https://app.indico.io/auth/account) by clicking the large, blue “Download new API Token” 
button. Most browsers will download the API token as indico_api_token.txt and place it in your Downloads directory. You should move 
the token file from Downloads to either your home directory or another convenient location in your development environment. 

## Configuration

### IndicoConfig Class
The IndicoConfig class gives you the maximum control over the Java Client Library's configuration. Here’s how you might instantiate 
an IndicoConfig object and set the host and path to your API Token:
```
IndicoConfig config = new IndicoConfig.Builder()
                .host("app.indico.io")
                .tokenPath("/home/user/indico-api-token.txt")
                .build();
```
You configure the IndicoConfig class via an embedded Builder that has methods to set the various config parameters such as 
host, tokenPath, connection timeout, etc.

### API Client

The Indico Platform uses GraphQL to communicate with ALL clients including the company’s own web application and also the 
Indico Java Client. You’ll use an IndicoClient object to pass GraphQL queries to the Indico Platform. Here’s a simple way 
to create a client. The `IndicoClient` class is an interface, with a concrete implementation called `IndicoKtorClient`:
```
IndicoClient client = new IndicoKtorClient(config);
```
If you want to learn more about GraphQL, the [How to GraphQL](https://www.howtographql.com/) tutorial is a great place to start.

## Indico GraphQL Schema

The Indico Platform ships with a built-in sandbox environment that both documents and allows you to interactively explore 
the Platform’s GraphQL schema. You can find the sandbox at `/graph/api/graphql` on your Indico Platform installation. If your 
Platform’s host is `indico.my_company.com` then the full sandbox URL would be `https://indico.my_company.com/graph/api/graphql`. 

## Pre-Built GraphQL Queries

GraphQL is extremely powerful, flexible and efficient but can be a bit verbose. To make things easier for day-to-day use of the 
Platform and Client Library, the developers at Indico created a collection of Java Classes to generate the most often used 
queries for you. 

## Examples

Several examples are provided in this repo under the `./examples/` folder:
* **SubmissionExample** - An example of executing a submission to a workflow.
* **SQSExample** - An example of fetching submission status using Amazon's SQS service.
* **GetModelTrainingProgress** - Get % complete progress on a model that is training.

The examples can be run by putting them in an appropriate java project and referencing the Indico library.

## Example Snippets

### Create a Client
```
IndicoConfig config = new IndicoConfig.Builder()
                .host("app.indico.io")
                .tokenPath("/home/user/indico-api-token.txt")
                .build();
IndicoClient client = new IndicoKtorClient(config);
```
