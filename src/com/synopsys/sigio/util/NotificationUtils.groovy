#! /bin/groovy

package com.synopsys.sigio.util

def sendNotification(manifestObj, color, message, imChannel) {
    UtilPrint.info("selected channel to send notification: " + imChannel)
    if(imChannel == null) {
        return;
    }

    switch(imChannel.toUpperCase()) {
        case "SLACK":
            slackSend color: color, message: message
            break;
        case "MSTEAMS":
            // https://plugins.jenkins.io/Office-365-Connector/
            UtilPrint.warning("MS Teams support is not available currently for synopsys environment")
            office365ConnectorSend message: message, status: color, webhookUrl: env.OFFICE_365_CONNECTOR_URL
            break;
        default:
            // no notifications
            break;
    }
}