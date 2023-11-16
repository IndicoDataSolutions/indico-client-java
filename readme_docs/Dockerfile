FROM gradle:jdk8

WORKDIR /indico-client-java
COPY . /indico-client-java
RUN apt-get update && apt-get install jq -y
CMD ["sleep", "infinity"]