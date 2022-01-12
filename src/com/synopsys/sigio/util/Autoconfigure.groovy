package com.synopsys.sigio.util

import groovy.json.JsonBuilder

/**
* HTTP POST to IO to auto-onboard the risk matrix.
*/
// void autoOnboardRiskMatrix(revision) {
//     def risk_template = [:]
//     UtilPrint.info("Auto-onboard application to IO")

//     //if not found, load the default template as per risk and create the risk matrix
//     if (revision == 'high-risk') {
//         risk_template = readYaml(text: libraryResource("Auto-Config/RiskMatrix/HighRisk-RiskMatrix.yml"))
//     } else if (revision == 'medium-risk') {
//         risk_template = readYaml(text: libraryResource("Auto-Config/RiskMatrix/MediumRisk-RiskMatrix.yml"))
//     } else if (revision == 'low-risk') {
//         risk_template = readYaml(text: libraryResource("Auto-Config/RiskMatrix/LowRisk-RiskMatrix.yml"))
//     }

//     def riskJson = new JsonBuilder(risk_template).toPrettyString()
//     UtilPrint.debug("Paylod to IO:\n$riskJson\n")

//     starUrl = env.IO_URL + '/io/api/calculator/update'
//     UtilPrint.debug("API URL: " + starUrl)
//     withCredentials([string(credentialsId: 'IO-AUTH-TOKEN', variable: 'token')]) {
//         httpRequest customHeaders: [
//             [maskValue: true, name: 'Authorization', value: token]
//         ], 
//         contentType: 'APPLICATION_JSON', 
//         httpMode: 'POST', 
//         requestBody: riskJson, 
//         responseHandle: 'NONE', 
//         url: starUrl
//     }

// }

/**
* Populate template
*/
static void populateManifest(template_manifest, config) {

    template_manifest.application.projectName = config.application.projectName

    template_manifest.environment.scm = config.gitData.scmName
    template_manifest.environment.scmOwner = config.gitData.repoOwner
    template_manifest.environment.scmRepositoryName = config.gitData.repoName
    template_manifest.environment.scmBranchName = config.gitData.gitBranch
    template_manifest.environment.githubToken = config.environment.githubToken
}
