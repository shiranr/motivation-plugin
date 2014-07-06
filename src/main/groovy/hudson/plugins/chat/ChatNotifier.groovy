package hudson.plugins.chat

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import hudson.Extension
import hudson.Launcher
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.BuildListener
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.BuildStepMonitor
import hudson.tasks.Notifier
import hudson.tasks.Publisher
import hudson.util.FormValidation
import net.sf.json.JSONObject
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.QueryParameter
import org.kohsuke.stapler.StaplerRequest

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by Shiran on 7/2/2014.
 */
public class ChatNotifier extends Notifier {

    private static final Logger logger = Logger.getLogger(ChatNotifier.class.name)

    String getToken() {
        descriptor.token
    }

    String getRoom() {
        descriptor.room
    }

    String getColor() {
        descriptor.color
    }

    private static String baseUrl = "https://api.hipchat.com/v2"

    @DataBoundConstructor
    public ChatNotifier() {
        super()
        logger.log(Level.INFO, "Starting Chat Notifier")
    }

    @Override
    public DescriptorImpl getDescriptor() {
        (DescriptorImpl) super.getDescriptor()
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        logger.log(Level.INFO, "Performing Chat Notifier")
        Map jsonContent = getJsonContent(build)
        try {
            String url = "$baseUrl/room/${room}/notification?auth_token=${token}"
            HTTPBuilder builder = new HTTPBuilder(url)
            logger.log(Level.INFO, "Sending request with url ${url} and jsonContent ${jsonContent}")
            builder.request(Method.POST, ContentType.JSON) { req ->
                body = jsonContent
                response.succuess = { resp, json ->
                    logger.log(Level.INFO, "A hipchat notification was sent with message $jsonContent.message")
                }
                response.failure = { resp, json ->
                    logger.log(Level.INFO, "Failed to send hipchat notification with message $jsonContent.message and response ${resp.status} - ${json}")
                }
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Failed to send hipchat notification with message $jsonContent.message", e)
        }
        return true
    }

    private LinkedHashMap<String, Serializable> getJsonContent(AbstractBuild<?, ?> build) {
        [color: color, notify: true, message_format: 'text', message: "Build $build.displayName finished with status $build.result".toString()]
    }

    public BuildStepMonitor getRequiredMonitorService() {
        BuildStepMonitor.NONE
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private static final Logger logger = Logger.getLogger(DescriptorImpl.class.name)

        private String token
        private String room
        private String color


        String getColor() {
            color ?: 'random'
        }

        String getRoom() {
            room
        }

        String getToken() {
            token
        }


        @Override
        boolean isApplicable(Class<? extends AbstractProject> jobType) {
            true
        }

        @Override
        String getDisplayName() {
            'Chat Notifications'
        }

        public FormValidation doCheckToken(@QueryParameter String value) {
            (value) ? FormValidation.ok() : FormValidation.error("Invalid token")
        }

        public FormValidation doCheckRoom(@QueryParameter String value) {
            (value) ? FormValidation.ok() : FormValidation.error("Invalid room id")
        }

        public DescriptorImpl() {
            logger.log(Level.INFO, "Starting descriptor Impl.")
            load()
        }

        @Override
        public boolean configure(StaplerRequest sr, JSONObject formData) {
            token = formData.getString("hipChatToken")
            room = formData.getString("hipChatRoom")
            color = formData.getString("hipChatColor")
            save()
            super.configure(sr, formData)
        }

    }


}
