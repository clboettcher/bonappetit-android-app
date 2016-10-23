The API mock is a small standalone application that mocks the backend API using
wiremock (basic URL matching to static content).

Config:
* Port: 8888

Place the request mappings under ./mappings
Place the static response files under ./__files

Start the ./wiremock-standalone-XXX.jar. Change the port using the program argument --port 8888.
See .idea/runConfigurations if you are using intellij.