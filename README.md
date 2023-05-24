
# Transfer File Microservice [ms.transferfile]  

Este microservicio se encarga de recuperar los archivos almacenados en el servidor MFTP.  


## Cómo funciona  

1. **ms.transferfile** recibe el mensaje de un *cliente http* indicando la descarga de un archivo  
2. Verifica que el archivo exista y que contenga información  
3. Descarga una copia en el host local y elimina la original del servidor MFTP  
4. Notifica la operación realizada a los servicios **ms.logevent**, **ms.notification** y **ms.processfile**  


## Descripción de la configuración  

Datos relevantes contenidos en los archivos y clases de configuración del proyecto:  

#### application.properties  

+ Incluye ruta de contexto del servicio (`/olympus/transferfile/v1`)  
+ Puerto utilizado (`8082`)  
+ Importa archivo *service.yml*

#### service.yml  

+ Incluye información de conexión a los servicios externos **ms.logevent**, **ms.notification** y **ms.processfile**  
    - URLs en la nube (para ambiente de desarrollo)  
    - Rutas de contexto (paths)  
    - Puertos que utilizan  

+ IP del servidor MFTP en la nube (para ambiente de desarrollo)  

#### properties.xml  

+ Contiene datos de conexión al servidor MFTP (host, puerto, usuario y password)  
+ Incluye directorio remoto desde donde se realizarán las descargas (propiedad 'inbound')  
+ Incluye directorio local en el que se guardarán los archivos recuperados (propiedad 'destination')  

#### validation.properties  

+ Contiene los mensajes de validación que retorna el servicio cuando recibe peticiones con datos inválidos  

#### Application.java  

+ Importa archivo *properties.xml*  
+ Incluye configuración de zona horaria  
+ Importa archivo *validation.properties*  


## Descripción de la implementación  

Descripción de clases principales:  

#### Message.java  

+ Clase de dominio para mensajes recibidos de los clientes del servicio
+ Incluye anotaciones para validación básica de datos

#### MftpConfig.java  

+ Carga configuración que utilizará el cliente MFTP   

#### MftpClient.java  

+ Realiza operaciones de bajo nivel para interactuar con servidor MFTP  

#### TransferService.java  

+ Verifica estatus y nombre de archivo recibidos en el mensaje  
+ Genera nombre que se asignará al archivo descargado  

#### MftpService.java  

+ Contiene la lógica de negocio de la aplicación  
+ Con conexión exitosa a servidor MFTP, envía evento a **ms.logevent**  
+ Con conexión fallida envía evento a **ms.logevent** y **ms.notification**  
+ Si no encuentra archivo remoto, envía evento a **ms.logevent**  
+ Si archivo remoto esta vacío, lo borra del servidor MFTP y envía evento a **ms.logevent** y **ms.notification**  
+ Si no puede descargar archivo, a envía evento a **ms.logevent** y **ms.notification**  
+ Si puede descargar archivo, elimina original en servidor MFTP, envía evento a **ms.logevent** y envía mensaje a **ms.processfile**  


## Ejecución de pruebas unitarias  

+ Pruebas *sociables*  
  `$ mvn test -Dtest="LogEventServiceTest1"` *con servicio **ms.logevent** detenido*  
  `$ mvn test -Dtest="LogEventServiceTest2"` *con servicio **ms.logevent** en ejecución*  
  `$ mvn test -Dtest="NotificationServiceTest1"` *con servicio **ms.notification** detenido*  
  `$ mvn test -Dtest="NotificationServiceTest2"` *con servicio **ms.notification** en ejecución*  
  `$ mvn test -Dtest="ProcessFileServiceTest1"` *con servicio **ms.processfile** detenido*  
  `$ mvn test -Dtest="ProcessFileServiceTest2"` *con servicio **ms.processfile** en ejecución*  
  
+ Pruebas *solitarias*  
  `$ mvn test`


## Ejecución de la aplicación  

+ Generación de WAR  
  `$ mvn clean package`  

+ Ejecución de JAR  
  `$ mvn spring-boot:run`  


## Uso  

#### Request  
    curl -X POST -H 'Content-Type: application/json' -d '{"status": 1, "msg": "SUCCESS", "file": "file.txt"}' http://{server-ip}:8082/olympus/transferfile/v1/message  

#### Response  
    HTTP/1.1 200  
    Content-Type: text/plain;charset=UTF-8  
    Content-Length: 2  
    Date: Tue, 16 May 2023 23:26:55 GMT  

    OK  

## Respuestas HTTP / Causa  

#### 200 OK  
+ El archivo solicitado se descargó correctamente  

#### 400 Bad Request  
+ Atributo **{message.status}** nulo  
+ Atributo **{message.msg}** nulo o vacío  
+ El valor del atributo **{message.status}** no es 1 (SUCCESS)  
+ El valor del atributo **{message.status}** es 1 (SUCCESS), pero **{message.file}** es nulo o vacío  

#### 500 Internal Server Error  
+ Sin conexión a servidor MFTP  
+ No se encuentra archivo solicitado en servidor MFTP  
+ El archivo solicitado está vacío  
+ Descarga fallida de archivo  
