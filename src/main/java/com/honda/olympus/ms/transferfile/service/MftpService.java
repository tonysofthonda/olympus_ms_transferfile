package com.honda.olympus.ms.transferfile.service;

import static com.honda.olympus.ms.transferfile.domain.Status.SUCCESS;
import static com.honda.olympus.ms.transferfile.domain.Status._FAIL;
import static com.honda.olympus.ms.transferfile.domain.Status._SUCCESS;
import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.honda.olympus.ms.transferfile.client.MftpClient;
import com.honda.olympus.ms.transferfile.client.MftpConfig;
import com.honda.olympus.ms.transferfile.domain.Event;
import com.honda.olympus.ms.transferfile.domain.Message;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Setter
@Service
public class MftpService {

    private static final String MSG_CONNECTION_OK = "Conexión al MFTP %s, %s fue exitosa";
    private static final String MSG_CONNECTION_ERROR = "112 Fallo de conexión al sitio MFTP. No se puede obtener la información correctamente del MFT, favor de revisar";
    private static final String MSG_SEARCH_ERROR = "El archivo %s, no encontrado en la ubicación %s";
    private static final String MSG_INFO_ERROR = "El archivo %s no tiene información";
    private static final String MSG_DOWNLOAD_ERROR = "Fallo al momento de realizar la copia del archivo %s, a la siguiente ubicación definida %s";

    @Autowired
    private MftpConfig config;
    @Autowired
    private LogEventService logEventService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ProcessFileService processFileService;

    @Value("${service.name}")
    private String serviceName;

    @Value("${mftp.timewait}")
    private int timeoutSftpClient;

    public void downloadFile(String fileName, String newFileName) {
        MftpClient client = new MftpClient(config, fileName, newFileName);
        downloadFile(client, fileName, newFileName);
    }

    public void downloadFile(MftpClient client, String fileName, String newFileName) {
        // connect to mftp server
        if (!client.open()) {
            Event event = connectionErrorEvent(fileName);
            logEventService.logEvent(event);
            notificationService.sendNotification(event);

            log.info("Transferfile:: Notification & Log event sent");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, event.getMsg());
        }
        logEventService.logEvent(connectionOkEvent());

        CountDownLatch latch = new CountDownLatch(1);
        Thread downloadThread = new Thread(() -> {
            try {
                // search file
                if (!client.fileExists()) {
                    client.close();
                    Event event = searchErrorEvent(fileName);
                    logEventService.logEvent(event);

                    log.info("Transferfile:: Log event sent");
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, event.getMsg());
                }

                // verify if empty
                if (client.isFileEmtpy()) {
                    client.deleteFile();
                    client.close();
                    Event event = infoErrorEvent(fileName);

                    logEventService.logEvent(event);
                    notificationService.sendNotification(event);

                    log.info("Transferfile:: Notification & Log event sent");
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, event.getMsg());
                }

                // download file
                if (client.downloadFile()) {
                    client.deleteFile();
                    client.close();
                    logEventService.logEvent(downloadOkEvent(newFileName));
                    processFileService.processFile(downloadOkMessage(newFileName));

                    log.info("Transferfile:: Processfile & Log event sent");
                } else {
                    client.close();
                    Event event = downloadErrorEvent(fileName, newFileName);
                    logEventService.logEvent(event);
                    notificationService.sendNotification(event);

                    log.info("Transferfile:: Notification & Log event sent");
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, event.getMsg());
                }
            } finally {
                client.close();
                latch.countDown();
            }
        });

        downloadThread.start();

        try {
            boolean completed = latch.await(timeoutSftpClient, TimeUnit.MILLISECONDS);
            if (!completed) {
                downloadThread.interrupt();
                Event event = downloadErrorEvent(fileName, newFileName);
                logEventService.logEvent(event);
                notificationService.sendNotification(event);
            }
        } catch (InterruptedException e) {
            Event event = downloadErrorEvent(fileName, newFileName);

            logEventService.logEvent(event);
            notificationService.sendNotification(event);
        }
    }

    private Event connectionOkEvent() {
        return new Event(serviceName, _SUCCESS, format(MSG_CONNECTION_OK, config.getHost(), config.getPort()), "");
    }

    private Event connectionErrorEvent(String fileName) {
        return new Event(serviceName, _SUCCESS, MSG_CONNECTION_ERROR, fileName);
    }

    private Event searchErrorEvent(String fileName) {
        return new Event(serviceName, _FAIL, format(MSG_SEARCH_ERROR, fileName, config.getInbound()), fileName);
    }

    private Event infoErrorEvent(String fileName) {
        return new Event(serviceName, _FAIL, format(MSG_INFO_ERROR, fileName), fileName);
    }

    private Event downloadOkEvent(String newFileName) {
        return new Event(serviceName, _SUCCESS, SUCCESS, newFileName);
    }

    private Message downloadOkMessage(String newFileName) {
        return new Message(_SUCCESS, SUCCESS, newFileName);
    }

    private Event downloadErrorEvent(String fileName, String newFileName) {
        return new Event(serviceName, _FAIL, format(MSG_DOWNLOAD_ERROR, newFileName, config.getDestination()),
                fileName);
    }


}
