package hudson.plugins.chat
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

    private HttpConnector httpConnector

    @DataBoundConstructor
    public ChatNotifier(final String token, final String roomId, final String color) {
        super()
        logger.log(Level.INFO, "Starting Chat Notifier")
        httpConnector = new HttpConnector(token, roomId, color)
    }

    @Override
    public DescriptorImpl getDescriptor() {
        (DescriptorImpl) super.getDescriptor()
    }

    public ChatNotifier() {
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        String message = "Build $build.displayName finished with status $build.result"
        if (httpConnector) {
            try {
                httpConnector.sendMessage(message)
            } catch (Exception e) {
                logger.log(Level.ERROR, "Failed to send hipchat notification with message $message", e)
            }
        }
        return true
    }

    public BuildStepMonitor getRequiredMonitorService() {
        BuildStepMonitor.NONE
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        String token
        String room
        String color


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
            load()
        }

        @Override
        public ChatNotifier newInstance(StaplerRequest sr) {
            if (token == null) token = sr.getParameter("hipChatToken")
            if (room == null) room = sr.getParameter("hipChatRoom")
            color = sr.getParameter("hipChatColor")
            new ChatNotifier(token, room, color)
        }

        @Override
        public boolean configure(StaplerRequest sr, JSONObject formData){
            token = sr.getParameter("hipChatToken")
            room = sr.getParameter("hipChatRoom")
            color = sr.getParameter("hipChatColor")
            try {
                new ChatNotifier(token, room, color)
            } catch (Exception e) {
               throw e
            }
            save()
            super.configure(sr, formData)
        }

    }


}
