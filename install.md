1. Install MySQL Server (currently running on 5.7.10)
2. Install Java 8
3. Install tomcat 8
4. Install maven 2
5. Change settings in jdbc.properties
6. Turn on 2 step verification for sender email https://support.google.com/accounts/answer/185839?hl=en
7. Run SQL Scripts to create database \paygo\src\scripts\paygo_schema_creation.sql
8. In jdbc.properties change google.client.id for google token.
9. Build war from console.

     a. run console

     b. step into directory with pom.xml

     c. run command : mvn package
10. deploy on tomcat:

    a. copy war file to \apache-tomcat-8.0.30\webapps\

    b. add  in   \apache-tomcat-8.0.30\conf\server.xml
     ```html
     <Context path="" docBase="paygo" debug="0" reloadable="true"></Context>
     ```

    in tag
     ```html
     <Host> </Host>
     ```
    c.. from apache-tomcat-8.0.30\bin\ run command: startup.bat

11. visit http://localhost:8080/index.html



**email sender configuration:**

For use email sender you should configure properties:

|property                  | explanation|
|:------------------------ |:---|
|mail.sender               | sender email (for example natali.borshcheva@gmail.com)|
|mail.sender.pass          | sender application password. You could get it from google account if you are using 2-way verification|
|mail.subject              |Subject of an email|
|mail.smtp.port            |google smtp port. default value=587|
|mail.smtp.auth            |google smtp authentication default value=true|
|mail.smtp.starttls.enable |enable/disable TSL default value=true|
|mail.body.content.type    |body content type. default value=text/html|
|mail.body.file.name       |path to body template. default value=/basic.html|

To configure body template you can use following parameters in body template:

|parameter           | explanation|
|:------------------ |:-----------|
| %company_name%     |Company name|
| %firstname%        |User's first name|
| %lastname%         |User's last name|
| %order_date%       |Date of order|
| %experian_bin%     |Experian BIN|
| %card_type%        |Debit/credit card type|
| %card_expire%      |Card expiration date|
| %payment_id%       |Payment ID|
| %order_ip_address% |User's IP address order came from|
| %order_total%      |Order total in $|