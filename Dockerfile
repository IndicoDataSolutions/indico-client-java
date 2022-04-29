FROM gradle:jdk8

RUN apt-get update && apt-get -y install gnupg2
WORKDIR /src
COPY . /src/
RUN chmod +x ./build.sh

