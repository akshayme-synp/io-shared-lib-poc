#! /bin/groovy

package com.synopsys.sigio.util

import groovy.json.JsonBuilder
import java.text.SimpleDateFormat
import com.synopsys.sigio.util.Autoconfigure

def init() {
    config = [:]
    initConfig()

    initDone = true

    return this
}


def loadManifest() {
    template_manifest = [:]
    template_manifest = readYaml(text: libraryResource("Auto-Config/Manifest/io-manifest.yml"))

    return this;    
}


def populateManifest() {
    Autoconfigure autoconfigure = new Autoconfigure()

    // populate and override values
    autoconfigure.populateManifest(template_manifest, config)
    return this;
}


private void setToolAndConnectorInfo() {
//Populate Tool information for licensing checks and for WF Engine
    def toolInfo = template_manifest.tool_information
    for (int i = 0; i < toolInfo.size(); i++) {
        //initialize to avoid NPE
        if (toolInfo[i].fields == null) {
            toolInfo[i].fields = [:]
        }

        def fields = toolInfo[i].fields
        if (toolInfo[i].tool_name == 'blackduck') {
            fields.project_name = BLACKDUCK_PROJECT_NAME + ':' + BLACKDUCK_PROJECT_VERSION
            fields.instance_url = config.toolDetails.blackduckInstanceUrl
            fields.auth_token = config.toolDetails.blackduckAuthToken
        } 
        else if (toolInfo[i].tool_name == 'polaris') {
            fields.project_name = config.gitData.repoOwner + "/" + config.gitData.repoName
            //as Polaris recognizes projects by default this way
            fields.instance_url = config.toolDetails.polarisInstanceUrl
            fields.auth_token = config.toolDetails.polarisAuthToken
        }

        toolInfo[i].fields = fields
    }

    //Populate Connectors info for Workflow Engine feedback
    def connInfo = template_manifest.connectors
    for (int i = 0; i < connInfo.size(); i++) {

        if (connInfo[i].connector_name == 'slack') {
            // withCredentials([string(credentialsId: 'Slack-token', variable: 'SLACK_TOKEN')]) {
            //     template_manifest.connectors[i].fields.bearertoken = SLACK_TOKEN.trim()
            // }
            connInfo.remove(template_manifest.connectors[i])
        } else if (connInfo[i].connector_name == 'jira' ) {
            if(template_manifest.environment.enableJira) {
                template_manifest.connectors[i].fields.projectkey = template_manifest.environment.jiraProjectName
                template_manifest.connectors[i].fields.assignee = config.gitData.committer
                template_manifest.connectors[i].fields.url = config.environment.jiraUrl
                template_manifest.connectors[i].fields.username = config.environment.jiraUserName
                template_manifest.connectors[i].fields.authtoken = config.environment.jiraAuthToken
            }else{
                connInfo.remove(template_manifest.connectors[i])
            }
        }
    }
}

/**
 * HTTP POST to IO-IQ to update the application's manifest.
 * @return updatedManifestJson
 */
def getPrescription() {
    UtilPrint.info("Getting IO prescription")  

    /* Convert to JSON for IO-IQ API call */
    def manifestJson = new JsonBuilder(template_manifest).toPrettyString()
    UtilPrint.debug("Paylod to IO:\n$manifestJson\n")

    def ioUrl = env.IO_URL + '/io/api/manifest/update'
    UtilPrint.debug("API URL: " + ioUrl)

    UtilPrint.sectionSeperator("Start REST API request to IO")
    def response;
    withCredentials([
        string(credentialsId: 'IO-AUTH-TOKEN', variable: 'token')
    ]) {
        response = httpRequest customHeaders: [
            [maskValue: true, name: 'Authorization', value: token]
        ], 
        acceptType: 'APPLICATION_JSON', 
        contentType: 'APPLICATION_JSON', 
        httpMode: 'POST', 
        outputFile: 'responseJson.json', 
        requestBody: manifestJson, 
        responseHandle: 'NONE', 
        url: ioUrl, 
        validResponseCodes: '100:499'
    }

    /*
    The below code checks if status code is greater than 399 (which means there is an error and aborts the build)
     */
    if (response.status > 399) {
        UtilPrint.debug("Request Failed with status code: " + response.status)
        UtilPrint.debug("Message" + response)
        def error = readJSON file: 'responseJson.json'
        UtilPrint.error(error)
        currentBuild.getRawBuild().getExecutor().interrupt(Result.ABORTED)
    }

    UtilPrint.sectionSeperator("End REST API request to IO")

    def updatedManifestJson = readJSON file: 'responseJson.json', returnPojo: true
    UtilPrint.debug("Updated manifest JSON after IO processing:\n$updatedManifestJson\n")

    //Update the config object
    config.security.sast.enabled = updatedManifestJson.security.activities.sast.enabled
    config.security.sca.enabled = updatedManifestJson.security.activities.sca.enabled
    config.totalRiskScore = getTotalRiskScore(updatedManifestJson)
    
    sh 'rm responseJson.json'
    return updatedManifestJson
}

/**
 *
 * @param updatedManifestJson
 * @return
 */
def getTotalRiskScore(updatedManifestJson) {
    def trainingScore = 0

    def bizCritScore = Double.parseDouble(updatedManifestJson.riskScoreCard.bizCriticalityScore.split('/')[1])
    def bizCrit = updatedManifestJson.riskScoreCard.bizCriticalityScore.split('/')[0]
    
    def dataclassScore = Double.parseDouble(updatedManifestJson.riskScoreCard.dataClassScore.split('/')[1])
    def dataclass = updatedManifestJson.riskScoreCard.dataClassScore.split('/')[0]
    
    def accessScore = Double.parseDouble(updatedManifestJson.riskScoreCard.accessScore.split('/')[1])
    def access = updatedManifestJson.riskScoreCard.accessScore.split('/')[0]
    
    def openVulnScore = Double.parseDouble(updatedManifestJson.riskScoreCard.openVulnScore.split('/')[1])
    def openVuln = updatedManifestJson.riskScoreCard.openVulnScore.split('/')[0]
    
    def changeSignificanceScore = Double.parseDouble(updatedManifestJson.riskScoreCard.changeSignificanceScore.split('/')[1])
    def changeSignificance = updatedManifestJson.riskScoreCard.changeSignificanceScore.split('/')[0]
    
    
    config.bizCritScore = bizCritScore
    config.bizCrit = bizCrit
    
    config.dataclassScore = dataclassScore
    config.dataclass = dataclass
    
    config.accessScore = accessScore
    config.access = access
    
    config.openVulnScore = openVulnScore
    config.openVuln = openVuln
    
    config.changeSignificanceScore = changeSignificanceScore
    config.changeSignificance = changeSignificance
    
   

    def totalRiskScore = bizCritScore + dataclassScore + accessScore + openVulnScore + changeSignificanceScore + trainingScore
    UtilPrint.sectionSeperator('Total Risk Score: ' + totalRiskScore)
    
    return totalRiskScore
}

/**
 *
 * @param prescription
 * @return
 */
def updateManifestPostScan(prescription) {

    def scanUpdateJson = libraryResource("scanDateUpdateTemplate.json")
    def scanUpdateTemplateYaml = readJSON text: scanUpdateJson, returnPojo: true
    def sourceActivities = prescription.security.activities
    def templateActivities = scanUpdateTemplateYaml.activities

    scanUpdateTemplateYaml.assetId = prescription.application.assetId
    
    def keySet = [];
    for (entry in templateActivities) {
        def sourceValue = sourceActivities[entry.key]
        if(sourceValue == null || !sourceValue['enabled']) {
            keySet.add(entry.key)
            continue
        }        
    }

    Iterator entriesIterator = keySet.iterator();
    while (entriesIterator.hasNext()) {
        templateActivities.remove(entriesIterator.next().toString());
    }

    for (entry in templateActivities) {
        def sourceValue = sourceActivities[entry.key]
        def date = new Date()
        def sdf = new SimpleDateFormat("yyyy-MM-dd")
        sourceValue['lastScanDate'] = sdf.format(date)
        entry.value = sourceValue
    }

    writeFile file: 'scanUpdate.json', text: new JsonBuilder(scanUpdateTemplateYaml).toPrettyString()

    sh 'cat scanUpdate.json'

    withCredentials([string(credentialsId: 'IO-AUTH-TOKEN', variable: 'token')]) {
        def ioUrl = env.IO_URL + '/io/api/manifest/update/scandate'
        sh 'curl -X POST -H "Content-Type: application/json" -H "Authorization: ' + token + '"  -d @scanUpdate.json ' + ioUrl
    }
}

def checkInit() {
    if (initDone) return true
    init()
}

Map getParams() {
    checkInit()
    return params
}

Map getConfig() {
    checkInit()
    return config
}

def setConfig(String name, value) {
    addToconfig(name,value)
}

def addConfig(String name, value) {
    addToConfig(name,value)
}

def addToConfig(String name, value) {
    config[name]=value
}

def addToConfig(Map newMap) {
    mergeMaps(config, newMap)
}

def getConfig(String name) {
    return config[name]
}

def printConfig() {
    UtilPrint.map(config)
}

Map mergeMaps(Map map1, Map map2) {
    map2.each { k, v ->
        // when both items are maps, attempt to merge them
        if (map1[k] instanceof Map && v instanceof Map) {
            mergeMaps(map1[k], v)
        } else {
            map1[k] = v
        }
    }
    return map1
}

def initConfig() {
    UtilPrint.info('manifest: init config')
    
    withCredentials([
        string(credentialsId: 'Github-AuthToken', variable: 'Scm_AuthToken'), 
        string(credentialsId: 'IO-AUTH-TOKEN', variable: 'IO_AUTH_TOKEN')        
    ]) {
        config.environment = [
            githubToken: env.Scm_AuthToken.trim(),            
            ioAccessToken: IO_AUTH_TOKEN.trim()
        ]
    }

    withCredentials([
        string(credentialsId: 'BlackDuck-AuthToken', variable: 'blackDuck_AuthToken'),
        string(credentialsId: 'polaris-token', variable: 'polaris_token')
    ]) {
        config.toolDetails = [
            blackduckInstanceUrl: env.BLACKDUCK_URL,
            blackduckAuthToken: blackDuck_AuthToken.trim(),
            polarisInstanceUrl: env.POLARIS_URL,
            polarisAuthToken: polaris_token.trim()
        ]
    }

    //Names of the Jenkins nodes that have respective tool installations
    config.dockerNode = 'docker'
    config.mainNode = 'master'
    config.polarisNode = 'master'

    config.application = [
        appType: '',
        microservice: false,
        projectLanguage: 'Java',
        platformVersion: 'jdk-8',
        buildSystem: 'Maven',
        projectName: PROJECTNAME
    ]

    config.security = [
        sast: [
            enabled: false,
            tools: [
                [ toolName: 'Polaris', cliOptions: '', runParallel: false ]
            ]
        ],
        sca: [
            enabled: false, 
            tools: [
                [ toolName: 'Blackduck', cliOptions: '', runParallel: false ]
            ]
        ]
    ]
    
    config.totalRiskScore = 0 //stores calculated risk score from IO manifest update response    
    
    // config.imchannel = 'slack'
    // config.imchannelId = 'C015LGE7RRQ'   

    config.gitData = UtilSCM.getGitRepoInfo()    
}

