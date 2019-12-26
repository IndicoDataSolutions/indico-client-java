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
    try (Indico indico = new Indico([IndicoConfig config])) {

        // Get Model Group
        ModelGroup modelGroup = indico.ModelGroup(int groupId);
        // Load Model
        String status = modelGroup.load([int id]);
        // Model Info
        JSONObject modelInfo = modelGroup.info();
        // Selected Model
        JSONObject selectedModel = modelGroup.getSelectedModel();
        // Predict Data
        Job job = modelGroup.predict(List data,[int id]);
        job.await();
        JSONArray jobResult = job.result();
        // or use sync
        JSONArray jobResult = modelGroup.predict(List data,[int id]).sync();

        // For Pdf Extraction
        Job job = indico.pdfExtraction(List data, [PdfExtractionOptions options]);
        job.await();
        JSONArray jobResult = job.result();
        // or use sync
        JSONArray jobResult = indico.pdfExtraction(List data, [PdfExtractionOptions options]).sync();

    }
```

