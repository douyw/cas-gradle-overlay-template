## Setup cas:

1. run ./bin/bootstrap 

2. add to /etc/hosts file: 127.0.0.1    devcas.infusiontest.com

3. ./gradlew clean build

4. run cas server using "java -jar cas/build/libs/cas.war --cas.standalone.config=etc/cas/config"

5. INSERT INTO cas.user VALUES (1, 1, 'your-first-name', 'your-last-name', null, null, 'youre-email')

6. INSERT INTO cas.user_authority VALUES (1,2);

7. Go to https://devcas.infusiontest.com:7443

8. Click 'Forgot Your Password?'

9. Get recovery code from the logs or from the database with: SELECT password_recovery_code FROM cas.user;

10. Enter recovery code and create new password 


## Configure Infusionsoft to use cas:

1. Update your infusionsoft User.GlobalUserId field: UPDATE User SET GlobalUserId=1 WHERE Id=~yourUserId~;

2. Run flagship with the cas profile: 
    mvn tomcat6:run -pl webapp -P cas
    
3. Login with cas credentials

# You did it!