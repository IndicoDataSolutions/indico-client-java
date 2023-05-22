FROM gradle:jdk8


ENV INDICO_HOST="dev-ci.us-east-2.indico-dev.indico.io"
ENV TEST_WORKFLOW_ID=0
ENV TEST_DATASET_ID=0
ENV TEST_MODEL_ID=0

RUN apt-get update && apt-get -y install gnupg2
WORKDIR /src
COPY . /src/
RUN chmod +x ./build.sh

