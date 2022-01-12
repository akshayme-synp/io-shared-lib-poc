// Copyright (c) 2021 Synopsys, Inc. All rights reserved worldwide.

package com.synopsys.sigio.util

import com.synopsys.sigio.constants.Constants

def downloadWFClient(String filename = 'WorkflowClient.jar'){
    // Download it always, as versions might change and we don't know if last download was successful
    // and there is no checksum to rely on from the server, github user content is hosted on amazon s3 but
    // that's an implementation detail. We may change the hosting.

    //see if workflow client URL is explicitly set
    if(env.WORKFLOW_CLIENT_URL?.trim()){
        download(env.WORKFLOW_CLIENT_URL, filename)
    } else {
        download(Constants.WORKFLOW_CLIENT_DEFAULT_URL_TEMPLATE, filename)
    }
}

// def downloadJavaSeekerAgent(String projectKey = 'default') {
//     def String javaAgentDownloadPath = env.SEEKER_URL?.trim() + "/rest/api/latest/installers/agents/scripts/JAVA?osFamily=LINUX\\&downloadWith=wget\\&projectKey=" + projectKey
//     def String agentDownloadScript = "${pwd()}/seeker.sh "
//     def Integer response = sh(script: ' wget --no-check-certificate ' + javaAgentDownloadPath + ' -O ' + agentDownloadScript , returnStdout: true,returnStatus: true)
//     if (response != 0 ) {
//         UtilPrint.error("Error:  Unable to download Seeker Agent! We don't have project key: ${projectKey} in Seeker. Check your configuration to proceed")
//         return false;
//     } else {
//         sh " chmod +x " + agentDownloadScript
//         sh agentDownloadScript
//         return true
//     }
// }

def download(String url, String path){
    sh 'wget --no-check-certificate ' + url + ' -O ' + path
}
