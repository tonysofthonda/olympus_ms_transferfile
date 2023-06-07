 
# Transfer File Microservice [ms.transferfile]  
This microservice is responsible for retrieving files sotered on the MFTP server. 



How it Works 
1. **ms.transferfile** Receive the message from a *customer http* indicating the dowload of a file.  
2. Verify that the file exists and contains information.  
3. Download a copy to the local host and delete the original from the server MFTP  
4. Notifies the services of the operation**ms.logevent**, **ms.notification** y **ms.processfile**  


Understanding the configuraction  
Relevant data contained in project configuration files and classes:  

service.properties  
Basic Information **ms.transferfile**  
    - Versión  
    - Service context path   
    - Port assigned  
    - URLs of required services: **ms.logevent**, **ms.notification** y **ms.processfile**  

Properties.xml  
- MFTP Server connection data (host, puerto, usuario y password)  
- Remote directory from which downloads will be made (propiedad 'inbound')  
- Directorio local en el que se guardarán los archivos recuperados (property 'destination')  

Validation.properties  
- validation messages returned by the service when it receives requestes with invalid data.  

Application.java  
- Time zone settings


Descriptión of the implementatión  
-Description of main classes:  

Message.java  
- Domain class for messages received from service clients.
- Includes annotation for basic data validation.

MftpConfig.java  
- Loading settings that will be used by MFTP client.   

MftpClient.java  
- Performs low-level operations to interact with MFTP server. 

TransferService.java  
- Check statusestatus and files name received in the message.  
- Generates name to be assigned to the downloaded file.  

MftpService.java  
Contains the businness logic of the application:  
- With successful connection to MFTP MFTP server,  post event**ms.logevent**  
- Connection failed sends event to  **ms.logevent**  
- If the remote file is empty, deletes it from the MFTP server and send an event to **ms.logevent** y **ms.notification**  
- If you can't download file, send event **ms.logevent** y **ms.notification**  
- Download file, delete original on MFTP server send event to **ms.logevent** y envía mensaje a **ms.processfile**  


Running Unit Tests  
- Test *sociable*  
  `$ mvn test -Dtest="LogEventServiceTest1"` with service  **ms.logevent** Stopped in local environment 
  `$ mvn test -Dtest="LogEventServiceTest2"` with service **ms.logevent** Stopped in local environment  
  `$ mvn test -Dtest="NotificationServiceTest1"` with service **ms.notification** Stopped in local environment  
  `$ mvn test -Dtest="NotificationServiceTest2"` with service **ms.notification** Stopped in local environment   
  `$ mvn test -Dtest="ProcessFileServiceTest1"` with service **ms.processfile** Stopped in local environment 
  `$ mvn test -Dtest="ProcessFileServiceTest2"` with service **ms.processfile** Stopped in local environment 
  
- Test *solitary*  
  `$ mvn test`


Running the application  

- Generation of WAR  
  `$ mvn clean package`  

- Execution of JAR  
  `$ mvn spring-boot:run`  


 Use  

Download successful file 

Request  
    curl -X POST -H 'Content-Type: application/json' -d '{"status": 1, "msg": "SUCCESS", "file": "file.txt"}' http://{server-ip}:8082/olympus/transferfile/v1/message  

Response  
    HTTP/1.1 200  
    Content-Type: text/plain;charset=UTF-8  
    Content-Length: 2  
    Date: Tue, 16 May 2023 23:26:55 GMT  
    OK  

File download with disallowed 

Request  
    curl -X POST -H 'Content-Type: application/json' -d '{"status": 0, "msg": "FAIL", "file": "file.txt"}' http://{server-ip}:8082/olympus/transferfile/v1/message  

Response  
    HTTP/1.1 400  
    Content-Type: application/json  
    Transfer-Encoding: chunked  
    Date: Wed, 24 May 2023 08:08:41 GMT  
    Connection: close  

    {"timestamp": "2023-05-24T03:08:41.795-05:00", "status": 400, "error": "Bad Request", "message": "El mensaje tiene un status no aceptado para el proceso Message(status=0, msg=FAIL, file=file1.txt)", "path": "/olympus/transferfile/v1/message"}  


Responses HTTP / Cause  

200 OK  
- The requested file was successfully downloaded

400 Bad Request  
- Attribute **{message.status}** null 
- Attribute  **{message.msg}** nullo or vacum
- The value of the attribute **{message.status}** It's not 1 (SUCCESS)  
- The value of the attribute**{message.status}** is 1 (SUCCESS), bur **{message.file}** in null or empty 

500 Internal Server Error  
- No connection to MFTP server.  
+ No requested file found on MFTP server.  
+ The requested file is empty   
+ Download Failed file.  
