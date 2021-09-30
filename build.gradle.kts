import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer

plugins {
    id "java"
    id 'org.jetbrains.kotlin.jvm' version '1.5.10'
    id "signing"
    id "maven-publish"
    id "io.github.gradle-nexus.publish-plugin" version "1.0.0"
    id 'com.expediagroup.graphql' version '5.0.0-alpha.3'

}

group 'com.indico'

repositories {
    mavenCentral()
}

archivesBaseName = "indico-client-java"
version = "4.12.0"


task sourceJar(type: Jar) {
    classifier "sources"
    from sourceSets.main.allJava
}

task javadocJar(type: Jar, dependsOn: javadoc) {
    classifier "javadoc"
    from javadoc.destinationDir
}

publishing {
    publications {
        maven(MavenPublication) {
            from(components.java)
        }
    }
}


dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib"
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.7.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.7.0'
    implementation("io.ktor:ktor-client-okhttp:1.6.1")
    testImplementation('org.junit.jupiter:junit-jupiter:5.5.2')
    implementation("com.expediagroup:graphql-kotlin-ktor-client:5.0.0-alpha.3")
    implementation("com.expediagroup", "graphql-kotlin-ktor-client", "5.0.0-alphs.4") {
        exclude("com.expediagroup", "graphql-kotlin-client-serialization")    }
    implementation("com.expediagroup:graphql-kotlin-client-jackson:5.0.0-alpha.3")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation('org.apache.logging.log4j:log4j-1.2-api:2.14.1')
    api('org.json:json:20190722')
    compileOnly("org.jetbrains:annotations:13.0")
    testCompileOnly("org.jetbrains:annotations:13.0")
}


//use with update-schema.sh to refresh graphql schema
graphqlIntrospectSchema {
    outputFile = file("${project.projectDir}/src/main/graphql/schema.graphql")
    endpoint = "https://app.indico.io/graph/api/graphql"
    headers = ["Authorization" : "Bearer " + project.getProperties().getOrDefault('graphQlToken', "")]
}

graphqlGenerateClient {
    // Boolean flag indicating whether or not selection of deprecated fields is allowed.
    allowDeprecatedFields = true
    packageName = "com.indico.graphql"
    // Custom directory containing query files, defaults to src/main/resources
    queryFileDirectory = "${project.projectDir}/src/main/graphql/queries"
    // GraphQL schema file. Can be used instead of `endpoint` or `sdlEndpoint`.
    schemaFile = file("${project.projectDir}/src/main/graphql/schema.graphql")
    serializer = GraphQLSerializer.JACKSON
    useOptionalInputWrapper = true

}


// ossrh requires javadoc and sources https://central.sonatype.org/pages/requirements.html
java {
    withJavadocJar()
    withSourcesJar()
}

def hasSigningKey = project.hasProperty("signingKeyId") || project.hasProperty("signingKey")

if(hasSigningKey) {
    sign(project)
}
void sign(Project project) {
    project.signing {
        required { project.gradle.taskGraph.hasTask("publish") }
        def signingKeyId = project.findProperty("signingKeyId")
        def signingKey = project.findProperty("signingKey")
        def signingPassword = project.findProperty("signingPassword")
        if (signingKeyId) {
            useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        } else if (signingKey) {
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
        sign publishing.publications.maven
    }
}

project.plugins.withType(MavenPublishPlugin).all {
    PublishingExtension publishing = project.extensions.getByType(PublishingExtension)
    publishing.publications.withType(MavenPublication).all { mavenPublication ->
        mavenPublication.pom {
            name = "${project.group}:${project.name}"
            description = name
            url = "https://github.com/indico/indico-client-java"
            licenses {
                license {
                    name = "MIT"
                    url = "https://github.com/IndicoDataSolutions/indico-client-java/blob/master/LICENSE"
                }
            }
            developers {
                developer {
                    id = "indico"
                    name = "Indico Engineering"
                    email = "contact@indico.io"
                }
            }
            scm {
                connection = "scm:git:https://github.com/indico/indico-client-java.git"
                developerConnection = "scm:git:ssh://github.com/indico/indico-client-java.git"
                url = "https://github.com/indico/indico-client-java"
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
    connectTimeout = Duration.ofMinutes(3)
    clientTimeout = Duration.ofMinutes(3)
}


publishing {
    repositories {
        maven {
            name = "local"
            def releasesRepoUrl = "$buildDir/repos/releases"
            def snapshotsRepoUrl = "$buildDir/repos/snapshots"
            url = version.endsWith("SNAPSHOT") ? snapshotsRepoUrl : releasesRepoUrl
        }
    }
}