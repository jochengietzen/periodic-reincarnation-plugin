package org.jenkinsci.plugins.periodicreincarnation;

import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.kohsuke.stapler.DataBoundConstructor;

import antlr.ANTLRException;
import hudson.AbortException;
import hudson.scheduler.CronTab;

/**
 * Abstract Class for the periodic Triggers. Like Regular Expression or Build Failure Cause.
 * 
 * @author Jochen Gietzen
 *
 */
public abstract class PeriodicTrigger {

    /**
     * Logger for PeriodicReincarnation.
     */
    private static final Logger LOGGER = Logger.getLogger(PeriodicTrigger.class.getName());

    /**
     * Value of the periodic trigger as String.
     */
    public String value;
    
    /**
     * Description for this periodic trigger.
     */
    public String description;
    /**
     * Cron time format for this periodic trigger. Overrides the globally configured cron
     * time if set.
     */
    public String cronTime;
    /**
     * Script for node.
     */
    public String nodeAction;
    /**
     * Script for master.
     */
    public String masterAction;

    /**
     * Constructor. Creates a periodic trigger.
     * 
     * @param value
     *            the value of the specific class - like a regex or failure id.
     * @param description
     *            periodic trigger description
     * @param cronTime
     *            cron time format.
     * @param nodeAction
     *            node script.
     * @param masterAction
     *            master script
     */
    @DataBoundConstructor
    public PeriodicTrigger(String value, String description, String cronTime, String nodeAction, String masterAction) {
        this.value = value;
        this.description = description;
        this.cronTime = cronTime;
        this.nodeAction = nodeAction;
        this.masterAction = masterAction;
    }

    /**
     * Returns this periodic trigger value.
     * 
     * @return the periodic trigger value.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Returns a description for this periodic trigger.
     * 
     * @return Description as String.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the cron time for this particular periodic trigger.
     * 
     * @return crontime as String.
     */
    public String getCronTime() {
        return this.cronTime;
    }

        /**
     * Returns the node script.
     * 
     * @return the script as String.
     */
    public String getNodeAction() {
        return this.nodeAction;
    }

    /**
     * Returns the master script.
     * 
     * @return the script as String.
     */
    public String getMasterAction() {
        return this.masterAction;
    }

    /**
     * Checks if the current time corresponds to the cron tab configured for
     * this value. If such cron tab is missing or could not be parsed then the
     * global cron tab is used.
     * 
     * @param currentTime
     *            current time from System
     * @return true if global cron time covers the current time, false
     *         otherwise.
     */
    public boolean isTimeToRestart(long currentTime) {
        CronTab valCronTab = null;
        CronTab globalExCronTab = null;
        try {
            if (this.getCronTime() != null) {
                valCronTab = new CronTab(this.getCronTime());
            }
        } catch (ANTLRException e) {
            LOGGER.fine("val cron tab could not be parsed or is empty! Trying to use global instead...");
        }
        try {
            if (PeriodicReincarnationGlobalConfiguration.get().getCronTime() != null) {
                globalExCronTab = new CronTab(PeriodicReincarnationGlobalConfiguration.get().getCronTime());
            }
        } catch (ANTLRException e) {
            LOGGER.fine("Global cron tab could not be parsed!");
        }
        // if the valCronTab is available use it, if not go with the
        // global cron time.
        if (valCronTab != null) {
            return valCronTab.ceil(currentTime).getTimeInMillis() - currentTime == 0;
        }
        if (globalExCronTab != null) {
            return globalExCronTab.ceil(currentTime).getTimeInMillis() - currentTime == 0;
        }
        return false;

    }

}

