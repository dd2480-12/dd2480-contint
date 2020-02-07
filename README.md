This is a CI server, aimed at making sure a commit passes all tests before you merge with master!


## Statement of contributions

### Alex Diaz

Build log website server, creating build logs, documentation

### Dingli Mao

README, logic for GitHandler

### Jesper Lundholm

Mock tests, refactor CIServer, implement Notifier

### Joar Ekelund

Part of the inital CI server, payload class, logic for githandler


## Run the program

Clone the repository

```
git clone https://github.com/dd2480-12/dd2480-contint.git 
```
Download & setup ngrok
```
https://ngrok.com/download
```
Run continuous integration server in the root folder
```
gradle build
gradle run
```
Run ngrok and make CI server visible on the internet 
 
```
./ngrok http 8080
```

We configure our Github repository:

-   go to  `Settings >> Webhooks`, click on  `Add webhook`.
-   paste the forwarding URL from ngrok (eg  `http://8929b010.ngrok.io`) in field  `Payload URL`) and send click on  `Add webhook`. In the simplest setting, nothing more is required.

To test that everything works:
- make a commit in branch Assessment
- check the commit history on Github and the status of the test and URL should be visible.

## Compilation and Testing
### Implementation
Once there is a new commit on remote repository, webhook is triggered and name of newly committed repository is passed as a value to CI server. Before compiling it, the repository is cloned to local device using Jgit API. The repository is stored in a directory of which the name is generated randomly.  The cloned repository will be executed by ``` gradle build ``` and then the result is stored in **build.json**. The result of the test, commit SHA and url are included in **build.json**.

### Unit testing

Compilation and testing are unit tested with Mockito. We create a mock request object with a mock payload json. We create a mock status and return values, and assert that each value is correct after processing. If there are no errors, that means the program flow has executed correctly.

## Notification
### Implementation

Github is connected and authenicated by using a token. The status and URL of the build will be sent to Github once there is a new commit. The status is either success or failed. 

### Unit testing

Notifications are tested by mocking a status object and making it into a commit status object. We assert that the values of the commit status object correspondes to the values of the status object.

## Extra Feature, Log history website

To start the Node server, run the following commands.

```
cd dist
node index.js
```

### Build list URL
The website will now be accessible at [localhost:3000](localhost:3000).
