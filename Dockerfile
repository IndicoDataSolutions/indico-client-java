FROM gradle:jdk8

WORKDIR /src
COPY . /src/
RUN chmod +x ./build.sh

