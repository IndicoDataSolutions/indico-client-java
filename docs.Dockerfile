FROM gradle:jdk8

WORKDIR /src
COPY . /src/
RUN ./gradlew dokkaGfm