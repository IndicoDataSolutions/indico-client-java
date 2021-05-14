#fetch and update the schema
#snap install jq && snap install node
REFRESH_TOKEN=$(<~/indico_api_token.txt)
PROJ=./

TOKEN=$(curl --location --request POST 'https://dev.indico.io/auth/users/refresh_token' \
--header "Authorization: Bearer $REFRESH_TOKEN" \
 | jq .auth_token \
 | tr -d '"')


npx apollo schema:download --endpoint=https://dev.indico.io/graph/api/graphql schema.json --header="Authorization: Bearer $TOKEN"
