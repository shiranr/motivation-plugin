package hudson.plugins.chat
import hudson.Extension
import hudson.Launcher
import hudson.model.AbstractBuild
import hudson.model.BuildListener
import hudson.tasks.BuildStepMonitor
import hudson.tasks.Notifier
import org.apache.log4j.Level
import org.kohsuke.stapler.DataBoundConstructor

import java.util.logging.Logger
/**
 * Created by Shiran on 7/2/2014.
 */
@Extension
public class ChatNotifier extends Notifier {

    private static final Logger logger = Logger.getLogger(ChatNotifier.class.name)

    private HttpConnector httpConnector

    @DataBoundConstructor
    public ChatNotifier(final String username, final String token, final String roomId, final String color = 'random') {
        super()
        logger.log(Level.INFO, "Starting Chat Notifier")
        httpConnector = new HttpConnector(username, token, roomId, color)
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
        return BuildStepMonitor.NONE

    }


}
