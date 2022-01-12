#! /bin/groovy

package com.synopsys.sigio.util

import com.synopsys.sigio.constants.Constants
import com.synopsys.sigio.model.PipelineTool
import com.synopsys.sigio.model.ToolChain
import jenkins.model.Jenkins.*

/**
 * Returns the IP address of the Jenkins node
 * @param nodeName
 * @return String
 */
@NonCPS
def getNodeIpAddress(nodeName) {
    def node = jenkins.model.Jenkins.instance.getNode(nodeName)
    UtilPrint.debug('Node IP Address:' + node.toComputer().launcher.host)
    return node.toComputer().launcher.host
}

/**
 * Calculate the total score from Stargazer's scorecard
 * @param manifestObj
 * @return String
 */
def getTotalRiskScore(manifestObj) {
    def trainingScore = 0

    def bizCritScore = Double.parseDouble(manifestObj.riskScoreCard.bizCriticalityScore.split('/')[1])
    def dataclassScore = Double.parseDouble(manifestObj.riskScoreCard.dataClassScore.split('/')[1])
    def accessScore = Double.parseDouble(manifestObj.riskScoreCard.accessScore.split('/')[1])
    def openVulnScore = Double.parseDouble(manifestObj.riskScoreCard.openVulnScore.split('/')[1])
    def changeSignificanceScore = Double.parseDouble(manifestObj.riskScoreCard.changeSignificanceScore.split('/')[1])
    
    def totalScore = bizCritScore + dataclassScore + accessScore + openVulnScore + changeSignificanceScore + trainingScore
    UtilPrint.sectionSeperator('Total Risk Score: ' + totalScore)
    return totalScore
}

/**
 * Write the coverity_filelist for affected added/edited files & return whether there were any file changes from the Git changelog
 * @return Integer
 */
@NonCPS
def getFileChanges() {
    def changeLogSets = currentBuild.changeSets
    def isChangeExist = 0

    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            UtilPrint.info("${entry.commitId} by ${entry.author} on ${new Date(entry.timestamp)}: ${entry.msg}")

            def files = new ArrayList(entry.affectedFiles)
            for (int k = 0; k < files.size(); k++) {
                def file = files[k]
                UtilPrint.info("${file.editType.name} ${file.path}")

                // Check if the editType is edit or add. Add the file path to file list and return the file list for Incremental scanning.
                def fileListText = ""
                if (file.editType.name == "edit" || file.editType.name == "add") {
                    // Add to file list
                    fileListText = fileListText + file.path + "\n"
                    writeFile(file: "coverity_filelist.txt", text: fileListText)
                    UtilPrint.debug('Change detected:' + file.editType.name)
                    isChangeExist = 1
                    // Archive this file
                    // TODO ?
                }
            }
        }
    }

    return isChangeExist
}

