# indico-client-java

### How To Install

#### Gradle :
 in build.gradle : 
```
    repositories {
        jcenter()
    }

    dependencies {
        implementation 'com.indico:indico-client:1.0-SNAPSHOT'
    }
```

#### Maven :
 in pom.xml : 
```
    <dependencies>
        <dependency>
            <groupId>com.indico</groupId>
            <artifactId>indico-client</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <repositories>
        <repository>
            <id>jcenter</id>
            <url>https://jcenter.bintray.com/</url>
        </repository>
    </repositories>
```


### How To Use

```
    // Using try with resources`
    try (IndicoClient indico = new IndicoClient([IndicoConfig config])) {

        // Get Model Group
        ModelGroup mg = indico.modelGroupQuery()
                              .id(int id)
                              .query();
        // Load Model
        String status = indico.modelGroupLoad()
                              .modelGroup(mg)
                              .execute();
        // To Upload Files
        JSONArray metadata = indico.uploadFile()
                                   .filePaths(List<String>)
                                   .call();
        // Predict Data
        Job job = indico.modelGroupPredict()
                        .modelGroup(mg)
                        .data(List<String>)
                        .execute();
        while(job.status() == JobStatus.PENDING) {
            Thread.sleep(1000);
        }
        JSONArray jobResult = job.results();

        // To Extract Documents
        List<Job> jobs = indico.documentExtraction()
                               .files(List<String>)
                               .jsonConfig(JSONObject)
                               .execute();

        // To Fetch Result From Storage
        Blob blob = indico.retrieveBlob()
                          .url(string)
                          .execute();
    }
```