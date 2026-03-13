# Ktor Cache-proxy
    https://roadmap.sh/projects/caching-server
## Basic setup
- The app runs using java 21

## How to run
### Application
1. Run the executable file from terminal using `./cache-proxy --port 8080 --host http://power.lowyinstitute.org`
2. Open a browser and make requests through `http://0.0.0.0:8080`
### Gradle task
1. Open the IDE's terminal(Intellij) or navigate to projects route folder `cache-proxy` in terminal
2. Run the gradle task 
`./gradlew run --args='--port 8080 --host http://dummyjson.com'`
3. Open a browser and make requests through `http://0.0.0.0:8080`

Note that the port number will use the one you specified as input.