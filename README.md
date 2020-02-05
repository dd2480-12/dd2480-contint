
## Statement of contributions

## Importing Project to IDE

### IntelliJ
1. Start IntelliJ and close any open projects (`File > Close project`). 
2. From the Welcome screen, click `Import Project` and navigate to this repository, select its root and press OK. 
3. Select `Import project from external model` and select `Maven`. 
4. Check `Import Maven projects automatically` and click Next.
5. Select the project _DD2480-Group12-A1:1.0_ and click Next. 
6. Select SDK, click Next.
7. Optionally change naming/location,  click Finish. 

### Eclipse 
1. Go to `File > Import...`
2. Select `Maven > Existing Maven Projects`
3. Navigate to this repository, select its root, check the project (/pom.xml) and click Finish

## Run the program

 Clone the repository

```
git clone https://github.com/dd2480-12/dd2480-contint.git 
```
Download & setup ngrok
```
https://ngrok.com/download
```
Run continuous integration server
```
gradle build
gradle run
```
Run ngrok  and make CI server visible on the internet 
 
```
./ngrok http 8080
```

We configure our Github repository:

-   go to  `Settings >> Webhooks`, click on  `Add webhook`.
-   paste the forwarding URL (eg  `http://8929b010.ngrok.io`) in field  `Payload URL`) and send click on  `Add webhook`. In the simplest setting, nothing more is required.

To test that everything works:
- make a commit in branch Assessment
- check the commit history on Github and the status of the test and URL should be visible.

## Compilation and Testing
### Implementation
Once there is a new commit on remote repository, webhook is triggered and name of newly committed repository is passed as a value to CI server. Before compiling it, the repository is cloned to local device using Jgit API. The repository is stored in a directory of which the name is generated randomly.  The cloned repository will be executed by ``` gradle build ``` and then the result is stored in **build.json**. The result of the test, commit SHA and url are included in **build.json**.

### Unit-tested


## Notification

### Implementation

### Unit-tested

## Extra Feature
### Build list URL

