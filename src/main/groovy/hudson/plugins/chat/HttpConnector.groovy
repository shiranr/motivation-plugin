package hudson.plugins.chat

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.log4j.Level

import java.util.logging.Logger

/**
 * Created by Shiran on 7/6/2014.
 */
class HttpConnector {

    private static final Logger logger = Logger.getLogger(HttpConnector.class.name)

    private String token
    private String roomId
    private String color
    private String message_format
    private boolean notify
    private HTTPBuilder builder

    HttpConnector(String token, String roomId, String color = 'random', String message_format = 'text', boolean notify = true) {
        this.token = token
        this.roomId = roomId
        this.color = color
        this.notify = notify
        this.message_format = message_format
        this.builder = new HTTPBuilder ('https://api.hipchat.com/v2/')
    }

    def sendMessage(String message) {
        builder.uri = "room/${roomId}/notification?auth_token=${token}"
        def jsonContent = [color: this.color, message: message, notify: this.notify, message_format: this.message_format]
        builder.putAt(Method.PUT, ContentType.JSON) {req ->
            body = jsonContent
            response.succuess {resp, json ->
                logger.log(Level.INFO, "A hipchat notification was sent with message $message")
            }

            response.failure {resp ->
                logger.log(Level.ERROR, "Failed to send hipchat notification with message $message and response $resp")

            }
        }

    }

}
