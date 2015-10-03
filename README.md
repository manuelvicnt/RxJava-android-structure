# RxJava Android Structure

Implementation of the structure explained in this blog in Medium
https://medium.com/@manuelvicnt/rxjava-android-mvvm-app-structure-with-retrofit-a5605fa32c00

How to use RxJava in a organised way with Retrofit as the networking library.

### Happy Path
There are some things to have into account when exploring the project:
- RegistrationAPIService throws more than one exception for different networking errors (all are handled in the RegistrationFragment).
- RegistrationAPIService gets the status code from a failure response.
- LoginAPIService gets the status code from a successful response.
- After Registration, we're going to log the user in.
- After Login, we're going to get the user data.
- We allow Pull to refresh in the Home Activity (this means we will have to create different Subjects/Subscriber per request).
- We process the user data information when both request come. If one of them fails, we don't want to update the data.

### Set up
When the registration is successful, it stores a value in SharedPreferences (that's how it goes to Login or Registration). To force registration, you can uninstall the app and install it again or clear cache data in the App info (Settings > Apps > Select App).

### Unit Testing
RegistrationAPIService is tested. The rest of the classes can be tested in the same way. Be aware of threading problems.

### Sad Paths
You can change the response of the network request with the files stored in the assets folder. Change the 200 response for a 400, for example. That gives you the ability of exploring different sad paths.

One good example would be: Change the accountResponse.txt to fail (400 response instead of 200). Run the app from registration, it will fail in the account response and the login screen will appear.
