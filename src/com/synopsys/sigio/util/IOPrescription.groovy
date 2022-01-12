#! /bin/groovy

package com.synopsys.sigio.util

/**
 * Print Stargazer prescription
 */
def printPrescription(ioConfig) {
    NotificationUtils noteUtil = new NotificationUtils()

    def scoreMessage = "==========Start IO Prescription for : " + ioConfig.gitData.repoName + "==========\n" + "Total risk score: " + ioConfig.totalRiskScore + "\n\n"+getChangeSignificance(ioConfig) + getOpenVulnRisk(ioConfig) +  getBizCriticality(ioConfig) + getDataClassification(ioConfig) + getAccessibility(ioConfig)
    def message = ""
    if (ioConfig.security.sast.enabled) {
        message += "SAST is enabled with " + ioConfig.security.sast.tools[0].toolName + ".\n"
    } else {
        message += "SAST is disabled.\n"
    }

    //noteUtil.sendNotification(manifestObj, "good", message)

    if (ioConfig.security.sca.enabled) {
        message += "SCA is enabled with " + ioConfig.security.sca.tools[0].toolName + ".\n"
    } else {
        message += "SCA is disabled.\n"
    }
    //noteUtil.sendNotification(manifestObj, "good", message)
 
    message = scoreMessage + "\n\n" + message + "\n==========End IO Prescription for : " + ioConfig.gitData.repoName + "=========="
    UtilPrint.info(message)

    //noteUtil.sendNotification(manifestObj, "good", message, imchannel)
    //UtilPrint.sectionSeperator("End Stargazer Prescription for : " + manifestObj.application.appName)
}

/**
 *
 * @param manifestObj
 * @return
 */
def getChangeSignificance(ioConfig) {
    def message = "Change Significance Score: " +  ioConfig.changeSignificanceScore + " and Change Significance is " + ioConfig.changeSignificance + "\n"
    return message
}

/**
 *
 * @param manifestObj
 * @return
 */
def getOpenVulnRisk(ioConfig) {
    def message = "Open Vulnerabilities Score: " +  ioConfig.openVulnScore + " and Risk of open vulnerabilities is " + ioConfig.openVuln + "\n"
    return message
}

/**
 *
 * @param manifestObj
 * @return
 *  riskScoreCard:[bizCriticalityScore:High/7.5, dataClassScore:Restricted/11.25, accessScore:Internet/15.0, openVulnScore:High/7.5, changeSignificanceScore:Low/4.0, trainingScore:null]]
 */
def getBizCriticality(ioConfig) {
    def message = "Business Criticality Score: " +  ioConfig.bizCritScore + " and Business criticality is " + ioConfig.bizCrit + "\n"
    return message
}

/**
 *
 * @param manifestObj
 * @return
 */
def getDataClassification(ioConfig) {
    def message = "Data Classification Score: " +  ioConfig.dataclassScore + " and Data Classification is " + ioConfig.dataclass + "\n"
    return message
}

/**
 *
 * @param manifestObj
 * @return
 */
def getAccessibility(ioConfig) {
    def message = "Accessibility Score: " +  ioConfig.accessScore + " and Accessibility is " + ioConfig.access + "\n"
    return message
}
