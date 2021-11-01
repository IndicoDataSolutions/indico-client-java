#fetch and update the schema
#snap install jq
#must have gradle 7 or above to run
REFRESH_TOKEN=$(<~/indico_api_token.txt)
PROJ=./


TOKEN=$(curl --location --request POST 'https://app.indico.io/auth/users/refresh_token' \
--header "Authorization: Bearer $REFRESH_TOKEN" \
 | jq .auth_token \
 | tr -d '"')


./gradlew -PgraphQlToken=$TOKEN graphqlIntrospectSchema --stacktrace
